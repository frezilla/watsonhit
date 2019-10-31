package fr.frezilla.watsonhit.reader.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import lombok.NonNull;

public class CSVReader {

    public static String DEFAULT_DELIMITER = ",";

    private String currentLine;
    private final BufferedReader bufferedReader;
    private String delimiter;
    private String[] headerValues;
    private boolean isHeaderRead;
    private boolean withHeader;

    private CSVReader(@NonNull String csvPath) throws FileNotFoundException {
        this.bufferedReader = new BufferedReader(new FileReader(csvPath));
        this.currentLine = null;
        this.delimiter = DEFAULT_DELIMITER;
        this.headerValues = null;
        this.isHeaderRead = false;
        this.withHeader = false;
    }

    public String[] getHeader() {
        String[] values;
        if (headerValues == null) {
            values = null;
        } else {
            int headerValuesLength = headerValues.length;
            values = new String[headerValuesLength];
            System.arraycopy(headerValues, 0, values, 0, headerValuesLength);    
        }
        return values;
    }

    public void close() throws IOException {
        bufferedReader.close();
    }

    public boolean hasNext() throws IOException {
        return ((currentLine = bufferedReader.readLine()) != null);
    }

    public String[] next() {
        String[] columns;
        if (currentLine == null) {
            columns = null;
        } else {
            columns = currentLine.split(delimiter);
            
            if (withHeader && !isHeaderRead) {
                isHeaderRead = true;
                int columnsLength = columns.length;
                headerValues = new String[columnsLength];
                System.arraycopy(columns, 0, headerValues, 0, columnsLength);
            }
            
            currentLine = null;
        }
        return columns;
    }

    public static class Builder {

        private final String csvPath;
        private String delimiter;
        private boolean withHeader;

        public Builder(@NonNull String csvPath) {
            this.csvPath = csvPath;
            this.withHeader = false;
        }

        public CSVReader build() throws FileNotFoundException {
            CSVReader instance = new CSVReader(csvPath);
            if (delimiter != null) {
                instance.delimiter = delimiter;
            }
            instance.withHeader = withHeader;

            return instance;
        }

        public Builder setDelimiter(@NonNull String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public Builder withHeader(boolean withHeader) {
            this.withHeader = withHeader;
            return this;
        }

    }
}
