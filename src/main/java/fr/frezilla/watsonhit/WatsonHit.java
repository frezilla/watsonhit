package fr.frezilla.watsonhit;

import fr.frezilla.watsonhit.business.csv.CsvColumnDescription;
import fr.frezilla.watsonhit.business.csv.CsvDescription;
import fr.frezilla.watsonhit.reader.file.CsvReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;

@NoArgsConstructor
public final class WatsonHit {

    public static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Méthode main
     *
     * @param args
     */
    public static void main(String[] args) {
        new WatsonHit().doMain(args);
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
            LOGGER.debug(e);
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
            LOGGER.debug(e);
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
     * Créé les options nécessaires au parsing des arguments.
     *
     * @return
     */
    private Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder("csvDelimiter").desc("délimiteur de zones du fichier csv").hasArg().build());
        options.addOption(Option.builder("csvDescriptorFile").desc("fichier de description du fichier csv").hasArg().required().build());
        options.addOption(Option.builder("csvFile").desc("fichier csv à traiter").hasArg().required().build());
        options.addOption(Option.builder("resultFile").desc("fichier de résultat").hasArg().required().build());

        return options;
    }

    /**
     * Exécute le traitement principal
     * 
     * @param args 
     */
    private void doMain(String[] args) {
        try {
            LOGGER.info("analyse des paramètres");
            WatsonHitParameters parameters = parseArguments(args, createOptions());
            LOGGER.info("paramètres \n" + parameters.toString());

            LOGGER.info("contrôle des paramètres");
            checkParameters(parameters);

            LOGGER.info("chargement de la description du fichier csv");
            CsvDescription csvDescription = loadCsvDescription(parameters.getCsvDescriptorFile());
            LOGGER.info("contrôle de la description du fichier csv");
            checkCsvDescription(csvDescription);

            LOGGER.info("initialisation du fichier de résultat");

            LOGGER.info("vérification du fichier csv");
            int nbCsvLines = checkCsvFile(parameters.getCsvFile(), csvDescription, parameters.getCsvDelimiter());

            LOGGER.info("comparaison des données");
            File tempFile = run(parameters.getCsvFile(), csvDescription, parameters.getCsvDelimiter(), nbCsvLines);

            LOGGER.info("écriture des résultats dans le fichier");
            
            LOGGER.info("Fin du traitement, consultez le fichier <" + parameters.getResultFile() + "> pour visualiser le résultat du traitement");
        } catch (BusinessException e) {
            LOGGER.info("L'erreur ci-dessous a été détectée, activez le mode debug pour avoir plus d'informations sur l'origine du problème.");
            LOGGER.info(" -> " + e.getMessage());
        } catch (Exception e) {
            LOGGER.fatal("Erreur bloquante non gérée, activez le mode debug pour avoir plus d'informations sur l'origine du problème.");
            LOGGER.debug(e);
        }
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
            LOGGER.debug(e);
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
    private WatsonHitParameters parseArguments(@NonNull String[] args, @NonNull Options options) throws BusinessException {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            return new WatsonHitParameters(
                    cmd.hasOption("csvDelimiter") ? cmd.getOptionValue("csvDelimiter") : CsvReader.DEFAULT_DELIMITER,
                    cmd.getOptionValue("csvDescriptorFile"),
                    cmd.getOptionValue("csvFile"),
                    cmd.getOptionValue("resultFile"));
        } catch (ParseException e) {
            LOGGER.debug(e);
            throw BusinessExceptions.argumentsError.build();
        }
    }

    private File run(@NonNull String fileName, @NonNull CsvDescription csvDescription, @NonNull String csvDelimiter, int nbCsvLines) throws BusinessException {
        try {
            CsvReader.Builder builder = CsvReader.builder(fileName).setDelimiter(csvDelimiter);
            CsvReader mainReader = builder.build();
            
            int currentLine = 0;
            LOGGER.info("0 %");
            
            final int stepSizePercent = 10;
            final int stepSize = nbCsvLines / stepSizePercent;
            int nextStep = stepSize;
            int nextStepPercent = stepSizePercent;
            
            while (mainReader.hasNext()) {
                currentLine++;
                mainReader.next();
                
                CsvReader reader = builder.build();
                while (reader.hasNext()) {
                    reader.next();
                }
                reader.close();
                
                if (currentLine == nextStep) {
                    LOGGER.info(nextStepPercent + " %");
                    nextStepPercent += stepSizePercent;
                    nextStep += stepSize;
                }
            }
            LOGGER.info("100 %");
            mainReader.close();
        } catch (IOException e) {
            
        }
        return null;
    }
}
