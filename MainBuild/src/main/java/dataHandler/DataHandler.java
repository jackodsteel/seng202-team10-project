package dataHandler;

public interface DataHandler {

    void processLine(String[] record, SuccessCallback successCallback);

    boolean canProcess(int columnCount);

    String getFieldCounts();

}
