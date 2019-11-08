package fr.frezilla.watsonhit.business.csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Charge la description du fichier Csv depuis un fichier Xml.
 */
public class CsvDescription {

    /**
     * Retourne le builder lié à la classe
     *
     * @param fileName
     * @return
     */
    public static Builder builder(String fileName) {
        return new Builder(fileName);
    }

    private final List<CsvColumnDescription> columnsDefinitions;

    private CsvDescription(String fileName) throws ConfigurationException, IOException, JDOMException {
        columnsDefinitions = new ArrayList<>();
        loadXmlFile(fileName);
    }

    /**
     * Retourne la liste des descriptions des colonnes
     *
     * @return
     */
    public List<CsvColumnDescription> getColumnsDescription() {
        return new ArrayList<>(columnsDefinitions);
    }

    /**
     * Charge la description du fichier csv depuis le fichier xml de
     * configuration.
     *
     * @param fileName
     * @throws ConfigurationException
     * @throws IOException
     * @throws JDOMException
     */
    private void loadXmlFile(String fileName) throws ConfigurationException, IOException, JDOMException {
        SAXBuilder sxb = new SAXBuilder();
        Document document = sxb.build(new File(fileName));
        Element racine = document.getRootElement();
        Element columns = racine.getChild("columns");

        List<Element> colElts = columns.getChildren("column");
        for (Element e : colElts) {
            String description = e.getAttributeValue("description");
            String display = e.getAttributeValue("display");
            String id = e.getAttributeValue("id");
            String ignoreSpecialCharacters = e.getAttributeValue("ignoreSpecialCharacters");
            String matchCase = e.getAttributeValue("matchCase");
            String name = e.getAttributeValue("name");
            String weight = e.getAttributeValue("weight");

            CsvColumnDescription.Builder builder = new CsvColumnDescription.Builder(name);
            
            if (StringUtils.equals("1", display)) {
                builder.isDisplayed();
            }
            if (StringUtils.isNotBlank(description)) {
                builder.setDescription(description);
            }
            if (StringUtils.equals("1", id)) {
                builder.isId();
            }
            if (StringUtils.equals("1", ignoreSpecialCharacters)) {
                builder.ignoreSpeacialCharacters();
            }
            if (StringUtils.equals("1", matchCase)) {
                builder.matchCase();
            }
            if (StringUtils.isNotBlank(weight)) {
                builder.setWeight(Double.parseDouble(weight));
            }
            columnsDefinitions.add(builder.build());
        }
    }

    public static class Builder {

        private final String fileName;

        Builder(String fileName) {
            this.fileName = fileName;
        }

        /**
         * Charge et retourne la description du fichier csv à traiter en entrée
         * à partir du fichier xml renseigné au préalable.
         *
         * @return
         * @throws ConfigurationException
         * @throws IOException
         * @throws JDOMException
         */
        public CsvDescription load() throws ConfigurationException, IOException, JDOMException {
            CsvDescription csvDescription = new CsvDescription(fileName);
            return csvDescription;
        }
    }
}
