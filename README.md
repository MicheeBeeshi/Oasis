# Oasis — California Food Access

## Live Application
[Open the Oasis California Food Access Map](https://oasis-dyxy.onrender.com/)
The application may take a short time to start after a period of inactivity because it is hosted on Render.

Oasis is an interactive web map for exploring food access across California census tracts. It combines tract-level population and income information, official USDA food-access indicators, grocery-store and farmers-market locations, and a project-specific nearby-store classification.

The application is intended as an informational reference for people comparing places to live and for anyone interested in the relationship between food access and neighborhood-level economic conditions. It does not decide whether a place is suitable for a particular person, and it should not be treated as an official eligibility, planning, or real-estate tool.

## Why this project exists

Food access is more complicated than the cost to purchase alone. Lower-income areas tend to have lower access to fresh food in general. Areas with mininal to no access to fresh food are known as "Food Deserts".
This project is meant to inform the user of areas affected by this systemic inequity as well as allow them to make informed choices when looking to move to a neighborhood. 
Oasis displays all the information a person the food accesibility of any area in the state in a single map. It does this through two main classification methods:

- **USDA access classification**, based on the USDA low-access flag included in it's annual tract dataset.
- **Oasis store availability**, based on the number of mapped SNAP-authorized grocery stores and farmers markets within a distance radius.

This separation lets users view official USDA facts about a tract while also exploring the nearby-store environment. 
The USDA method is based on a tract-wide population distribution, while the Oasis method is based on are count of stores in radius of the tract center.

## Current features

- Shades all available California census-tract polygons.
- Switches between USDA access and Oasis nearby-store levels from either the map settings or the draggable map key.
- Uses a 1-mile radius for urban tracts and a 10-mile radius for rural tracts.
- Shows grocery stores, supermarkets, super stores, and farmers markets as map markers.
- Displays tract name, population, median family income, poverty rate, urban/rural status, store count, and USDA fields in tract popups.
- Lets users change the Oasis threshold that begins the moderate-access range.
- Searches a California home address and counts mapped stores within 1 or 10 miles of that address.
- Allows automatic urban/rural home-search distance or a manually selected 1-mile or 10-mile calculation.
- Draws the home-search radius as a translucent circle colored by its classification.
- Includes a draggable map key that explains tract shading, home-search colors, and store markers.
- Provides JSON API endpoints for tract results and store locations.
- Includes JUnit tests for the core Oasis classification logic.

## Geographic terminology

The mapped areas are **census tracts**, not formally defined neighborhoods. Tracts are small statistical areas created by the U.S. Census Bureau. A tract may approximate part of a neighborhood, contain multiple communities, or cross locally understood neighborhood boundaries.

## Classification methods

### USDA access mode

USDA mode shades tracts using only the imported `usda_low_access` value:

| Access flag | Map category |
|---|---|
| `true` | Low access |
| `false` | Not low access |

For the 1-mile/10-mile measure represented by this project, USDA classifies a tract as low access when at least 500 residents or at least 33% of its population lives farther than 1 mile from a qualifying retailer in an urban tract or 10 miles in a rural tract. The method evaluates the distribution of residents within the tract; it is not a simple count around the tract center.

The tract popup reports the imported number and percentage of residents represented as beyond the threshold. It continues to show median family income, poverty rate, and the low-income flag as separate context, but those fields do not determine the shading.

USDA access is **not recalculated from the marker data in the browser or Java service**. It is imported into the runtime tract dataset. This distinction matters because USDA source methodology and the Oasis store list are not interchangeable.

### Oasis mode

Oasis mode classifies a tract using `fresh_food_store_count`, which is a precomputed count associated with the tract. The current Java application reads that count from `neighborhoods.csv`; it does not perform the original store-to-tract distance calculation when the server starts.

Let `T` be the user-selected **moderate-access minimum**. The default is `T = 3`.

| Oasis category | Rule | Default range |
|---|---:|---:|
| Very low access | `stores = 0` | 0 stores |
| Low access | `0 < stores < T` | 1–2 stores |
| Moderate access | `T ≤ stores < 2T` | 3–5 stores |
| High access | `stores ≥ 2T` | 6 or more stores |
| Not rated | Population is 0 | Not applicable |

`lowAccess` is `true` when a populated tract has fewer stores than the selected threshold. Income does not change the Oasis category; it is displayed separately.

For the tract map, the count represents stores around the tract's supplied center coordinates using the tract's `access_radius_miles` value: normally 1 mile for urban tracts and 10 miles for rural tracts.

### Home-address calculation

The browser sends the entered address to the OpenStreetMap Nominatim search service and restricts results to California. It then locates the census tract containing the returned coordinate and changes the home result with the selected classification mode:

- **USDA access mode:** The circle and popup use the imported USDA access classification of the containing tract. The circle uses that tract's official 1-mile urban or 10-mile rural threshold. Income does not affect the result. This is a tract classification, not a new USDA calculation for the individual address.
- **Oasis mode:** The browser calculates straight-line great-circle distance from the home coordinate to every loaded store using the Haversine formula, counts stores within the selected automatic or manual radius, and applies the current Oasis threshold. The original Very low, Low, Moderate, and High levels determine the circle color.

In both modes, the popup reports the number of mapped grocery stores and farmers markets inside the displayed circle as additional context. Changing the mode, Oasis threshold, or home-radius option refreshes an existing home result.

## Data included in this export

| File | Runtime purpose |
|---|---|
| `src/main/resources/data/neighborhoods.csv` | 9,109 California tract records used by the Java API. |
| `src/main/resources/data/california_stores.csv` | 8,401 California grocery and farmers-market records used by the store API and home search. |
| `src/main/resources/static/data/california_tracts.json` | 9,109 GeoJSON tract boundaries used to shade the map and locate a searched address within a tract. |
| `src/main/resources/data/sram_tracts.csv.csv` | Source/intermediate tract data retained in the project; it is not read directly by the running application. |

The store file currently contains these `Store_Type` values:

- `Grocery Store`
- `Supermarket`
- `Super Store`
- `Farmers and Markets`

The project was assembled from USDA tract/access data, California records obtained through the SNAP Retailer Locator dataset, Census tract boundaries, and OpenStreetMap services. Before using the project for research or publication, record the exact source URLs, dataset editions, download dates, preprocessing steps, and licenses for the copies included here.

### Important data caveat

Farmers Markerts are marked seperately from other stores as they may be seasonal or operate only on particular days.

A location's inclusion in the store file does not guarantee its current operating status, hours, affordability, inventory, or consistent availability of fresh produce. The project treats the listed store types as qualifying food locations; it does not independently verify the fresh-food selection at every location. Store openings, closures, relocations, duplicates, missing coordinates, and farmers-market schedules can affect results.

## CSV schemas

### `neighborhoods.csv`

| Column | Description |
|---|---|
| `geoid` | 11-digit census-tract identifier; leading zero must be preserved. |
| `name` | Human-readable county and tract name. |
| `population` | Tract population. |
| `urban` | `true` for urban and `false` for rural. |
| `access_radius_miles` | Radius associated with the tract, normally 1 or 10. |
| `low_income` | Imported low-income tract flag. |
| `poverty_rate` | Decimal rate, such as `0.208` for 20.8%. |
| `median_family_income` | Median family income in dollars. |
| `fresh_food_store_count` | Precomputed qualifying-store count within the tract's radius. |
| `latitude`, `longitude` | Tract center used by the Oasis count-generation process. |
| `usda_low_access` | Imported USDA low-access flag. |
| `usda_low_income_low_access` | Imported combined low-income/low-access flag. |
| `usda_low_access_population` | Number of residents represented as beyond the USDA threshold. |
| `usda_low_access_population_share` | Decimal share of tract residents represented as beyond the threshold. |
| `usda_status` | Display-ready USDA category. |

### `california_stores.csv`

The runtime repository reads these columns:

| Column | Description |
|---|---|
| `Store_Name` | Store or market name. |
| `Store_Type` | One of the supported store types. |
| `Store_Street_Address` | Street address. |
| `City` | City. |
| `County` | County name. |
| `Latitude`, `Longitude` | Marker and distance-calculation coordinates. |

Additional source columns may remain in the CSV but are not returned by the API.

## Technology

- Java and Spring Boot 3.5.6
- Maven
- Apache Commons CSV
- JUnit 5 through Spring Boot Test
- HTML, CSS, and browser JavaScript
- Leaflet 1.9.4
- OpenStreetMap map tiles and Nominatim address search
- GeoJSON census-tract boundaries

## Requirements

- Apache Maven 3.9 or newer
- A compatible JDK
- Internet access for the Leaflet CDN, OpenStreetMap tiles, and address search
- A modern web browser with JavaScript enabled

The current `pom.xml` compiles the project with Java release 25. This export was tested by running Maven on JDK 26 while targeting Java 25 because Spring Boot 3.5.6 could not process Java 26 class files (`major version 70`). Seeing JDK 26 in `mvn -version` is therefore acceptable as long as the build output says `release 25`.

## Run from PowerShell

Open PowerShell in the directory containing `pom.xml`, or change to it explicitly:

```powershell
cd "C:\Users\USERNAME\Directory\Path\...Oasis"
```

Run the automated tests:

```powershell
mvn clean test
```

Start the application:

```powershell
mvn spring-boot:run
```

After the console reports that `FoodAccessApplication` has started, open:

<http://localhost:8080>

Keep PowerShell open while using the site. Press `Ctrl+C` to stop the server.

### OneDrive file-lock issue

If Maven reports that it cannot delete a path inside `target`, stop the application, close Eclipse, and temporarily pause OneDrive before rerunning the command. The long-term solution is to keep the development copy outside a synchronized folder, for example:

```text
C:\Projects\california-food-access-java
```

The `target` directory contains generated build files and can be deleted safely while the application is stopped.

## Import into Eclipse

1. Select **File → Import**.
2. Choose **Maven → Existing Maven Projects**.
3. Select the directory containing `pom.xml`.
4. Finish the import.
5. Right-click the project and select **Maven → Update Project**.

The intended source layout is:

```text
src/main/java
src/main/resources
src/test/java
```

Do not separately add `src`, `src/main`, or `src/test` as Java source folders because that creates nested-source-folder errors. The absence of `src/test/resources` is harmless because the current tests do not require test resources.

Eclipse can still be used as the editor if its Run button is not configured correctly; a successful Maven build is the authoritative result.

## API

### `GET /api/neighborhoods`

Returns all tract food-access results as JSON.

Optional query parameter:

| Parameter | Default | Description |
|---|---:|---|
| `minimumNearbyStores` | `3.0` | Positive threshold at which Oasis moderate access begins. |

Example:

```text
http://localhost:8080/api/neighborhoods?minimumNearbyStores=3
```

The endpoint returns both Oasis classification fields and imported USDA fields. The threshold changes the Oasis `category` and `lowAccess` values; it does not change `usdaLowAccess`. The frontend uses `usdaLowAccess`—not income or the combined `usdaStatus` field—to shade USDA mode.

### `GET /api/stores`

Returns the loaded store and farmers-market records as JSON:

```text
http://localhost:8080/api/stores
```

This is an API response, so opening it directly displays JSON rather than a formatted webpage.

## Automated tests

`FoodAccessClassifierTest` currently verifies:

- Zero stores produces `Very low access`.
- Four stores with a threshold of three produces `Moderate access`.
- A rural tract preserves its 10-mile radius and USDA status.
- A zero or negative threshold is rejected.

Run the suite with:

```powershell
mvn clean test
```

The current suite tests the Java classifier. It does not yet automatically test CSV integrity, API responses, map rendering, address search, spatial containment, or browser interactions.

## Project structure

```text
california-food-access/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/org/oasis/foodDesert/
    │   │   ├── FoodAccessApplication.java
    │   │   ├── FoodAccessClassifier.java
    │   │   ├── FoodAccessController.java
    │   │   ├── FoodAccessResult.java
    │   │   ├── Neighborhood.java
    │   │   ├── NeighborhoodCsvRepository.java
    │   │   ├── Store.java
    │   │   ├── StoreController.java
    │   │   └── StoreCsvRepository.java
    │   └── resources/
    │       ├── data/
    │       └── static/
    │           ├── index.html
    │           └── data/california_tracts.json
    └── test/java/org/oasis/foodDesert/
        └── FoodAccessClassifierTest.java
```

## Limitations

- Census tracts are used as neighborhood proxies but are not synonymous with neighborhoods.
- Tract-level values do not describe every household or block within a tract.
- Oasis tract counts are precomputed and are not regenerated when the application starts.
- The home calculation uses straight-line distance, not road distance, travel time, public transit, terrain, or physical barriers.
- The tract-level Oasis count uses a tract center, so residents near a tract edge may have different access than the tract-wide display.
- A store marker does not establish food quality, price, cultural suitability, accessibility, hours, or current operation.
- Farmers markets may be seasonal or operate only on particular days.
- Address geocoding depends on an external service and may return an approximate or incorrect point.
- The browser loads all stores and tract geometry, which may affect performance on slower devices.
- Data can become outdated and should be refreshed before consequential use.
- The application is not an official USDA product and the Oasis categories are project-defined.

## Privacy

Home-address searches are performed in the browser through OpenStreetMap Nominatim. The address is therefore transmitted to that external service. Oasis does not provide its own database or server endpoint for saving searched addresses, but users should still avoid entering sensitive addresses if they do not want them sent to the geocoding provider.

## Coming Updates

- Add filters and marker clustering for performance and readability.
- Add accessible non-map results for users who cannot use the interactive map..
- Add a repeatable preprocessing program that assigns stores to tract-center radii and rebuilds `neighborhoods.csv`.
- Generalize state-specific filenames and preprocessing for nationwide expansion.
- Mental Health Resource Accessibility offshoot

## Disclaimer

Oasis is an educational project and an exploratory reference. Its classifications depend on the included data, preprocessing assumptions, distance method, and user-selected threshold. Verify important decisions with current official datasets, direct store information, local knowledge, and on-the-ground research.

## License

No software license is included in this export. Add a license before distributing or accepting outside contributions, and separately follow the attribution and licensing requirements of each source dataset and map service.
