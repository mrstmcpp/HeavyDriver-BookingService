package org.mrstm.uberbookingservice.repositories;

import org.mrstm.uberentityservice.dto.driver.BookingDTO;
import org.mrstm.uberentityservice.dto.passenger.PassengerBookingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.mrstm.uberentityservice.models.Booking;
import org.mrstm.uberentityservice.models.BookingStatus;
import org.mrstm.uberentityservice.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.bookingStatus = :status , b.driver = :driver WHERE b.id = :id")
    void updateBookingStatusAndDriverById(@Param("id") Long id, @Param("status") BookingStatus Status, @Param("driver") Driver driver);


    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.bookingStatus = :status WHERE b.id = :id")
    void updateBookingStatus(@Param("id") Long id, @Param("status") BookingStatus status);

    Booking getBookingById(Long id);

    @Query("SELECT b.bookingStatus FROM Booking b WHERE b.id = :id")
    BookingStatus getBookingStatusById(@Param("id") Long id);

    //druver panel queries
    @Query("""
            SELECT new org.mrstm.uberentityservice.dto.driver.BookingDTO(
                b.id,
                b.bookingStatus,
                f.finalFare,
                b.createdAt,
                b.driver.id
            )
            FROM Booking b
            LEFT JOIN b.fare f
            WHERE b.driver.id = :driverId
            ORDER BY b.createdAt DESC
            """)
    Page<BookingDTO> findAllBookingsByDriverId(@Param("driverId") Long driverId, Pageable pageable);


    @Query("""
            SELECT new org.mrstm.uberentityservice.dto.driver.BookingDTO(
                b.id,
                b.bookingStatus,
                f.finalFare,
                b.createdAt,
                b.driver.id
            )
            FROM Booking b
            LEFT JOIN b.fare f
            WHERE b.driver.id = :driverId AND DATE(b.createdAt) = CURRENT_DATE
            ORDER BY b.createdAt DESC
            """)
    Page<BookingDTO> findTodayBookingsByDriverId(@Param("driverId") Long driverId, Pageable pageable);


    @Transactional
    @Modifying
    @Query("UPDATE Booking b SET b.startTime = CURRENT_TIMESTAMP WHERE b.id = :bookingId")
    void setStartTimeOfBooking(@Param("bookingId") Long bookingId);

    @Transactional
    @Modifying
    @Query("UPDATE Booking b SET b.endTime = CURRENT_TIMESTAMP WHERE b.id = :bookingId")
    void setEndTimeOfBooking(@Param("bookingId") Long bookingId);


    @Query("""
            SELECT new org.mrstm.uberentityservice.dto.passenger.PassengerBookingDTO(
                b.id,
                b.bookingStatus,
                f.finalFare,
                b.createdAt,
                d.id,
                d.fullName
            )
            FROM Booking b
            LEFT JOIN b.driver d
            LEFT JOIN b.fare f
            WHERE b.passenger.id = :passengerId
            ORDER BY b.createdAt DESC
            """)
    Page<PassengerBookingDTO> findAllBookingsByPassengerId(
            @Param("passengerId") Long passengerId,
            Pageable pageable
    );


}
