package org.mrstm.uberbookingservice.services;

import org.mrstm.uberbookingservice.dto.BookingStateDto.UpdatingStateDto;
import org.mrstm.uberbookingservice.repositories.BookingRepository;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
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
    public BookingsByPassengerResponseDto fetchAllBookingsByPassenger(Long passengerId, int offset, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(offset , pageSize);
            // fetch all bookings for driver
            Page<PassengerBookingDTO> bookingPage = bookingRepository.findAllBookingsByPassengerId(passengerId, pageable);
            return BookingsByPassengerResponseDto.builder()
                    .bookingList(bookingPage.getContent())
                    .currentPage(offset)
                    .totalPages(bookingPage.getTotalPages())
                    .totalItems(bookingPage.getTotalElements())
                    .passengerId(passengerId.toString())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all bookings for driverId: " + passengerId, e);
        }
    }

    @Override
    public UpdateBookingResponseDto updateStatus(Long passengerId, UpdatingStateDto bookingRequestDto) {
        return null;
    }
}
