package fr.frezilla.watsonhit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
class WatsonHitParameters {

    @NonNull
    private String csvDelimiter;

    @NonNull
    private String csvDescriptorFile;

    @NonNull
    private String csvFile;

    @NonNull
    private String resultFile;

    @Override
    public String toString() {
        return String.format("-> délimiteur csv : <%s>\n"
                + "-> fichier de description : <%s>\n"
                + "-> fichier csv : <%s>\n"
                + "-> fichier résultat : <%s>",
                csvDelimiter, csvDescriptorFile, csvFile, resultFile);
    }
}
