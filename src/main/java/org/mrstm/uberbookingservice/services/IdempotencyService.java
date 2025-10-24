package org.mrstm.uberbookingservice.services;

import org.mrstm.uberentityservice.models.Booking;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public interface IdempotencyService {
    Booking processBooking(String idempotencyKey , Long passengerId, Object requestPayload, Supplier<Booking> bookingSupplier);
}
