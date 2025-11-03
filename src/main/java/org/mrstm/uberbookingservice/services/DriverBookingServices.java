package org.mrstm.uberbookingservice.services;

import org.mrstm.uberbookingservice.dto.BookingStateDto.UpdatingStateDto;
import org.mrstm.uberentityservice.dto.booking.StartRideWithOtp;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.mrstm.uberentityservice.dto.driver.BookingsByDriverResponseDto;
import org.springframework.stereotype.Service;


@Service
public interface DriverBookingServices {
    BookingsByDriverResponseDto fetchAllBookingsByDriver(String driverId, int offset , int pageSize);
    BookingsByDriverResponseDto fetchTodayBookingsByDriver(String driverId, int offset , int pageSize);
    String startRideWithOtp(StartRideWithOtp startRideWithOtp, String bookingId , String driverId);


}
