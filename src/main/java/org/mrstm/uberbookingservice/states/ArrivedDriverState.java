package org.mrstm.uberbookingservice.states;

import org.mrstm.uberbookingservice.dto.UpdateBookingRequestDto;
import org.mrstm.uberbookingservice.exceptions.InvalidOtpException;
import org.mrstm.uberbookingservice.exceptions.OtpNotFoundException;
import org.mrstm.uberbookingservice.services.BookingService;
import org.mrstm.uberbookingservice.services.BookingServiceImpl;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.mrstm.uberentityservice.models.BookingStatus;
import org.springframework.beans.factory.annotation.Autowired;


public class ArrivedDriverState implements BookingState{
    @Override
    public void updateStatus(BookingContext bookingContext, BookingStatus newStatus, Long bookingId, UpdateBookingRequestDto updateBookingRequestDto) {
        switch (newStatus){
            case IN_RIDE:
                if(updateBookingRequestDto.getOtp() == null || updateBookingRequestDto.getOtp().isEmpty()){
                    throw new OtpNotFoundException("OTP must be provided to start the ride.");
                }
                String otp = bookingContext.getOtpRepository().getOTPByBookingId(Long.parseLong(updateBookingRequestDto.getBookingId())).get().getCode();
                bookingContext.getBookingRepository().setStartTimeOfBooking(bookingId);
                //                System.out.println(otp + " in arrived state class");
                if(!updateBookingRequestDto.getOtp().equals(otp)){
                    throw new InvalidOtpException("Invalid otp provided.");
                }
                bookingContext.setState(new InrideState());
                //producing notification
                bookingContext.getKafkaService().publishStatusUpdateNotification(UpdateBookingResponseDto.builder().bookingId(bookingId).bookingStatus(BookingStatus.IN_RIDE).build());
                break;
            case CANCELLED:
                bookingContext.setState(new CancelledState());
                bookingContext.getKafkaService().publishStatusUpdateNotification(UpdateBookingResponseDto.builder().bookingId(bookingId).bookingStatus(BookingStatus.CANCELLED).build());
                break;
            default:
                throw new IllegalStateException("Illegal transition of state at arrivedstate class");
        }
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.ARRIVED;
    }
}
