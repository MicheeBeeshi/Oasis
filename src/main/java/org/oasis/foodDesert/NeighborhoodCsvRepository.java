package org.oasis.foodDesert;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

@Repository
public class NeighborhoodCsvRepository {

    private final List<Neighborhood> neighborhoods;

    public NeighborhoodCsvRepository() throws IOException {
        ClassPathResource resource =
                new ClassPathResource("data/neighborhoods.csv");

        try (Reader reader = new InputStreamReader(
                resource.getInputStream(),
                StandardCharsets.UTF_8)) {

            neighborhoods = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(reader)
                    .stream()
                    .map(this::toNeighborhood)
                    .toList();
        }
    }

    public List<Neighborhood> findAll() {
        return neighborhoods;
    }

    private Neighborhood toNeighborhood(CSVRecord row) {
        return new Neighborhood(
                row.get("geoid"),
                row.get("name"),
                Integer.parseInt(row.get("population")),
                Boolean.parseBoolean(row.get("urban")),
                Double.parseDouble(row.get("access_radius_miles")),
                Boolean.parseBoolean(row.get("low_income")),
                Double.parseDouble(row.get("poverty_rate")),
                Integer.parseInt(row.get("median_family_income")),
                Integer.parseInt(row.get("fresh_food_store_count")),
                Boolean.parseBoolean(row.get("usda_low_access")),
                Boolean.parseBoolean(row.get("usda_low_income_low_access")),
                Integer.parseInt(row.get("usda_low_access_population")),
                Double.parseDouble(
                        row.get("usda_low_access_population_share")),
                row.get("usda_status"),
                Double.parseDouble(row.get("latitude")),
                Double.parseDouble(row.get("longitude"))
        );
    }
}