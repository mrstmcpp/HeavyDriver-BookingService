package org.mrstm.uberbookingservice.dto;

import lombok.*;
import org.mrstm.uberentityservice.models.Driver;

import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponseDto {
    private Long bookingId;
    private String bookingStatus;
    private String carType;
    private Optional<Driver> driver;

}
