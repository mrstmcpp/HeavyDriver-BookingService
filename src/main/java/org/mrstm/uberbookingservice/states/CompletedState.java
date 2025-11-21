package org.mrstm.uberbookingservice.states;

import org.mrstm.uberbookingservice.dto.UpdateBookingRequestDto;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.mrstm.uberentityservice.models.BookingStatus;

public class CompletedState implements BookingState{


    @Override
    public void updateStatus(BookingContext bookingContext, BookingStatus newStatus, Long bookingId, UpdateBookingRequestDto completeBookingRequestDto) {
        throw new IllegalStateException(
                "Invalid transition: Booking is already COMPLETED. Cannot move to " + newStatus
        );
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.COMPLETED;
    }

    public static CompletedState completeBooking(BookingContext bookingContext , Long bookingId, UpdateBookingRequestDto updateBookingRequestDto){
        bookingContext.getBookingRepository().updateBookingStatus(bookingId , BookingStatus.COMPLETED);
        bookingContext.getFareService().calculateAndSaveFare(bookingId);
        bookingContext.getBookingRepository().setEndTimeOfBooking(bookingId);
        bookingContext.getPassengerRepository().clearActiveBooking(Long.parseLong(updateBookingRequestDto.getPassengerId()));
        System.out.println("cleared active booking of passenger : " + updateBookingRequestDto.getPassengerId());
        bookingContext.getDriverRepository().clearActiveBooking(Long.parseLong(updateBookingRequestDto.getDriverId()));
        System.out.println(updateBookingRequestDto.getDriverId());
        bookingContext.getRedisService().deleteDriverBookingPair(updateBookingRequestDto.getDriverId());
        bookingContext.getKafkaService().publishStatusUpdateNotification(UpdateBookingResponseDto.builder().bookingId(bookingId).bookingStatus(BookingStatus.COMPLETED).build());
        return new CompletedState();
    }
}
