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
    private boolean isHeaderRead;
    private boolean withHeader;

    private CSVReader(@NonNull String csvPath) throws FileNotFoundException {
        this.currentLine = null;
        this.delimiter = DEFAULT_DELIMITER;
        this.bufferedReader = new BufferedReader(new FileReader(csvPath));
        this.isHeaderRead = false;
        this.withHeader = false;
    }

    public String[] getHeader() {
        // TODO : Implémenter la méthode
        return null;
    }

    public void close() throws IOException {
        bufferedReader.close();
    }

    public boolean hasNext() throws IOException {
        return ((currentLine = bufferedReader.readLine()) != null);
    }

    public String[] next() {
        //TODO : Filter la ligne d'entête si cette ligne doit être présente
        String[] columns;
        if (currentLine == null) {
            columns = null;
        } else {
            columns = currentLine.split(delimiter);
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
