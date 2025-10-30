package org.mrstm.uberbookingservice.controllers;

import org.mrstm.uberbookingservice.services.FareService;
import org.mrstm.uberentityservice.dto.fare.CalculatedFareDTO;
import org.mrstm.uberentityservice.dto.fare.EstimateFareRequestDto;
import org.mrstm.uberentityservice.dto.fare.FareRateDto;
import org.mrstm.uberentityservice.models.CarType;
import org.mrstm.uberentityservice.models.FareRate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class FareController {

    private String surgeMultiplier;
    private String discountPercent;

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/{bookingId}/fare/{carType}/calculate")
    public ResponseEntity<CalculatedFareDTO> calculateFare(@PathVariable String bookingId , @PathVariable String carType){
        return new ResponseEntity<>(fareService.calculateAndSaveFare(Long.parseLong(bookingId) , CarType.valueOf(carType.toUpperCase()), 10) , HttpStatus.OK);
    }

    @PostMapping("/fare/estimate")
    public ResponseEntity<CalculatedFareDTO> estimateFare(@RequestBody EstimateFareRequestDto estimateFareRequestDto){
        return new ResponseEntity<>(fareService.estimateFare(estimateFareRequestDto, 10) , HttpStatus.OK);
    }

    @PostMapping("/fare/add-rate") //jsut for production
    public ResponseEntity<String> addNewFareRate(@RequestBody FareRateDto fareRateDto){
        return new ResponseEntity<>(fareService.addNewFareRate(fareRateDto) , HttpStatus.CREATED);
    }
}
