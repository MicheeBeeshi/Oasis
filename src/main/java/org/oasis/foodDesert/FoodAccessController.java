package org.oasis.foodDesert;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/neighborhoods")
public class FoodAccessController {

    private final NeighborhoodCsvRepository repository;
    private final FoodAccessClassifier classifier;

    public FoodAccessController(
            NeighborhoodCsvRepository repository,
            FoodAccessClassifier classifier) {

        this.repository = repository;
        this.classifier = classifier;
    }

    @GetMapping
    public List<FoodAccessResult> all(
            @RequestParam(
                    name = "minimumNearbyStores",
                    defaultValue = "3.0"
            )
            double minimumNearbyStores) {

        return repository.findAll()
                .stream()
                .map(neighborhood ->
                        classifier.classify(
                                neighborhood,
                                minimumNearbyStores
                        ))
                .toList();
    }
}