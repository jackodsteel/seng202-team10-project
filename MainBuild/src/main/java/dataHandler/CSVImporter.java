package dataHandler;

import com.opencsv.CSVReader;
import javafx.concurrent.Task;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class CSVImporter extends Task<Void> implements SuccessCallback {

    private final String url;
    private final SQLiteDB db;
    private final DataHandler handler;

    private boolean isTest;

    private int successful = 0;
    private int failed = 0;
    private int resulted = 0;
    private int totalCount;

    /**
     * Creates an importer linked to the given SQLiteDB. Can process CSVs and add single entries
     * @param db Database to import to
     * @param url Local file URL to read the file from
     * @param handler DataHandler to process the CSV lines
     */
    public CSVImporter(SQLiteDB db, String url, DataHandler handler) {
        this.db = db;
        this.url = url;
        this.handler = handler;
        isTest = false;
    }

    /**
     * Set the testMode flag, which will avoid any JavaFX specific calls, thus removing the need for JavaFX to be running
     * Will prevent any progress updates from being delivered
     * @return The existing CSVImporter
     */
    public CSVImporter enableTestMode() {
        isTest = true;
        return this;
    }

    @Override
    public Void call() throws Exception {
        updateProgress(0, 1);
        db.setAutoCommit(false);
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(url), ',');
        } catch (FileNotFoundException e) {
            updateMessage("That file doesn't exist.\nPlease select a valid file");
            return null;
        }
        List<String[]> records = reader.readAll();
        if (!handler.canProcess(records.get(0).length)) {
            updateMessage(String.format(
                    "Incorrect number of fields, expected %s but got %d\n" +
                    "Did you select the correct CSV file?", handler.getFieldCounts(), records.get(0).length));

            return null;
        }
        totalCount = records.size() - 1;

        for (int i = 1; i <= totalCount; i++) {
            if (isCancelled()) {
                db.rollback();
                db.setAutoCommit(true);
                return null;
            }
            handler.processLine(records.get(i), this);
        }
        while (resulted < totalCount) {
            Thread.sleep(50);
            if (isCancelled()) {
                db.rollback();
                db.setAutoCommit(true);
                return null;
            }
        }
        db.commit();
        db.setAutoCommit(true);
        return null;
    }

    @Override
    public void result(boolean result) {
        resulted ++;
        if (result) {
            successful ++;
        } else {
            failed ++;
        }

        updateProgress(resulted, totalCount);

        updateMessage(String.format("Currently processed %d records out of %d.\n" +
                "Successfully imported: %d records\n" +
                "Failed to import: %d records.", resulted, totalCount, successful, failed));
    }

    @Override
    protected void updateProgress(long workDone, long max) {
        if (isTest) {
            return;
        }
        super.updateProgress(workDone, max);
    }

    @Override
    protected void updateMessage(String message) {
        if (isTest) {
            return;
        }
        super.updateMessage(message);
    }
}

