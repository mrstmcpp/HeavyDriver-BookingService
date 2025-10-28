package org.mrstm.uberbookingservice.controllers;

import org.mrstm.uberbookingservice.services.PassengerBookingService;
import org.mrstm.uberentityservice.dto.passenger.BookingsByPassengerResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passenger")
public class PassengerPanelController {
    private final PassengerBookingService passengerBookingService;

    public PassengerPanelController(PassengerBookingService passengerBookingService) {
        this.passengerBookingService = passengerBookingService;
    }

    @GetMapping("/{passengerId}/all-booking")
    public ResponseEntity<BookingsByPassengerResponseDto> fetchAllBookingsByPassenger(@RequestParam(value = "offset" , required = false, defaultValue = "0") Integer offset,
                                                                                      @RequestParam(value = "pageSize" , required = false , defaultValue = "10") Integer pageSize,
                                                                                      @PathVariable String passengerId){
        try{
            return new ResponseEntity<>(passengerBookingService.fetchAllBookingsByPassenger(passengerId, offset, pageSize) , HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
