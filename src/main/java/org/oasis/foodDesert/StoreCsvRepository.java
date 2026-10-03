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
public class StoreCsvRepository {
	private final List<Store> stores;

    public StoreCsvRepository() throws IOException {
        ClassPathResource resource =
                new ClassPathResource("data/california_stores.csv");

        try (Reader reader = new InputStreamReader(
                resource.getInputStream(),
                StandardCharsets.UTF_8)) {

            stores = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(reader)
                    .stream()
                    .map(this::toStore)
                    .toList();
        }
    }


	public List<Store> findAll() {
	    return stores;
	}
	
	private Store toStore(CSVRecord row) {
	    return new Store(
	            row.get("Store_Name"),
	            row.get("Store_Type"),
	            row.get("Store_Street_Address"),
	            row.get("City"),
	            row.get("County"),
	            Double.parseDouble(row.get("Latitude")),
	            Double.parseDouble(row.get("Longitude"))
	    );
	}
}