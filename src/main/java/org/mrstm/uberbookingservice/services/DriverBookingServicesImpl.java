package org.mrstm.uberbookingservice.services;

import org.mrstm.uberbookingservice.repositories.BookingRepository;
import org.mrstm.uberentityservice.dto.driver.BookingDTO;
import org.mrstm.uberentityservice.dto.driver.BookingsByDriverResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverBookingServicesImpl implements DriverBookingServices {

    private final BookingRepository bookingRepository;

    public DriverBookingServicesImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

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
                    .driverId(driverId)
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
                    .driverId(driverId)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all bookings for driverId: " + driverId, e);
        }
    }
}
