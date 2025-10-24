package org.mrstm.uberbookingservice.repositories;

import org.mrstm.uberentityservice.models.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord ,Long> {
    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

}
