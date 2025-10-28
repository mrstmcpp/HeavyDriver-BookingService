package org.mrstm.uberbookingservice.services;

import org.mrstm.uberentityservice.dto.passenger.BookingsByPassengerResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface PassengerBookingService {
    BookingsByPassengerResponseDto fetchAllBookingsByPassenger(String passengerId, int offset , int pageSize);
}
