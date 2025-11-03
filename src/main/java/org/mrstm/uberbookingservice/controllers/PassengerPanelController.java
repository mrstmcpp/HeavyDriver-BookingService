package org.mrstm.uberbookingservice.controllers;

import lombok.RequiredArgsConstructor;
import org.mrstm.uberbookingservice.dto.BookingStateDto.UpdatingStateDto;
import org.mrstm.uberbookingservice.services.BookingService;
import org.mrstm.uberbookingservice.services.PassengerBookingService;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.mrstm.uberentityservice.dto.passenger.BookingsByPassengerResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passenger")
@RequiredArgsConstructor
public class PassengerPanelController {
    private final PassengerBookingService passengerBookingService;

    @GetMapping("/all-booking")
    public ResponseEntity<BookingsByPassengerResponseDto> fetchAllBookingsByPassenger(@RequestParam(value = "offset" , required = false, defaultValue = "0") Integer offset,
                                                                                      @RequestParam(value = "pageSize" , required = false , defaultValue = "10") Integer pageSize,
                                                                                      @RequestHeader("X-User-Id") Long passengerId,
                                                                                      @RequestHeader("X-User-Role") String role){
        try{
            return new ResponseEntity<>(passengerBookingService.fetchAllBookingsByPassenger(passengerId, offset, pageSize) , HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
