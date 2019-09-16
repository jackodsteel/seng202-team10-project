package dataHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Creates a GeoApiContext for geocoding. There should never be two instances of this class active at one time
 */
public class Geocoder {

    private static GeoApiContext context;

    /**
     * Creates a GeoApiContext object to be used for searching.
     * Currently uses a static API key
     */
    public static void init() {
        context = new GeoApiContext.Builder()
                .apiKey("AIzaSyDY5q_c8hgNgdiT3iMBhH43pIJtvQ0_aaM")
                .connectTimeout((long) 300, TimeUnit.MILLISECONDS)
                .maxRetries(0)
                .queryRateLimit(40)
                .build();
    }

    /**
     * Takes an address and uses Google Geocoding API to get a latitude and longitude.
     *
     * @param address A string specifying the address to search
     * @return The first result from the geocode search
     * @throws ApiException
     * @throws IOException
     * @throws InterruptedException
     * @throws ArrayIndexOutOfBoundsException
     */
    public static double[] addressToLatLon(String address) throws ApiException, IOException, InterruptedException, ArrayIndexOutOfBoundsException {
        GeocodingResult[] results;
        results = GeocodingApi.geocode(context, address).await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        double lat = Double.parseDouble(gson.toJson(results[0].geometry.location.lat));
        double lon = Double.parseDouble(gson.toJson(results[0].geometry.location.lng));
        return new double[]{lat, lon};
    }

    /**
     * Checks the database by testing a random address. If the test fails, the error popup will show.
     *
     * @return If a value was successfully returned
     */
    public static boolean testConnection() {
        try {
            return (addressToLatLon("123 Fake St") != null);
        } catch (ApiException | IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * A simple async request
     *
     * @param address A String address to geocode
     * @param outcome The GeocoderOutcome object to callback on
     */
    public static void addressToLatLonAsync(String address, GeocodeOutcome outcome) {
        GeocodingApi.geocode(context, address).setCallback(outcome);
    }
}
