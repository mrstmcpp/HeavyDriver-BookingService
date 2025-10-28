package org.mrstm.uberbookingservice.dto;


import lombok.*;
import org.mrstm.uberbookingservice.models.Location;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBookingDetailsResponseDTO {
    private Long driverId;
    private Long bookingId;
    private String driverName;
    private String passengerName;
    private String bookingStatus;
    private Location pickupLocation;
    private Location dropoffLocation;
    private Date startTime;
    private Date endTime;
    private String otp;
    private String totalTimeTaken;
}
