package org.mrstm.uberbookingservice.dto;

import lombok.*;
import org.mrstm.uberentityservice.models.CarType;
import org.mrstm.uberentityservice.models.ExactLocation;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NearbyDriversRequestDto {
    private CarType carType;
    private ExactLocation pickupLocation;
    private ExactLocation dropLocation;

}
