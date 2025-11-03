package org.mrstm.uberbookingservice.repositories;

import org.springframework.transaction.annotation.Transactional;
import org.mrstm.uberentityservice.models.Booking;
import org.mrstm.uberentityservice.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    @Query("SELECT p.activeBooking FROM Passenger p WHERE p.id = :passengerId")
    Optional<Booking> getActiveBookingByPassengerId(@Param("passengerId") Long passengerId);

    @Query("SELECT p.activeBooking.id FROM Passenger p WHERE p.id = :passengerId")
    Optional<Long>  getActiveBookingIdByPassengerId(@Param("passengerId") Long passengerId);

    @Modifying
    @Transactional
    @Query("UPDATE Passenger p SET p.activeBooking = null WHERE p.id = :passengerId")
    void clearActiveBooking(@Param("passengerId") Long passengerId);

    @Modifying
    @Transactional
    @Query("UPDATE Passenger p SET p.activeBooking = :booking WHERE p.id = :passengerId")
    void setActiveBooking(@Param("passengerId") Long passengerId , @Param("booking") Booking booking);

    List<Passenger> findPassengerByActiveBookingId(Long activeBookingId);

}
