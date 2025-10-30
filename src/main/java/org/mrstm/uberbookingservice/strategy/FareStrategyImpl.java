package org.mrstm.uberbookingservice.strategy;

import org.mrstm.uberentityservice.models.FareRate;
import org.springframework.stereotype.Component;

@Component
public class FareStrategyImpl implements FareStrategy {

    @Override
    public double calculate(FareRate fareRate, double distance, double duration, double surge, double discount) {
        double baseFare = fareRate.getBaseFare();
        double perKmRate = fareRate.getPerKmRate();
        double perMinRate = fareRate.getPerMinRate();
        double minFare = fareRate.getMinFare();
        double fare = baseFare + (distance * perKmRate) + (duration * perMinRate);
        fare *= surge;
        fare = Math.max(fare, minFare);
        if (discount > 0) {
            fare -= fare * (discount / 100.0);
        }
        return Math.round(fare);
    }
}
