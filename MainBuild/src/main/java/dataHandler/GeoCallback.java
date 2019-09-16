package dataHandler;

public interface GeoCallback {
    void result(String[] record, double[] latlon, SuccessCallback successCallback);
}
