package org.mrstm.uberbookingservice.services;

import org.mrstm.uberentityservice.dto.booking.StartRideWithOtp;
import org.mrstm.uberentityservice.dto.driver.BookingsByDriverResponseDto;
import org.mrstm.uberentityservice.models.Booking;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public interface DriverBookingServices {
    BookingsByDriverResponseDto fetchAllBookingsByDriver(String driverId, int offset , int pageSize);
    BookingsByDriverResponseDto fetchTodayBookingsByDriver(String driverId, int offset , int pageSize);
    String startRideWithOtp(StartRideWithOtp startRideWithOtp, String bookingId , String driverId);

}
