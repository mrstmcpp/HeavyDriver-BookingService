package org.mrstm.uberbookingservice.dto;

import lombok.*;
import org.mrstm.uberentityservice.models.BookingStatus;
import org.mrstm.uberentityservice.models.OTP;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingRequestDto {
    private String otp;
    private String passengerId;
    private String bookingId;
    private String driverId;
    private BookingStatus bookingStatus;
}
