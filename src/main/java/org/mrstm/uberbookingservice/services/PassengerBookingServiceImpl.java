package org.mrstm.uberbookingservice.services;

import org.mrstm.uberbookingservice.repositories.BookingRepository;
import org.mrstm.uberentityservice.dto.passenger.BookingsByPassengerResponseDto;
import org.mrstm.uberentityservice.dto.passenger.PassengerBookingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PassengerBookingServiceImpl implements PassengerBookingService {
    private final BookingRepository bookingRepository;

    public PassengerBookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingsByPassengerResponseDto fetchAllBookingsByPassenger(String passengerId, int offset, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(offset , pageSize);
            // fetch all bookings for driver
            Page<PassengerBookingDTO> bookingPage = bookingRepository.findAllBookingsByPassengerId(Long.parseLong(passengerId) , pageable);
            return BookingsByPassengerResponseDto.builder()
                    .bookingList(bookingPage.getContent())
                    .currentPage(offset)
                    .totalPages(bookingPage.getTotalPages())
                    .totalItems(bookingPage.getTotalElements())
                    .passengerId(passengerId)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all bookings for driverId: " + passengerId, e);
        }
    }
}
