package dataHandler;


import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * These tests can't run reliably as they rely on an outside source.
 * These can be run specifically as long as there is a valid internet connection and API key
 */
@Ignore
public class GeocoderTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void MakeGeoApiContext() {
        Geocoder.init();
    }

    @Test
    public void addressToLatLonInvalidAddress() throws Exception {
        exception.expect(ArrayIndexOutOfBoundsException.class);
        Geocoder.addressToLatLon("ThisIsNotARealAddress");
    }

    @Test
    public void addressToLatLonValidAddress() throws Exception {
        double[] latLon = Geocoder.addressToLatLon("Corner Science Road and Engineering Road, Christchurch, New Zealand");
        System.out.println(latLon[0]);
        System.out.println(latLon[1]);
        assertEquals(-43.5225225, latLon[0], 0.001);
        assertEquals(172.5806867, latLon[1], 0.001);
    }

    @Test
    public void testConnection() {
        assertTrue(Geocoder.testConnection());
    }

}