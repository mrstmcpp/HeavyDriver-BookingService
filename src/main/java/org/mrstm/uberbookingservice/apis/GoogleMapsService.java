package org.mrstm.uberbookingservice.apis;

import org.mrstm.uberentityservice.dto.googlemaps.DistanceDuration;
import org.mrstm.uberentityservice.models.ExactLocation;
import org.springframework.stereotype.Service;

@Service
public interface GoogleMapsService {
    DistanceDuration getDistanceAndDuration(ExactLocation startLocation, ExactLocation endLocation);
}
