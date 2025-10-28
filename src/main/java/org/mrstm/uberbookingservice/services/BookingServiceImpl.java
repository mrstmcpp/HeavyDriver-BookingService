package org.mrstm.uberbookingservice.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.mrstm.uberbookingservice.apis.SocketApi;
import org.mrstm.uberbookingservice.dto.*;
import org.mrstm.uberbookingservice.dto.BookingStateDto.UpdatingStateDto;
import org.mrstm.uberbookingservice.exceptions.AccessDeniedException;
import org.mrstm.uberbookingservice.models.Location;
import org.mrstm.uberbookingservice.repositories.*;
import org.mrstm.uberbookingservice.states.*;
import org.mrstm.uberbookingservice.utils.TimeUtils;
import org.mrstm.uberentityservice.dto.booking.*;
import org.mrstm.uberentityservice.models.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final OtpRepository otpRepository;
    private final DriverRepository driverRepository;
    private final SocketApi socketApi;
    private final KafkaService kafkaService;
    private final RedisService redisService;
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;


    public BookingServiceImpl(BookingRepository bookingRepository,
                              PassengerRepository passengerRepository, OtpRepository otpRepository,
                              DriverRepository driverRepository ,
                              SocketApi socketApi, KafkaService kafkaService, RedisService redisService, IdempotencyRepository idempotencyRepository, ObjectMapper objectMapper) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.otpRepository = otpRepository;
        this.driverRepository = driverRepository;
        this.socketApi = socketApi;
        this.kafkaService = kafkaService;
        this.redisService = redisService;
        this.idempotencyRepository = idempotencyRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public CreateBookingResponseDto createBooking(CreateBookingRequestDto bookingDetails , String idempotencyKey) {
        try{
            Optional<IdempotencyRecord> existing = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return objectMapper.readValue(existing.get().getResponseData(), CreateBookingResponseDto.class);
            }

            Passenger p = passengerRepository.findById(bookingDetails.getPassengerId()).orElseThrow(() -> new RuntimeException("Passenger not found with id: " + bookingDetails.getPassengerId()));
            if(p.getActiveBooking() != null){
                System.out.println(p.getId() + " " + p.getPassanger_name());
                throw new IllegalArgumentException("Passenger already have active booking.");
            }
            Booking booking = Booking.builder()
                    .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                    .startLocation(bookingDetails.getStartLocation())
                    .endLocation(bookingDetails.getEndLocation())
                    .passenger(p)
                    .build();
            System.out.println("here");

            Booking newBooking = bookingRepository.save(booking);
            NearbyDriversRequestDto req = NearbyDriversRequestDto.builder()
                    .dropLocation(bookingDetails.getEndLocation())
                    .pickupLocation(bookingDetails.getStartLocation())
                    .build();

            //changing to kafka
            processNearbyDriverAsync(req , bookingDetails.getPassengerId() , newBooking.getId());
            CreateBookingResponseDto response = CreateBookingResponseDto.builder()
                    .bookingId(newBooking.getId())
                    .bookingStatus(newBooking.getBookingStatus().toString())
                    .driver(Optional.ofNullable(newBooking.getDriver()))
                    .build();

            IdempotencyRecord record = IdempotencyRecord.builder()
                    .idempotencyKey(idempotencyKey)
                    .passengerId(p.getId())
                    .booking(booking)
                    .responseData(objectMapper.writeValueAsString(response))
                    .build();
            idempotencyRepository.save(record);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private void processNearbyDriverAsync(NearbyDriversRequestDto nearbyDriversRequestDto , Long passengerId , Long bookingId) {
        BookingCreatedEvent event = BookingCreatedEvent.builder()
                .bookingId(bookingId.toString())
                .passengerId(passengerId.toString())
                .pickupLocation(nearbyDriversRequestDto.getPickupLocation())
                .dropLocation(nearbyDriversRequestDto.getDropLocation())
                .build();
        kafkaService.publishBookingCreated(event);
        System.out.println("BookingCreatedEvent published for bookingId: " + bookingId);
    }


    @Override
    @Transactional
    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto bookingDetails, Long bookingId) {
        try {
            Driver driver = driverRepository.findById(Long.parseLong(bookingDetails.getDriverId()))
                    .orElseThrow(() -> new NotFoundException("Driver not found with ID: " + bookingDetails.getDriverId()));
            if(driver.getActiveBooking() != null){
                throw new IllegalArgumentException("This driver already have active booking.");
            }
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new NotFoundException("Booking not found with ID: " + bookingId));

            bookingRepository.updateBookingStatusAndDriverById(bookingId, BookingStatus.SCHEDULED, driver);
            driver.setActiveBooking(booking);
            passengerRepository.setActiveBooking(booking.getPassenger().getId(), booking);
            redisService.setDriverBookingPair(bookingDetails.getDriverId() , bookingDetails.getBookingId()); //storing in cachee

            OTP otp = OTP.make(booking);
            otpRepository.save(otp);
            booking.setOtp(otp);
            booking.setBookingStatus(BookingStatus.SCHEDULED);
            booking.setDriver(driver);
            bookingRepository.save(booking);

            UpdateBookingResponseDto response = UpdateBookingResponseDto.builder()
                    .bookingId(bookingId)
                    .bookingStatus(booking.getBookingStatus())
                    .build();
            ConfirmedNotificationToPassengerDto notificationDTO = ConfirmedNotificationToPassengerDto.builder()
                    .bookingId(bookingId.toString())
                    .driverId(driver.getId().toString())
                    .passengerId(booking.getPassenger().getId().toString())
                    .fullName(driver.getFullName())
                    .bookingStatus(booking.getBookingStatus())
                    .build();
            kafkaService.publishBookingConfirmedNotification(notificationDTO);

            System.out.println("Your ride with: " + driver.getFullName() + " is scheduled.");
            return response;

        } catch (RuntimeException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            throw new RuntimeException("Internal server error occurred while updating booking.");
        }
    }

    @Override
    public String cancelBooking(CancelBookingRequestDto cancelBookingRequestDto) { //api just for development purposes
        Long bookingId = cancelBookingRequestDto.getBooking().getId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (booking.getBookingStatus() == BookingStatus.IN_RIDE || booking.getBookingStatus() == BookingStatus.COMPLETED) {
            return "It is not possible to cancel the booking at this stage.";
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        passengerRepository.clearActiveBooking(booking.getPassenger().getId());
        return "Booking cancelled successfully.";
    }

    @Override
    public GetBookingDetailsResponseDTO getBookingDetails(Long bookingId, GetBookingDetailsRequestDto requestDto) {
        if (requestDto == null) {
            throw new BadRequestException("Request body cannot be null.");
        }

        if (requestDto.getUserId() == null || requestDto.getUserId().isBlank()) {
            throw new BadRequestException("User ID must be provided.");
        }

        if (requestDto.getRole() == null || requestDto.getRole().isBlank()) {
            throw new BadRequestException("Role must be provided.");
        }

        Booking booking = bookingRepository.getBookingById(bookingId);
        if (booking == null) {
            throw new ResourceNotFoundException("Booking not found for ID: " + bookingId);
        }

        String role = requestDto.getRole().trim().toUpperCase();
        String userId = requestDto.getUserId();

        switch (role) {
            case "DRIVER":
                if (booking.getDriver() == null ||
                        !booking.getDriver().getId().toString().equals(userId)) {
                    throw new AccessDeniedException("This booking does not belong to this driver.");
                }
                break;

            case "PASSENGER":
                if (booking.getPassenger() == null ||
                        !booking.getPassenger().getId().toString().equals(userId)) {
                    throw new AccessDeniedException("This booking does not belong to this passenger.");
                }
                break;

            default:
                throw new BadRequestException("Invalid role: " + role);
        }

        String totalTimeTaken = TimeUtils.calculateTotalTimeTaken(booking.getStartTime() , booking.getEndTime());

        GetBookingDetailsResponseDTO.GetBookingDetailsResponseDTOBuilder responseBuilder =
                GetBookingDetailsResponseDTO.builder()
                        .bookingId(booking.getId())
                        .bookingStatus(booking.getBookingStatus().toString())
                        .driverId(booking.getDriver() != null ? booking.getDriver().getId() : null)
                        .driverName(booking.getDriver() != null ? booking.getDriver().getFullName() : null)
                        .passengerName(booking.getPassenger().getPassanger_name())
                        .startTime(booking.getStartTime())
                        .pickupLocation(Location.builder()
                                .latitude(booking.getStartLocation().getLatitude())
                                .longitude(booking.getStartLocation().getLongitude())
                                .address(booking.getStartLocation().getAddress())
                                .build())
                        .dropoffLocation(Location.builder()
                                .latitude(booking.getEndLocation().getLatitude())
                                .longitude(booking.getEndLocation().getLongitude())
                                .address(booking.getEndLocation().getAddress())
                                .build())
                        .startTime(booking.getStartTime())
                        .endTime(booking.getEndTime())
                        .totalTimeTaken(totalTimeTaken)
                ;

        if ("PASSENGER".equals(role) && booking.getOtp() != null) {
            responseBuilder.otp(booking.getOtp().getCode());
        }

        return responseBuilder.build();
    }



    @Override
    public ActiveBookingDTO getActiveBooking(Long passengerId) {
        try{
             Optional<Booking> booking = passengerRepository.getActiveBookingByPassengerId(passengerId);
            if(booking.isEmpty()){
                return null;
            }
                return ActiveBookingDTO.builder().bookingId(booking.get().getId().toString())
                        .bookingStatus(booking.get().getBookingStatus()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ActiveBookingDTO getActiveBookingOfDriver(Long driverId) {
        Optional<Long> activeBookingId =  driverRepository.getActiveBookingByDriver(driverId);
        return activeBookingId.map(aLong -> ActiveBookingDTO.builder()
                .bookingId(aLong.toString())
                .bookingStatus(bookingRepository.getBookingStatusById(aLong))
                .build()).orElse(null);
    }


    @Override
    public UpdateBookingResponseDto updateStatus(String bookingIdByPassenger, UpdatingStateDto bookingRequestDto) {
        Long bookingId = driverRepository.getActiveBookingByDriver(Long.parseLong(bookingRequestDto.getDriverId()))
                .orElseThrow(() -> new NotFoundException("No active booking found for driver " + bookingRequestDto.getDriverId()));

        BookingContext bookingContext = new BookingContext(bookingRepository , passengerRepository, driverRepository , redisService , otpRepository);
//        Booking dbBooking = bookingRepository.getBookingById(bookingRequestDto.getBookingId());
        BookingStatus currentStatus = bookingRepository.getBookingStatusById(bookingId);

//        System.out.println("Current for : " + dbBooking.getId() + " -> " + currentStatus);
//        System.out.println("Requested : " + bookingRequestDto.getBookingStatus());

        Long passenger = passengerRepository.findPassengerByActiveBookingId(Long.parseLong(bookingIdByPassenger)).getFirst().getId();
//        System.out.println("passenger : " + passenger);

        try {
            System.out.println(bookingRequestDto.getOtp());
            UpdateBookingRequestDto dto = UpdateBookingRequestDto.builder()
                    .otp(bookingRequestDto.getOtp())
                    .passengerId(passenger.toString())
                    .bookingStatus(bookingRequestDto.getBookingStatus())
                    .driverId(bookingRequestDto.getDriverId())
                    .bookingId(bookingIdByPassenger)
                    .build();
            bookingContext.setState(getStateObject(currentStatus)); // database state would be here
            bookingContext.updateStatus(bookingRequestDto.getBookingStatus(), Long.parseLong(bookingIdByPassenger), dto);
            bookingRepository.updateBookingStatus(
                    Long.parseLong(bookingIdByPassenger),
                    bookingRequestDto.getBookingStatus()
            );

            UpdateBookingResponseDto updateBookingResponseDto = UpdateBookingResponseDto.builder()
                    .bookingStatus(bookingContext.getStatus())
                    .bookingId(Long.parseLong(bookingIdByPassenger))
                    .build();

            //seding notification



            return updateBookingResponseDto;
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Transition not allowed: " + currentStatus + " -> " + bookingRequestDto.getBookingStatus());
        }
    }

    @Override
    public String getOtpForBooking(Long bookingId) {
        return otpRepository.getOTPByBookingId(bookingId)
                .map(OTP::getCode)
                .orElseThrow(() -> new NotFoundException("No OTP found for booking ID: " + bookingId));
    }

    @Override
    public String retryBookingRequest(String bookingId, RetryBookingRequestDto requestDto) {
        try{
            Optional<Booking> activeBooking = bookingRepository.findById(Long.parseLong(bookingId));
            if(activeBooking.isEmpty()){
                throw new NotFoundException("Passenger ID doesn't match with booking ID.");
            }
            if(!activeBooking.get().getBookingStatus().equals(BookingStatus.ASSIGNING_DRIVER)){
                throw new IllegalArgumentException("This booking status is not eligible for retrying.");
            }
            if(activeBooking.get().getPassenger().getId().equals(Long.parseLong(requestDto.getPassengerId()))){
                NearbyDriversRequestDto nearbyDriversRequestDto = NearbyDriversRequestDto.builder()
                        .pickupLocation(activeBooking.get().getStartLocation())
                        .dropLocation(activeBooking.get().getEndLocation())
                        .build();
                processNearbyDriverAsync(nearbyDriversRequestDto , Long.parseLong(requestDto.getPassengerId()) , Long.parseLong(bookingId));
                return "Sent retried request.";
            }
            return null;
        }catch(RuntimeException e){
            throw new RuntimeException("Error while retrying booking request." + e.getMessage());
        }
    }

    public BookingState getStateObject(BookingStatus bookingStatus){
        switch (bookingStatus){
            case ASSIGNING_DRIVER: return new AssigningDriverState();
            case SCHEDULED : return new ScheduledState();
            case ARRIVED: return new ArrivedDriverState();
            case IN_RIDE: return new InrideState();
            case COMPLETED: return new CompletedState();
            case CANCELLED: return new CancelledState();
            default: throw new IllegalStateException("Unknown state of booking");
        }
    }
}
