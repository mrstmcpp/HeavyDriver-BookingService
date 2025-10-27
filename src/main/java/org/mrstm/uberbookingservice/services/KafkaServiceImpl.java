package org.mrstm.uberbookingservice.services;

import org.mrstm.uberbookingservice.dto.KafkaDtos.BookingConfirmedEvent;
import org.mrstm.uberentityservice.dto.booking.BookingCreatedEvent;

import org.mrstm.uberentityservice.dto.booking.ConfirmedNotificationToPassengerDto;
import org.mrstm.uberentityservice.dto.booking.UpdateBookingResponseDto;
import org.mrstm.uberentityservice.kafkaTopics.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaServiceImpl implements KafkaService {
    private final KafkaTemplate<String , Object> kafkaTemplate;

    public KafkaServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishBookingCreated( BookingCreatedEvent bookingCreatedEvent) {
        kafkaTemplate.send(KafkaTopics.BOOKING_CREATED , bookingCreatedEvent);
    }

    @Override
    public void publishBookingConfirmedNotification(ConfirmedNotificationToPassengerDto confirmedNotificationToPassengerDto) {
        try{
            kafkaTemplate.send(KafkaTopics.BOOKING_CONFIRMED_NOTIFICATION , confirmedNotificationToPassengerDto);
            System.out.println("Publish to BOOKING_CONFIRMED_NOTIFICATION");
        } catch (Exception e) {
            throw new RuntimeException("Error publish Booking Confirmed Notification (to passenger) to kafka topic. " + e.getMessage());
        }
    }

    @Override
    public void publishStatusUpdateNotification(UpdateBookingResponseDto updateBookingResponseDto) {
        try{
            kafkaTemplate.send(KafkaTopics.BOOKING_UPDATES , updateBookingResponseDto);
        } catch (Exception e) {
            throw new RuntimeException("Error publish booknig updates to kafka topic. " + e.getMessage());
        }
    }
}
