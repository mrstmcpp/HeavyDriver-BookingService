package org.mrstm.uberbookingservice.services;

import org.mrstm.uberentityservice.dto.driver.BookingsByDriverResponseDto;
import org.mrstm.uberentityservice.models.Booking;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DriverBookingServices {
    BookingsByDriverResponseDto fetchAllBookingsByDriver(String driverId, int offset , int pageSize);
    BookingsByDriverResponseDto fetchTodayBookingsByDriver(String driverId, int offset , int pageSize);

}
