package org.mrstm.uberbookingservice.services;

import org.mrstm.uberbookingservice.dto.BookingStateDto.UpdatingStateDto;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.mrstm.uberentityservice.dto.passenger.BookingsByPassengerResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface PassengerBookingService {
    BookingsByPassengerResponseDto fetchAllBookingsByPassenger(Long passengerId, int offset , int pageSize);
    public UpdateBookingResponseDto updateStatus(Long passengerId, UpdatingStateDto bookingRequestDto);
}
