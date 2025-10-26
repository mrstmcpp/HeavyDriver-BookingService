package org.mrstm.uberbookingservice.controllers;

import org.mrstm.uberbookingservice.dto.*;
import org.mrstm.uberbookingservice.dto.BookingStateDto.UpdatingStateDto;
import org.mrstm.uberbookingservice.services.BookingServiceImpl;
import org.mrstm.uberentityservice.dto.booking.ActiveBookingDTO;
import org.mrstm.uberentityservice.dto.booking.RetryBookingRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class BookingController {
    private final BookingServiceImpl bookingService;

    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/")
    public ResponseEntity<CreateBookingResponseDto> createBooking(@RequestBody CreateBookingRequestDto createBookingRequestDto , @RequestHeader("Idempotency-Key") String idempotencyKey) {
        System.out.println("here llll");

        CreateBookingResponseDto response = bookingService.createBooking(createBookingRequestDto , idempotencyKey);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    @GetMapping("/feign")
//    public ResponseEntity<CreateBookingResponseDto> createBookingFeign(@RequestBody CreateBookingRequestDto createBookingRequestDto) {
//        CreateBookingResponseDto response = bookingService.createBooking(createBookingRequestDto);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }

    @PostMapping("/{bookingId}")
    public ResponseEntity<UpdateBookingResponseDto> updateBooking(@RequestBody UpdateBookingRequestDto requestDto , @PathVariable Long bookingId){
        return new ResponseEntity<>(bookingService.updateBooking(requestDto , bookingId) , HttpStatus.OK);
    }

    @PostMapping("/details/{bookingId}")
    public ResponseEntity<GetBookingDetailsResponseDTO> getBookingDetails(@PathVariable Long bookingId ,@RequestBody GetBookingDetailsRequestDto getBookingDetailsRequestDto){
        return new ResponseEntity<>(bookingService.getBookingDetails(bookingId , getBookingDetailsRequestDto) , HttpStatus.OK);
    }

    @GetMapping("/active/passenger/{passengerId}")
    public ResponseEntity<ActiveBookingDTO> getActiveBookingOfPassenger(@PathVariable Long passengerId){
        return new ResponseEntity<>(bookingService.getActiveBooking(passengerId) , HttpStatus.OK);
    }

    @GetMapping("/active/driver/{driverId}")
    public ResponseEntity<ActiveBookingDTO> getActiveBookingOfDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(bookingService.getActiveBookingOfDriver(driverId));
    }


    @PutMapping("/{bookingId}/updateStatus")
    public ResponseEntity<UpdateBookingResponseDto> updateBookingStatus(@PathVariable String bookingId ,@RequestBody UpdatingStateDto updateBookingRequestDto){
        return new ResponseEntity<>(bookingService.updateStatus(bookingId , updateBookingRequestDto) , HttpStatus.OK);
    }

    @PostMapping("/{bookingId}/retry")
    public ResponseEntity<String> retryBookingRequest(@PathVariable String bookingId, @RequestBody RetryBookingRequestDto requestDto){
        return new ResponseEntity<>(bookingService.retryBookingRequest(bookingId , requestDto) , HttpStatus.OK);
    }

}
