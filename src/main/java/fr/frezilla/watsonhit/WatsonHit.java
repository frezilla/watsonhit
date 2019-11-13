package fr.frezilla.watsonhit;

import fr.frezilla.watsonhit.business.csv.CsvColumnDescription;
import fr.frezilla.watsonhit.business.csv.CsvDescription;
import fr.frezilla.watsonhit.business.similarity.SimilarityAlgorithm;
import fr.frezilla.watsonhit.business.similarity.SimilarityAlgorithms;
import fr.frezilla.watsonhit.business.values.ValuesUtils;
import fr.frezilla.watsonhit.reader.file.CsvReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import lombok.NonNull;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;

public final class WatsonHit {

    private static final int LINELENGTH = 80;
    private static final Logger LOGGER = Logger.getLogger(WatsonHit.class);

    /**
     * Méthode main
     *
     * @param args
     */
    public static void main(String[] args) {
        new WatsonHit().doMain(args);
    }

    private final PrintStream outputStream;

    /**
     * Constructeur
     */
    private WatsonHit() {
        outputStream = System.out;
    }

    /**
     * Contrôle la cohérence de la description.
     * <p>
     * La description doit définir au moins deux colonnes.
     * <p>
     * Au moins une colonne doit être déclarée en tant qu'identifiant et le
     * nombre d'identifiants max doit être égal au nombre de colonnes - 1.
     *
     * @param csvDescription
     * @throw BusinessException
     */
    private void checkCsvDescription(@NonNull CsvDescription csvDescription) throws BusinessException {
        try {
            List<CsvColumnDescription> columnsDescriptions = csvDescription.getColumnsDescription();
            if (columnsDescriptions.isEmpty()) {
                int nbColumnsDescriptions = columnsDescriptions.size();
                if (nbColumnsDescriptions < 2) {
                    throw BusinessExceptions.csvDescriptionNotValid.build("le nombre de colonnes définies est inférieur à 2");
                }

                int nbIds = 0;
                columnsDescriptions.stream().filter((d) -> (d.isId())).map((_item) -> 1).reduce(nbIds, Integer::sum);

                if (nbIds == 0) {
                    throw BusinessExceptions.csvDescriptionNotValid.build("au moins une colonne \"identifiant\" doit être définie");
                }
                if (nbIds > nbColumnsDescriptions - 1) {
                    throw BusinessExceptions.csvDescriptionNotValid.build("le nombre de colonnes \"identifiant\" doit être inférieur au nombre de colonnes total");
                }
            }
        } catch (BusinessException e) {
            LOGGER.error(e);
            throw BusinessExceptions.csvDescriptionError.build();
        }
    }

    /**
     * Contrôle le fichier csv.
     * <p>
     * Le nombre de colonnes présentes dans le fichier doit être égal au nombre
     * de colonnes définies dans le fichier de description lu au préalable.
     *
     * @param fileName nom du fichier csv
     * @param csvDescription description du fichier csv
     * @return Nombre de lignes du fichier
     * @throws BusinessException
     */
    private int checkCsvFile(@NonNull String fileName, @NonNull CsvDescription csvDescription, @NonNull String csvDelimiter) throws BusinessException {
        final int nbColumns = csvDescription.getColumnsDescription().size();
        int nbLines = 0;
        try {
            CsvReader reader = CsvReader.builder(fileName).setDelimiter(csvDelimiter).build();
            while (reader.hasNext()) {
                nbLines++;
                String[] row = reader.next();
                if (nbColumns != row.length) {
                    throw BusinessExceptions.csvFileFormatError.build(nbLines, nbColumns, row.length);
                }
            }

            reader.close();
        } catch (BusinessException | IOException e) {
            LOGGER.error(e);
            throw BusinessExceptions.csvFileError.build();
        }
        return nbLines;
    }

    /**
     * Vérifie la cohérence des paramètres du traitement
     *
     * @param parameters
     */
    private void checkParameters(@NonNull WatsonHitParameters parameters) throws BusinessException {
        List<String> errorMessages = WatsonHitParametersChecker.check(parameters);

        if (!errorMessages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            errorMessages.forEach((msg) -> {
                sb.append(msg).append("\n");
            });
            throw BusinessExceptions.parametersError.build(sb.toString());
        }
    }

    /**
     * Copie le fichier de travail vers le fichier de résultat
     *
     * @param tempFile
     * @param resultFile
     * @throws IOException
     */
    private void copyFile(@NonNull File tempFile, @NonNull File resultFile) throws IOException {
        FileUtils.copyFile(tempFile, resultFile);
    }

    /**
     * Créé les options nécessaires au parsing des arguments.
     *
     * @return
     */
    private Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder("d").longOpt("csvDelimiter").desc("délimiteur des zones du fichier csv").hasArg().build());
        options.addOption(Option.builder("desc").longOpt("csvDescriptorFile").desc("fichier de description du fichier csv").hasArg().required().build());
        options.addOption(Option.builder("csvin").longOpt("csvFile").desc("fichier csv à traiter").hasArg().required().build());
        options.addOption(Option.builder("taux").longOpt("minSimilarity").desc("taux de similarité au delà duquel on sauvegarde le résultat").hasArg().build());
        options.addOption(Option.builder("res").longOpt("resultFile").desc("fichier de résultat").hasArg().required().build());

        return options;
    }

    /**
     * Créé le fichier de résultat
     *
     * @param resultFile
     * @return
     * @throws BusinessException
     */
    private File createResultFile(String resultFile) throws BusinessException {
        try {
            File f = new File(resultFile);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            return f;
        } catch (IOException e) {
            LOGGER.error(e);
            throw BusinessExceptions.resultFileError.build(resultFile);
        }
    }

    /**
     * Affiche l'aide de saisie des arguments du programme.
     *
     * @param options
     */
    private void displayHelp(@NonNull Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getClass().getName(), options, true);
        System.exit(0);
    }

    /**
     * Exécute le traitement principal
     *
     * @param args
     */
    private void doMain(String[] args) {
        try {
            final Option helpOption = Option.builder("h").longOpt("help").desc("affiche l'aide").build();
            final Options helpOptions = new Options();
            helpOptions.addOption(helpOption);
            final Options options = createOptions();

            printPadded("Analyse des paramètres...");
            WatsonHitParameters parameters = parseArguments(args, options, helpOptions);
            outputStream.println("[OK]");

            if (parameters.isHelpMode()) {
                displayHelp(options);
            }

            printPadded("Contrôle des paramètres...");
            checkParameters(parameters);
            outputStream.println("[OK]");

            printPadded("Chargement de la description du fichier csv...");
            CsvDescription csvDescription = loadCsvDescription(parameters.getCsvDescriptorFile());
            outputStream.println("[OK]");

            printPadded("Contrôle de la description du fichier csv...");
            checkCsvDescription(csvDescription);
            outputStream.println("[OK]");

            printPadded("Initialisation du fichier de résultat...");
            File resultFile = createResultFile(parameters.getResultFile());
            outputStream.println("[OK]");

            printPadded("Vérification du fichier csv...");
            int nbCsvLines = checkCsvFile(parameters.getCsvFile(), csvDescription, parameters.getCsvDelimiter());
            outputStream.println("[OK]");

            outputStream.println("Comparaison des données...");
            File tempFile = run(parameters.getCsvFile(), csvDescription, parameters.getCsvDelimiter(), nbCsvLines, parameters.getMinSimilarity());

            printPadded("Ecriture des résultats dans le fichier...");
            copyFile(tempFile, resultFile);
            outputStream.println("[OK]");

            printPadded("Nettoyage des fichiers de travail...");
            tempFile.delete();
            outputStream.println("[OK]");

            outputStream.println("Fin du traitement, consultez le fichier <" + parameters.getResultFile() + "> pour visualiser le résultat du traitement");
        } catch (BusinessException e) {
            outputStream.println("[KO]\nL'erreur ci-dessous a été détectée, consulter le fichier des traces pour avoir plus d'informations sur l'origine du problème.");
            outputStream.println("-> " + e.getMessage());
        } catch (Exception e) {
            outputStream.println("[KO]\nErreur bloquante non gérée, consulter le fichier des traces pour avoir plus d'informations sur l'origine du problème.");
            outputStream.println(e);
        }
    }

    /**
     * Filtre et formatte le contenu des colonnes en fonction de leur
     * description.
     *
     * @param wkColumns
     * @param csvDescription
     * @return
     */
    private String[] filterAndFormatColumns(@NonNull String[] wkColumns, @NonNull CsvDescription csvDescription) {
        List<CsvColumnDescription> columnsDescriptions = csvDescription.getColumnsDescription();
        String[] columns = new String[wkColumns.length];

        for (int i = 0; i < wkColumns.length; i++) {
            CsvColumnDescription d = columnsDescriptions.get(i);
            String value = null;
            if (!d.isId()) {
                value = wkColumns[i];
                if (d.isIgnoreSpecialCharacters()) {
                    value = ValuesUtils.replaceSpecialsCharacters(value);
                }
                if (!d.isMatchCase()) {
                    value = ValuesUtils.toUppercase(value);
                }
            }
            columns[i] = value;
        }

        return columns;
    }

    /**
     * Finalise le fichier de travail.
     *
     * @param writer
     * @throws IOException
     */
    private void finalizeWorkingFile(@NonNull Writer writer) throws IOException {
        Date now = new Date();
        writer.write(String.format("\t</table>\n</div>\n<p>Document g&eacute;n&eacute;r&eacute; le %s &agrave; </p>\n</body>", DateFormatUtils.format(now, "dd/MM/yyyy"),  DateFormatUtils.format(now, "HH:mm:ss")));
    }

    /**
     * Insère le résultats des comparaisons dans le fichier.
     * 
     * @param writer
     * @param csvDescription
     * @param columns1
     * @param columns2
     * @param similarityJaccard
     * @param similarityJaro
     * @param similarityJaro_winkler
     * @param similarityLevenstein
     * @throws IOException 
     */
    private void insertResultInWorkingFile(Writer writer, CsvDescription csvDescription, String[] columns1, String[] columns2, float similarityJaccard, float similarityJaro, float similarityJaro_winkler, float similarityLevenstein) throws IOException {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        List<CsvColumnDescription> columnsDescriptions = csvDescription.getColumnsDescription();

        int i = 0;
        for (CsvColumnDescription d : columnsDescriptions) {
            if (d.isId() || d.isDisplayed()) {
                sb1.append("<td>").append(columns1[i]).append("</td>");
                sb2.append("<td>").append(columns2[i]).append("</td>");
            }
            i++;
        }
        writer.write("\t\t<tr>" + sb1.toString() + sb2.toString() 
                + String.format("<td>%.2f</td>", similarityJaccard) 
                + String.format("<td>%.2f</td>", similarityJaro) 
                + String.format("<td>%.2f</td>", similarityJaro_winkler)
                + String.format("<td>%.2f</td>", similarityLevenstein) + "</tr>\n");
    }

    /**
     * Initialise le contenu du fichier de travail
     *
     * @param workingFile
     * @param csvDescription
     */
    private void intializeWorkingFile(@NonNull Writer writer, @NonNull CsvDescription csvDescription) throws IOException {
        writer.write("<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + "<meta charset=\"utf-8\">\n"
                + "<style>\n"
                + "table {\n"
                + "\tborder-collapse: collapse;\n"
                + "\tborder-spacing: 0;\n"
                + "\twidth: 100%;\n"
                + "\tborder: 1px solid #ddd;\n"
                + "}\n"
                + "\n"
                + "th, td {\n"
                + "\ttext-align: left;\n"
                + "\tpadding: 8px;\n"
                + "}\n"
                + "\n"
                + "tr:nth-child(even){background-color: #f2f2f2}\n"
                + "</style>\n"
                + "</head>\n"
                + "<body>\n"
                + "\n"
                + "<h2>R&eacute;sultat de la recherche lignes similaires</h2>\n"
                + "\n"
                + "<div style=\"overflow-x:auto;\">\n"
                + "\t<table>\n"
                + "\t\t<tr>");
        List<CsvColumnDescription> columnsDescriptions = csvDescription.getColumnsDescription();
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        columnsDescriptions.forEach(d -> {
            if (d.isDisplayed() || d.isId()) {
                sb1.append("<th>").append(d.getName()).append("&nbsp;<small>(1)</small></th>");
                sb2.append("<th>").append(d.getName()).append("&nbsp;<small>(2)</small></th>");
            }
        });
        writer.write(sb1.toString() + sb2.toString() + "<th>Taux Jaccob</th><th>Taux Jaro</th><th>Taux Jaro-Winkler</th><th>Taux Levenstein</th></tr>\n");
    }

    /**
     * Charge la description du fichier csv depuis un fichier au format xml.
     *
     * @param fileName nom du fichier xml qui décrit le fichier csv
     * @return Description
     * @throws BusinessException
     */
    private CsvDescription loadCsvDescription(@NonNull String fileName) throws BusinessException {
        try {
            return CsvDescription.builder(fileName).load();
        } catch (ConfigurationException | IOException | JDOMException e) {
            LOGGER.error(e);
            throw BusinessExceptions.csvDescriptionError.build();
        }
    }

    /**
     * Parse les arguments passés en ligne de commande
     *
     * @param args
     * @param options
     * @return
     * @throws ParseException
     */
    private WatsonHitParameters parseArguments(@NonNull String[] args, @NonNull Options options, @NonNull Options helpOptions) throws BusinessException {
        try {
            CommandLineParser parser = new DefaultParser();

            CommandLine cmd = parser.parse(helpOptions, args, true);

            WatsonHitParameters parameters;

            if (cmd.hasOption("help")) {
                parameters = new WatsonHitParameters(true);
            } else {
                cmd = parser.parse(options, args);
                parameters = new WatsonHitParameters(
                        cmd.hasOption("csvDelimiter") ? cmd.getOptionValue("csvDelimiter") : CsvReader.DEFAULT_DELIMITER,
                        cmd.getOptionValue("csvDescriptorFile"),
                        cmd.getOptionValue("csvFile"),
                        cmd.hasOption("minSimilarity") ? Double.parseDouble(cmd.getOptionValue("minSimilarity")) : 0.0,
                        cmd.getOptionValue("resultFile"));
            }
            return parameters;
        } catch (ParseException e) {
            LOGGER.error(e);
            outputStream.println();
            displayHelp(options);
            throw BusinessExceptions.argumentsError.build();
        }
    }

    /**
     * Affiche un texte mis en forme sur le flux de sortie spécifié.
     *
     * @param s
     */
    private void printPadded(@NonNull String s) {
        outputStream.print(StringUtils.rightPad(s, LINELENGTH));
        outputStream.flush();
    }

    /**
     * Exécute le traitement de comparaison des lignes du fichier.
     *
     * @param fileName
     * @param csvDescription
     * @param csvDelimiter
     * @param nbCsvLines
     * @param minSimilarity
     * @return
     * @throws BusinessException
     */
    private File run(@NonNull String fileName, @NonNull CsvDescription csvDescription, @NonNull String csvDelimiter, int nbCsvLines, double minSimilarity) throws BusinessException {
        try {
            File workingFile = File.createTempFile("watsonHit", ".temp");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(workingFile))) {
                intializeWorkingFile(writer, csvDescription);

                CsvReader.Builder builder = CsvReader.builder(fileName).setDelimiter(csvDelimiter);
                CsvReader mainReader = builder.build();

                SimilarityAlgorithm jaccard = SimilarityAlgorithms.JACCARD.getAlgorithm();
                SimilarityAlgorithm jaro = SimilarityAlgorithms.JARO.getAlgorithm();
                SimilarityAlgorithm jaro_winkler = SimilarityAlgorithms.JARO_WINKLER.getAlgorithm();
                SimilarityAlgorithm levenstein = SimilarityAlgorithms.LEVENSTEIN.getAlgorithm();

                List<CsvColumnDescription> columnsDescriptions = csvDescription.getColumnsDescription();

                int currentLineMainReader = 0;

                try (ProgressBar progressBar = new ProgressBar("", nbCsvLines, ProgressBarStyle.ASCII)) {

                    while (mainReader.hasNext()) {
                        currentLineMainReader++;
                        String[] mainColumns = mainReader.next();
                        String[] mainColumnsToCompare = filterAndFormatColumns(mainColumns, csvDescription);

                        CsvReader reader = builder.build();
                        int currentLine = 0;
                        while (reader.hasNext()) {
                            currentLine++;
                            String[] wkColumns = reader.next();
                            if (currentLine > currentLineMainReader) {
                                String[] currentColumns = wkColumns;
                                String[] currentColumnsToCompare = filterAndFormatColumns(currentColumns, csvDescription);

                                float similarityJaccard = 0.0f;
                                float similarityJaro = 0.0f;
                                float similarityJaro_winkler = 0.0f;
                                float similarityLevenstein = 0.0f;
                                
                                float totalWeight = 0.0f;
                                for (int i = 0; i < mainColumnsToCompare.length; i++) {
                                    float weight = columnsDescriptions.get(i).getWeight();
                                    if (weight != 0.0 && mainColumnsToCompare[i] != null && currentColumnsToCompare[i] != null) {
                                        if (mainColumnsToCompare[i].length() == 0 || currentColumnsToCompare[i].length() == 0) {
                                            similarityJaccard += 0.0f;
                                            similarityJaro += 0.0f;
                                            similarityJaro_winkler += 0.0f;
                                            similarityLevenstein += 0.0f;
                                        } else if (mainColumnsToCompare[i].equals(currentColumns[i])) {
                                            similarityJaccard += 1.0f * weight;
                                            similarityJaro += 1.0f * weight;
                                            similarityJaro_winkler += 1.0f * weight;
                                            similarityLevenstein += 1.0f * weight;
                                        } else {
                                            similarityJaccard += weight * jaccard.getHitRate(mainColumnsToCompare[i], currentColumns[i]);
                                            similarityJaro += weight * jaro.getHitRate(mainColumnsToCompare[i], currentColumns[i]);
                                            similarityJaro_winkler += weight * jaro_winkler.getHitRate(mainColumnsToCompare[i], currentColumns[i]);
                                            similarityLevenstein += weight * levenstein.getHitRate(mainColumnsToCompare[i], currentColumns[i]);
                                            
                                        }
                                        totalWeight += weight;
                                    }
                                }
                                
                                similarityJaccard = (totalWeight == 0.0f) ? 0.0f : similarityJaccard / totalWeight * 100.0f;
                                similarityJaro = (totalWeight == 0.0f) ? 0.0f : similarityJaro / totalWeight * 100.0f;
                                similarityJaro_winkler = (totalWeight == 0.0f) ? 0.0f : similarityJaro_winkler / totalWeight * 100.0f;
                                similarityLevenstein = (totalWeight == 0.0f) ? 0.0f : similarityLevenstein / totalWeight * 100.0f;
                                
                                if (similarityJaccard >= minSimilarity 
                                        || similarityJaro >= minSimilarity 
                                        || similarityJaro_winkler >= minSimilarity 
                                        || similarityLevenstein >= minSimilarity) {
                                    insertResultInWorkingFile(
                                            writer, 
                                            csvDescription, 
                                            mainColumns, 
                                            currentColumns, 
                                            similarityJaccard,
                                            similarityJaro,
                                            similarityJaro_winkler,
                                            similarityLevenstein
                                    );
                                }
                            }
                        }
                        reader.close();
                        progressBar.step();
                    }
                }
                outputStream.println();
                mainReader.close();

                finalizeWorkingFile(writer);

                writer.flush();
            }
            return workingFile;
        } catch (IOException e) {
            LOGGER.error(e);
            throw BusinessExceptions.ioError.build();
        }
    }
}
