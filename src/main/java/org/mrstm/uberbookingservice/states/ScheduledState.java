package org.mrstm.uberbookingservice.states;

import org.mrstm.uberbookingservice.dto.CompleteBookingRequestDto;
import org.mrstm.uberbookingservice.dto.UpdateBookingRequestDto;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.mrstm.uberentityservice.models.BookingStatus;

public class ScheduledState implements BookingState{

    @Override
    public void updateStatus(BookingContext bookingContext, BookingStatus newStatus, Long bookingId, UpdateBookingRequestDto completeBookingRequestDto) {
        switch (newStatus){
            case ARRIVED:
                bookingContext.setState(new ArrivedDriverState());
                bookingContext.getKafkaService().publishStatusUpdateNotification(UpdateBookingResponseDto.builder().bookingId(bookingId).bookingStatus(BookingStatus.ARRIVED).build());
                break;
            case CANCELLED:
                bookingContext.setState(new CancelledState());
                bookingContext.getKafkaService().publishStatusUpdateNotification(UpdateBookingResponseDto.builder().bookingId(bookingId).bookingStatus(BookingStatus.CANCELLED).build());
                break;
            default:
                throw new IllegalStateException("Invalid transition to state.");
        }
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.SCHEDULED;
    }
}
