package org.mrstm.uberbookingservice.consumers;


import org.mrstm.uberbookingservice.dto.UpdateBookingRequestDto;
import org.mrstm.uberbookingservice.services.BookingService;
import org.mrstm.uberbookingservice.services.KafkaService;
import org.mrstm.uberentityservice.dto.booking.RideResponseByDriver;
import org.mrstm.uberentityservice.kafkaTopics.KafkaTopics;
import org.mrstm.uberentityservice.models.BookingStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConfirmedBookingEventConsumer {
    private final KafkaService kafkaService;
    private final BookingService bookingService;

    public ConfirmedBookingEventConsumer(KafkaService kafkaService, BookingService bookingService) {
        this.kafkaService = kafkaService;
        this.bookingService = bookingService;
    }

    @KafkaListener(topics = KafkaTopics.BOOKING_CONFIRMED, groupId = "booking-group")
    public void listenConfirmedBookingAndUpdateBooking(RideResponseByDriver rideResponseByDriver) {
        try {
            System.out.println("Confirmed booking msg received" + rideResponseByDriver.getBookingId());
            UpdateBookingRequestDto updateRequest = UpdateBookingRequestDto.builder()
                    .bookingId(rideResponseByDriver.getBookingId())
                    .driverId(rideResponseByDriver.getDriverId())
                    .passengerId(rideResponseByDriver.getPassengerId())
                    .bookingStatus(BookingStatus.SCHEDULED)
                    .build();
            System.out.println("details : " + rideResponseByDriver.getDriverId() + " , " + rideResponseByDriver.getPassengerId() + " , " + rideResponseByDriver.getBookingId());
            bookingService.updateBooking(updateRequest, Long.parseLong(rideResponseByDriver.getBookingId()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
