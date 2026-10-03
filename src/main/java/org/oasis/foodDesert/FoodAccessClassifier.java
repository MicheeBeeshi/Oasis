package org.oasis.foodDesert;

import org.springframework.stereotype.Service;

@Service
public class FoodAccessClassifier {

    public FoodAccessResult classify(
            Neighborhood neighborhood,
            double minimumNearbyStores) {

        if (minimumNearbyStores <= 0) {
            throw new IllegalArgumentException(
                    "The store threshold must be greater than zero.");
        }

        int nearbyStores = neighborhood.freshFoodStoreCount();

        boolean lowAccess =
                neighborhood.population() > 0
                && nearbyStores < minimumNearbyStores;

        String category = determineCategory(
                neighborhood,
                nearbyStores,
                minimumNearbyStores
        );

        return new FoodAccessResult(
                neighborhood.geoid(),
                neighborhood.name(),
                neighborhood.population(),
                neighborhood.urban(),
                neighborhood.accessRadiusMiles(),
                neighborhood.lowIncome(),
                neighborhood.povertyRate(),
                neighborhood.medianFamilyIncome(),
                neighborhood.freshFoodStoreCount(),
                neighborhood.usdaLowAccess(),
                neighborhood.usdaLowIncomeLowAccess(),
                neighborhood.usdaLowAccessPopulation(),
                neighborhood.usdaLowAccessPopulationShare(),
                neighborhood.usdaStatus(),
                lowAccess,
                category,
                neighborhood.latitude(),
                neighborhood.longitude()
        );
    }

    private String determineCategory(
            Neighborhood neighborhood,
            int nearbyStores,
            double threshold) {

        if (neighborhood.population() <= 0) {
            return "Not rated";
        }

        if (nearbyStores == 0) {
            return "Very low access";
        }

        if (nearbyStores < threshold) {
            return "Low access";
        }

        if (nearbyStores < threshold * 2) {
            return "Moderate access";
        }

        return "High access";
    }
}