package fr.frezilla.watsonhit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

@Getter(AccessLevel.PACKAGE)
class WatsonHitParameters {

    @NonNull
    private final String csvDelimiter;

    @NonNull
    private final String csvDescriptorFile;

    @NonNull
    private final String csvFile;
    
    private boolean helpMode;

    private double minSimilarity;
    
    @NonNull
    private final String resultFile;

    WatsonHitParameters(boolean helpMode) {
        this.csvDelimiter = null;
        this.csvDescriptorFile = null;
        this.csvFile = null;
        this.helpMode = helpMode;
        this.resultFile = null;
    }

    WatsonHitParameters(String csvDelimiter, String csvDescriptorFile, String csvFile, double minSimilarity, String resultFile) {
        if (minSimilarity < 0.0 || minSimilarity > 100.0) {
            throw new IllegalArgumentException();
        }
        this.csvDelimiter = csvDelimiter;
        this.csvDescriptorFile = csvDescriptorFile;
        this.csvFile = csvFile;
        this.minSimilarity = minSimilarity;
        this.resultFile = resultFile;
    }

    @Override
    public String toString() {
        return String.format("-> délimiteur csv : <%s>\n"
                + "-> fichier de description : <%s>\n"
                + "-> fichier csv : <%s>\n"
                + "-> fichier résultat : <%s>",
                csvDelimiter, csvDescriptorFile, csvFile, resultFile);
    }
}
