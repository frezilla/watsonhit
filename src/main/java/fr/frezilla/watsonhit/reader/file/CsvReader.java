package fr.frezilla.watsonhit.reader.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import lombok.NonNull;

public final class CsvReader {

    public static String DEFAULT_DELIMITER = ",";

    private final BufferedReader bufferedReader;
    private String currentLine;
    private String delimiter;
    private String[] headerValues;
    private boolean isHeaderRead;
    private boolean withHeader;

    private CsvReader(@NonNull String csvPath) throws FileNotFoundException {
        this.bufferedReader = new BufferedReader(new FileReader(csvPath));
        this.currentLine = null;
        this.delimiter = DEFAULT_DELIMITER;
        this.headerValues = null;
        this.isHeaderRead = false;
        this.withHeader = false;
    }

    public static Builder builder(String csvPath) {
        return new Builder(csvPath);
    }

    public void close() throws IOException {
        bufferedReader.close();
    }
    public String[] getHeader() {
        String[] values;
        if (headerValues == null) {
            values = null;
        } else {
            int headerColumnsLength = headerValues.length;
            values = new String[headerColumnsLength];
            System.arraycopy(headerValues, 0, values, 0, headerColumnsLength);
        }
        return values;
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
                int headerColumnsLength = columns.length;
                headerValues = new String[headerColumnsLength];
                System.arraycopy(columns, 0, headerValues, 0, headerColumnsLength);
            }
            currentLine = null;
        }
        return columns;
    }

    public static class Builder {

        private final String csvPath;
        private String delimiter;
        private boolean withHeader;

        Builder(@NonNull String csvPath) {
            this.csvPath = csvPath;
            this.withHeader = false;
        }

        public CsvReader build() throws FileNotFoundException {
            CsvReader instance = new CsvReader(csvPath);
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

        public Builder withHeader() {
            this.withHeader = true;
            return this;
        }
    }
}
