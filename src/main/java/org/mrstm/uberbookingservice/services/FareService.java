package org.mrstm.uberbookingservice.services;

import org.mrstm.uberentityservice.dto.fare.CalculatedFareDTO;
import org.mrstm.uberentityservice.dto.fare.EstimateFareRequestDto;
import org.mrstm.uberentityservice.dto.fare.FareRateDto;
import org.mrstm.uberentityservice.models.CarType;
import org.springframework.stereotype.Service;

@Service
public interface FareService {
    void calculateAndSaveFare(Long bookingId);
    CalculatedFareDTO estimateFare(EstimateFareRequestDto estimateFareRequestDto, double discount);
    String addNewFareRate(FareRateDto fareRateDto);
}
