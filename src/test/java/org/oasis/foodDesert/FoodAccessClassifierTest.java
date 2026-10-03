package org.oasis.foodDesert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FoodAccessClassifierTest {

    private final FoodAccessClassifier classifier =
            new FoodAccessClassifier();

    @Test
    void zeroStoresProducesVeryLowAccess() {
        Neighborhood neighborhood = new Neighborhood(
                "06037601002",
                "Los Angeles County Census Tract 6010.02",
                5457,
                true,
                1.0,
                true,
                0.208,
                94417,
                0,
                true,
                true,
                2000,
                0.3665,
                "Low-income and low-access",
                33.956542,
                -118.347596
        );

        FoodAccessResult result =
                classifier.classify(neighborhood, 3);

        assertEquals("Very low access", result.category());
        assertTrue(result.lowAccess());
        assertEquals(0, result.freshFoodStoreCount());
        assertTrue(result.usdaLowAccess());
        assertTrue(result.usdaLowIncomeLowAccess());
    }

    @Test
    void fourStoresProducesModerateAccess() {
        Neighborhood neighborhood = new Neighborhood(
                "06001400200",
                "Alameda County Census Tract 4002",
                2001,
                true,
                1.0,
                false,
                0.077,
                250001,
                4,
                false,
                false,
                0,
                0.0,
                "Not low-access",
                37.848128,
                -122.249603
        );

        FoodAccessResult result =
                classifier.classify(neighborhood, 3);

        assertEquals("Moderate access", result.category());
        assertFalse(result.lowAccess());
        assertEquals(4, result.freshFoodStoreCount());
        assertFalse(result.usdaLowAccess());
    }

    @Test
    void ruralRadiusIsPreserved() {
        Neighborhood neighborhood = new Neighborhood(
                "06051000100",
                "Mono County Census Tract 1",
                2200,
                false,
                10.0,
                false,
                0.10,
                65000,
                6,
                false,
                false,
                100,
                0.0455,
                "Not low-access",
                37.90,
                -118.80
        );

        FoodAccessResult result =
                classifier.classify(neighborhood, 3);

        assertEquals("High access", result.category());
        assertEquals(10.0, result.accessRadiusMiles());
        assertEquals("Not low-access", result.usdaStatus());
    }

    @Test
    void invalidThresholdIsRejected() {
        Neighborhood neighborhood = new Neighborhood(
                "06001400100",
                "Test Census Tract",
                1000,
                true,
                1.0,
                false,
                0.10,
                80000,
                2,
                false,
                false,
                0,
                0.0,
                "Not low-access",
                37.80,
                -122.20
        );

        boolean exceptionThrown = false;

        try {
            classifier.classify(neighborhood, 0);
        } catch (IllegalArgumentException exception) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }
}