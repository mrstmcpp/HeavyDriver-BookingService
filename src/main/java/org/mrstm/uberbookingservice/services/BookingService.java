package org.mrstm.uberbookingservice.services;


import org.mrstm.uberbookingservice.dto.*;
import org.mrstm.uberbookingservice.dto.BookingStateDto.UpdatingStateDto;
import org.mrstm.uberentityservice.dto.booking.ActiveBookingDTO;
import org.springframework.stereotype.Service;

@Service
public interface BookingService {
    public CreateBookingResponseDto createBooking(CreateBookingRequestDto bookingDetails);

    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto bookingDetails , Long bookingId);

    public String cancelBooking(CancelBookingRequestDto cancelBookingRequestDto); //api just for development purposes

    public GetBookingDetailsResponseDTO getBookingDetails(Long bookingId , GetBookingDetailsRequestDto getBookingDetailsRequestDto);

    public Long getActiveBooking(Long passengerId);

    public ActiveBookingDTO getActiveBookingOfDriver(Long driverId);

    public UpdateBookingResponseDto updateStatus(UpdatingStateDto bookingRequestDto);

    public String getOtpForBooking(Long bookingId);



}
