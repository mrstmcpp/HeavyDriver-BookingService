package org.mrstm.uberbookingservice.repositories;

import org.mrstm.uberentityservice.models.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OTP , Long> {

    Optional<OTP> getOTPByBookingId(Long bookingId);
}
