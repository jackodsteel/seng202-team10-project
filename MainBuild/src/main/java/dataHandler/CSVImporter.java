package dataHandler;

import com.opencsv.CSVReader;
import javafx.concurrent.Task;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class CSVImporter extends Task<Void> implements SuccessCallback {

    private final String url;
    private SQLiteDB db;
    private DataHandler handler;
    private int successful = 0;
    private int failed = 0;
    private int resulted = 0;
    private int totalCount;

    /**
     * Initializes an object, linked to the given database. Can process CSVs and add single entries
     *
     * @param db
     * @param url
     */
    public CSVImporter(SQLiteDB db, String url, DataHandler handler) {
        this.db = db;
        this.url = url;
        this.handler = handler;
    }


    @Override
    protected Void call() throws Exception {
        System.out.println("here");
        updateProgress(0, 1);
        db.setAutoCommit(false);
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(url), ',');
        } catch (FileNotFoundException e) {
            System.out.println("here");
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
}

