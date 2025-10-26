package org.mrstm.uberbookingservice.dto.BookingStateDto;

import lombok.*;
import org.mrstm.uberentityservice.models.BookingStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatingStateDto {
    private String otp;
    private String driverId;
    private BookingStatus bookingStatus;
}
