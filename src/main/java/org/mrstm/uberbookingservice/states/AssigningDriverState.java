package org.mrstm.uberbookingservice.states;

import org.mrstm.uberbookingservice.dto.CompleteBookingRequestDto;
import org.mrstm.uberbookingservice.dto.UpdateBookingRequestDto;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.mrstm.uberentityservice.models.BookingStatus;

public class AssigningDriverState implements BookingState{


    @Override
    public void updateStatus(BookingContext bookingContext, BookingStatus newStatus, Long bookingId, UpdateBookingRequestDto completeBookingRequestDto) {
        switch (newStatus){
            case SCHEDULED:
                bookingContext.setState(new ScheduledState());
                bookingContext.getKafkaService().publishStatusUpdateNotification(UpdateBookingResponseDto.builder().bookingId(bookingId).bookingStatus(BookingStatus.SCHEDULED).build());

                break;
            case CANCELLED:
                bookingContext.setState(new CancelledState());
                bookingContext.getKafkaService().publishStatusUpdateNotification(UpdateBookingResponseDto.builder().bookingId(bookingId).bookingStatus(BookingStatus.CANCELLED).build());

                break;
            default:
                throw new IllegalStateException("Invalid transition");
        }
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.ASSIGNING_DRIVER;
    }
}
