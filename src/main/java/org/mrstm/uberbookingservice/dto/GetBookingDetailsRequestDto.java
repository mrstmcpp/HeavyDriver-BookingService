package org.mrstm.uberbookingservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBookingDetailsRequestDto {
    private String userId;
    private String role;
}
