package org.mrstm.uberbookingservice.services;


import org.mrstm.uberbookingservice.dto.*;
import org.mrstm.uberbookingservice.dto.BookingStateDto.UpdatingStateDto;
import org.mrstm.uberentityservice.dto.booking.ActiveBookingDTO;
import org.mrstm.uberentityservice.dto.booking.CreateBookingRequestDto;
import org.mrstm.uberentityservice.dto.booking.GetBookingDetailsDTO;
import org.mrstm.uberentityservice.dto.booking.RetryBookingRequestDto;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface BookingService {
    public CreateBookingResponseDto createBooking(CreateBookingRequestDto bookingDetails , String idempotencyKey);

    public UpdateBookingResponseDto registerBooking(UpdateBookingRequestDto bookingDetails , Long bookingId);

    public String cancelBooking(CancelBookingRequestDto cancelBookingRequestDto); //api just for development purposes

    public GetBookingDetailsDTO getBookingDetails(Long bookingId , GetBookingDetailsRequestDto getBookingDetailsRequestDto);

    public ActiveBookingDTO getActiveBooking(Long passengerId);

    public ActiveBookingDTO getActiveBookingOfDriver(Long driverId);

    public UpdateBookingResponseDto updateStatus(String bookingId, UpdatingStateDto bookingRequestDto);

    public String getOtpForBooking(Long bookingId);

    public String retryBookingRequest(String bookingId , RetryBookingRequestDto requestDto);

}
