package org.mrstm.uberbookingservice.controllers;

import org.mrstm.uberbookingservice.services.DriverBookingServices;
import org.mrstm.uberentityservice.dto.booking.StartRideWithOtp;
import org.mrstm.uberentityservice.dto.driver.BookingsByDriverResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/driver")
public class DriverPanelController {
    private final DriverBookingServices driverBookingServices;

    public DriverPanelController(DriverBookingServices driverBookingServices) {
        this.driverBookingServices = driverBookingServices;
    }

    @GetMapping("/{driverId}/today-booking")
    public ResponseEntity<BookingsByDriverResponseDto> getTodayBookings(@RequestParam(value = "offset" , required = false, defaultValue = "0") Integer offset,
                                                                        @RequestParam(value = "pageSize" , required = false , defaultValue = "10") Integer pageSize,
                                                                        @PathVariable String driverId){
        try{
            return new ResponseEntity<>(driverBookingServices.fetchTodayBookingsByDriver(driverId, offset, pageSize) ,HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{driverId}/all-booking")
    public ResponseEntity<BookingsByDriverResponseDto> getAllBookings(@RequestParam(value = "offset" , required = false, defaultValue = "0") Integer offset,
                                                                      @RequestParam(value = "pageSize" , required = false , defaultValue = "10") Integer pageSize,
                                                                      @PathVariable String driverId
    ){
        try{
            return new ResponseEntity<>(driverBookingServices.fetchAllBookingsByDriver((driverId) , offset ,pageSize) , HttpStatus.OK);

        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }



    @PutMapping("/{driverId}/booking/{bookingId}/start")
    public ResponseEntity<String> startRideWithOtp(@RequestBody StartRideWithOtp startRideWithOtp, @PathVariable String bookingId , @PathVariable String driverId){
        return new ResponseEntity<>(driverBookingServices.startRideWithOtp(startRideWithOtp , bookingId , driverId), HttpStatus.OK);
    }


}
