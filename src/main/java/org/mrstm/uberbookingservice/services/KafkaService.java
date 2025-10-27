package org.mrstm.uberbookingservice.services;

import org.mrstm.uberbookingservice.dto.KafkaDtos.BookingConfirmedEvent;
import org.mrstm.uberentityservice.dto.booking.BookingCreatedEvent;
import org.mrstm.uberentityservice.dto.booking.ConfirmedNotificationToPassengerDto;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface KafkaService {
    void publishBookingCreated(BookingCreatedEvent bookingCreatedEvent);
    void publishBookingConfirmedNotification(ConfirmedNotificationToPassengerDto confirmedNotificationToPassengerDto);
    void publishStatusUpdateNotification(UpdateBookingResponseDto updateBookingResponseDto);
}
