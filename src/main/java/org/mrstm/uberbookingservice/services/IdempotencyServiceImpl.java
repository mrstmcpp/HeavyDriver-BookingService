package org.mrstm.uberbookingservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mrstm.uberbookingservice.repositories.IdempotencyRepository;
import org.mrstm.uberentityservice.models.Booking;
import org.mrstm.uberentityservice.models.IdempotencyRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Supplier;

@Service
public class IdempotencyServiceImpl implements IdempotencyService{

    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;
    public IdempotencyServiceImpl(IdempotencyRepository idempotencyRepository, ObjectMapper objectMapper) {
        this.idempotencyRepository = idempotencyRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Booking processBooking(String idempotencyKey, Long passengerId, Object requestPayload, Supplier<Booking> bookingSupplier) {
        Optional<IdempotencyRecord> existing = idempotencyRepository.findByIdempotencyKey(idempotencyKey);

        if(existing.isPresent()){
            try{
                return objectMapper.readValue(existing.get().getResponseData() , Booking.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed at idempotency service." + e.getMessage());
            }
        }

        Booking booking = bookingSupplier.get();
        //saving idm record
        try{
            IdempotencyRecord record = IdempotencyRecord.builder()
                    .idempotencyKey(idempotencyKey)
                    .passengerId(passengerId)
                    .requestHash(objectMapper.writeValueAsString(requestPayload))
                    .responseData(objectMapper.writeValueAsString(booking))
                    .booking(booking)
                    .build();
            idempotencyRepository.save(record);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save idm record. " + e.getMessage());
        }

        return booking;
    }
}
