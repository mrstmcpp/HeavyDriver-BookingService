package org.mrstm.uberbookingservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.mrstm.uberbookingservice.dto.BookingStateDto.UpdatingStateDto;
import org.mrstm.uberbookingservice.dto.UpdateBookingRequestDto;
import org.mrstm.uberbookingservice.repositories.*;
import org.mrstm.uberbookingservice.states.*;
import org.mrstm.uberentityservice.dto.booking.StartRideWithOtp;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.mrstm.uberentityservice.dto.driver.BookingDTO;
import org.mrstm.uberentityservice.dto.driver.BookingsByDriverResponseDto;
import org.mrstm.uberentityservice.models.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverBookingServicesImpl implements DriverBookingServices {
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final OtpRepository otpRepository;
    private final DriverRepository driverRepository;
    private final KafkaService kafkaService;
    private final RedisService redisService;
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;
    private final FareService fareService;


    @Override
    public BookingsByDriverResponseDto fetchAllBookingsByDriver(String driverId , int offset , int pageSize) {
        try {
            Pageable pageable = PageRequest.of(offset , pageSize);
            // fetch all bookings for driver
            Page<BookingDTO> bookingPage = bookingRepository.findAllBookingsByDriverId(Long.parseLong(driverId) , pageable);
            return BookingsByDriverResponseDto.builder()
                    .bookingList(bookingPage.getContent())
                    .currentPage(offset)
                    .totalPages(bookingPage.getTotalPages())
                    .totalItems(bookingPage.getTotalElements())
                    .userId(driverId)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all bookings for driverId: " + driverId, e);
        }
    }

    @Override
    public BookingsByDriverResponseDto fetchTodayBookingsByDriver(String driverId , int offset , int pageSize) {
        try {
            // fetch only today's bookings
            Pageable pageable = PageRequest.of(offset , pageSize);
            // fetch all bookings for driver
            Page<BookingDTO> bookingPage = bookingRepository.findTodayBookingsByDriverId(Long.parseLong(driverId) , pageable);
            return BookingsByDriverResponseDto.builder()
                    .bookingList(bookingPage.getContent())
                    .currentPage(offset)
                    .totalPages(bookingPage.getTotalPages())
                    .totalItems(bookingPage.getTotalElements())
                    .userId(driverId)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all bookings for driverId: " + driverId, e);
        }
    }

    @Override
    public String startRideWithOtp(StartRideWithOtp startRideWithOtp, String bookingId, String driverId) {
        try {
            return "";
        } catch (Exception e) {
            throw new RuntimeException("Error in starting ride for driverId: " + driverId + " & bookingId: "  + bookingId , e);
        }
    }


}
