package org.mrstm.uberbookingservice.controllers;

import org.mrstm.uberbookingservice.common.CustomUserPrincipal;
import org.mrstm.uberbookingservice.services.FareService;
import org.mrstm.uberentityservice.dto.fare.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fare")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

//    @PostMapping("/{bookingId}/fare/calculate")
//    public ResponseEntity<CalculatedFareDTO> calculateFare(@PathVariable String bookingId){
//        return new ResponseEntity<>(fareService.calculateAndSaveFare(Long.parseLong(bookingId) , 10), HttpStatus.OK);
//    }

    @PostMapping("/estimate")
    public ResponseEntity<CalculatedFareDTO> estimateFare(@RequestBody EstimateFareRequestDto estimateFareRequestDto){
        return new ResponseEntity<>(fareService.estimateFare(estimateFareRequestDto, 10) , HttpStatus.OK);
    }

    @PostMapping("/add-rate") //jsut for production
    public ResponseEntity<String> addNewFareRate(@RequestBody FareRateDto fareRateDto){
        return new ResponseEntity<>(fareService.addNewFareRate(fareRateDto) , HttpStatus.CREATED);
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsResponseDto> getEarningsOfDriver(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {

        Long driverId = userPrincipal.getId();
        if (fromDate == null || toDate == null) {
            toDate = LocalDate.now();
            fromDate = toDate.minusDays(30);
        }
        return ResponseEntity.ok(fareService.getEarningsOfDriver(driverId, fromDate, toDate));
    }

    @GetMapping("/analytics/daily")
    public ResponseEntity<List<DailyEarningsDto>> getDailyEarningsOfDriver(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {

        Long driverId = userPrincipal.getId();
        if (fromDate == null || toDate == null) {
            toDate = LocalDate.now();
            fromDate = toDate.minusDays(30);
        }
        return ResponseEntity.ok(fareService.getDailyEarningsBetween(driverId, fromDate, toDate));
    }


}
