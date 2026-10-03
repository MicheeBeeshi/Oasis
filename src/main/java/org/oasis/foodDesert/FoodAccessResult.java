package org.oasis.foodDesert;

public record FoodAccessResult(
        String geoid,
        String name,
        int population,
        boolean urban,
        double accessRadiusMiles,
        boolean lowIncome,
        double povertyRate,
        int medianFamilyIncome,
        int freshFoodStoreCount,
        boolean usdaLowAccess,
        boolean usdaLowIncomeLowAccess,
        int usdaLowAccessPopulation,
        double usdaLowAccessPopulationShare,
        String usdaStatus,
        boolean lowAccess,
        String category,
        double latitude,
        double longitude
) {
}
