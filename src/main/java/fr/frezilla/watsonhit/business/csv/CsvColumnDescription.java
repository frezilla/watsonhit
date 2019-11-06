package fr.frezilla.watsonhit.business.csv;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter(AccessLevel.PRIVATE)
public final class CsvColumnDescription {

    private String description;
    private boolean id;
    private boolean ignoreSpecialCharacters;
    private boolean matchCase;
    private final String name;
    private double weight;

    private CsvColumnDescription(@NonNull String n) {
        if (StringUtils.isEmpty(n)) {
            throw new IllegalArgumentException("name can not be empty");
        }
        description = "";
        name = n;
    }
    private String returnOuiNon(boolean b) {
        return b ? "oui" : "non";
    }

    private void setDescription(@NonNull String d) {
        description = d;
    }

    private void setWeight(double w) {
        if (w < 0.0 || w > 1.0) {
            throw new IllegalArgumentException("weight must be between 0.0 and 1.0");
        }
        weight = w;
    }


    @Override
    public String toString() {
        return String.format(
                "[name = <%s>], "
                + "[description = <%s>], "
                + "[id = %s], "
                + "[ignoreSpecialCharacters = %s], "
                + "[matchCase = %s], "
                + "[weight = %f]",
                name, description, returnOuiNon(id), returnOuiNon(ignoreSpecialCharacters), returnOuiNon(matchCase), weight);
    }

    public static class Build {

        private String description = "";
        private boolean id = false;
        private boolean ignoreSpecialCharacters = false;
        private boolean matchCase = true;
        private final String name;
        private double weight = 0.0;

        public Build(String name) {
            this.name = name;
        }

        public CsvColumnDescription build() {
            CsvColumnDescription instance = new CsvColumnDescription(name);
            instance.setDescription(description);
            instance.setId(id);
            instance.setIgnoreSpecialCharacters(ignoreSpecialCharacters);
            instance.setMatchCase(matchCase);
            instance.setWeight(weight);
            return instance;
        }

        public Build isId() {
            return isId(true);
        }

        public Build isId(boolean b) {
            this.id = b;
            return this;
        }

        public Build ignoreSpeacialCharacters() {
            return ignoreSpeacialCharacters(true);
        }

        public Build ignoreSpeacialCharacters(boolean b) {
            this.ignoreSpecialCharacters = b;
            return this;
        }

        public Build matchCase() {
            return matchCase(true);
        }

        public Build matchCase(boolean b) {
            this.matchCase = b;
            return this;
        }

        public Build setDescription(String description) {
            this.description = description;
            return null;
        }

        public Build setWeight(double w) {
            this.weight = w;
            return this;
        }

    }
}
