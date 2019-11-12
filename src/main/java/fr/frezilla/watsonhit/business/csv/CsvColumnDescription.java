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
    private boolean displayed;
    private boolean id;
    private boolean ignoreSpecialCharacters;
    private boolean matchCase;
    private final String name;
    private float weight;

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

    private void setWeight(float w) {
        if (w < 0.0f || w > 1.0f) {
            throw new IllegalArgumentException("weight must be between 0.0 and 1.0");
        }
        weight = w;
    }


    @Override
    public String toString() {
        return String.format(
                "[name = <%s>], "
                + "[description = <%s>], "
                + "[displayed = <%s>], "
                + "[id = %s], "
                + "[ignoreSpecialCharacters = %s], "
                + "[matchCase = %s], "
                + "[weight = %f]",
                name, description, returnOuiNon(displayed), returnOuiNon(id), returnOuiNon(ignoreSpecialCharacters), returnOuiNon(matchCase), weight);
    }

    public static class Builder {

        private String description = "";
        private boolean displayed = false;
        private boolean id = false;
        private boolean ignoreSpecialCharacters = false;
        private boolean matchCase = true;
        private final String name;
        private float weight = 0.0f;

        public Builder(String name) {
            this.name = name;
        }

        public CsvColumnDescription build() {
            CsvColumnDescription instance = new CsvColumnDescription(name);
            instance.setDescription(description);
            instance.setDisplayed(displayed);
            instance.setId(id);
            instance.setIgnoreSpecialCharacters(ignoreSpecialCharacters);
            instance.setMatchCase(matchCase);
            instance.setWeight(weight);
            return instance;
        }
        
        public Builder isDisplayed() {
            return isDisplayed(true);
        }

        public Builder isDisplayed(boolean b) {
            displayed = b;
            return this;
        }
        
        public Builder isId() {
            return isId(true);
        }

        public Builder isId(boolean b) {
            this.id = b;
            return this;
        }

        public Builder ignoreSpeacialCharacters() {
            return ignoreSpeacialCharacters(true);
        }

        public Builder ignoreSpeacialCharacters(boolean b) {
            this.ignoreSpecialCharacters = b;
            return this;
        }

        public Builder matchCase() {
            return matchCase(true);
        }

        public Builder matchCase(boolean b) {
            this.matchCase = b;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return null;
        }

        public Builder setWeight(float w) {
            this.weight = w;
            return this;
        }

    }
}
