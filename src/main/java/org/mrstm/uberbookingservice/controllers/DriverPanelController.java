package org.mrstm.uberbookingservice.controllers;

import org.apache.coyote.Response;
import org.mrstm.uberbookingservice.services.DriverBookingServices;
import org.mrstm.uberentityservice.dto.driver.BookingsByDriverResponseDto;
import org.mrstm.uberentityservice.models.Booking;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DriverPanelController {
    private final DriverBookingServices driverBookingServices;

    public DriverPanelController(DriverBookingServices driverBookingServices) {
        this.driverBookingServices = driverBookingServices;
    }

    @GetMapping("/driver/{driverId}/today-booking")
    public ResponseEntity<BookingsByDriverResponseDto> getTodayBookings(@RequestParam(value = "offset" , required = false, defaultValue = "0") Integer offset,
                                                                        @RequestParam(value = "pageSize" , required = false , defaultValue = "10") Integer pageSize,
                                                                        @PathVariable String driverId){
        try{
            return new ResponseEntity<>(driverBookingServices.fetchTodayBookingsByDriver(driverId, offset, pageSize) ,HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/driver/{driverId}/all-booking")
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
}
