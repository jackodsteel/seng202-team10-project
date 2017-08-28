package mapElements;

/**
 * Holds data for key locations on the NY map.
  */

public class MapKeyLocations {
    int longitude;
    int latitute;
    String name;
    String city;

    /**
     * Constructor for MapKeyLocations.
     */
    public MapKeyLocations() {
        longitude = 0;
        latitute = 0;
        name = "NULL";
        city = "NULL";
    }

    public MapKeyLocations(int locationLongitude, int locationLatitude, String locationName, String locationCity) {
        longitude = locationLongitude;
        latitute = locationLatitude;
        name = locationName;
        city = locationCity;
    }

    /**
     * Getters and setters.
     */

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int input) {
        longitude = input;
    }

    public int getLatitute() {
        return latitute;
    }

    public void setLatitute(int input) {
        latitute = input;
    }

}