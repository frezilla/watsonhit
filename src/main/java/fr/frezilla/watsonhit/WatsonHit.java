package fr.frezilla.watsonhit;

import fr.frezilla.watsonhit.business.exceptions.BusinessException;
import fr.frezilla.watsonhit.business.exceptions.BusinessExceptions;
import fr.frezilla.watsonhit.reader.file.CsvReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@NoArgsConstructor
public final class WatsonHit {
    
    public static final Logger LOGGER = LogManager.getRootLogger();
    
    private String fileOut;
    private Level logLevel;
    
    private void checkCsvFileIn(String csvFileIn) throws BusinessException {
        LOGGER.debug("vérification du fichier entrée");
        File f = new File(csvFileIn);
        if (!f.exists()) {
            throw BusinessExceptions.fileInNotFound.getException();
        }
        if (!f.isFile()) {
            throw BusinessExceptions.fileInNotValid.getException();
        }
    }

    private void doMain(String[] args) {
        try {
            Options CsvReaderOptions = createCsvReaderOptions();
            
            CsvReaderArgs csvReaderArgs = parseCsvReaderArgs(args);
            CsvReader csvReader = createCSVReader(csvReaderArgs);
            while (csvReader.hasNext()) {
                String[] columns = csvReader.next();
                for (String c : columns) {
                    System.out.print(c);
                }
                System.out.println();
            }
            csvReader.close();
        } catch (ParseException | IOException e) {
            LOGGER.fatal(e.getMessage());
        } 
    }

    public static void main(String[] args) {
        new WatsonHit().doMain(args);
    }

    private Map<String, Object> parseArguments(@NonNull String[] args) throws ParseException {
        Options options = new Options();
        
        options.addOption("d", "debug", false, "activation du mode debug");
        options.addOption("del", "csv-delimiter", true, "délimiteur de zones du fichier csv");
        options.addRequiredOption("in", "csv-file-in", true, "fichier csv à traiter");
        options.addRequiredOption("out", "file-out", true, "fichier résultat");
        
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(new Option("ctlsize", "csv-delimiter", false, "active le contrôle du nombre de colonnes du fichier csv"));
        optionGroup.addOption(new Option("wh", "csv-with-header", false, "la première ligne du fichier csv est une ligne de titre"));
        options.addOptionGroup(optionGroup);
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        logLevel = (cmd.hasOption("d") ? Level.DEBUG : Level.INFO);
        
        fileOut = cmd.getOptionValue("out");
        
        CsvReaderArgs csvReaderArgs = new CsvReaderArgs();
        csvReaderArgs.delimiter = (cmd.hasOption("del") ? cmd.getOptionValue("del") : CsvReader.DEFAULT_DELIMITER);
        csvReaderArgs.fileIn = cmd.getOptionValue("in");
        csvReaderArgs.withHeader = cmd.hasOption("wh");
        
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("csvReaderArguments", csvReaderArgs);
        
        return arguments;
    }

    private CsvReader createCSVReader(CsvReaderArgs args) throws FileNotFoundException {
        LOGGER.debug(String.format("initialisation du traitement de lecture du fichier csv\n"
                + "%s", args.toString()));
        CsvReader.Builder builder = new CsvReader.Builder(args.fileIn);
        if (args.controlColumnsSize) {
            builder.controlColumnsSize();
        }
        builder.setDelimiter(args.delimiter);
        if (args.withHeader) {
            builder.withHeader();
        }
        return builder.build();
    }

    private Options createCsvReaderOptions() {
        Options options = new Options();
        
        return options;
    }

    private CsvReaderArgs parseCsvReaderArgs(String[] args) {
        Options options = new Options();
        
        options.addOption(Option.builder("csv-delimiter").hasArg(true).desc("délimiteur de zones du fichier csv").build());
        options.addOption(Option.builder("csv-filename").hasArg(true).desc("nom du fichier csv à traiter").build());
        
        options.addOption(Option.builder("csv-withheader").hasArg(false).desc("la première ligne du fichier csv est une ligne de titre").build());

        options.addOption("d", "debug", false, "activation du mode debug");
        options.addOption("del", "csv-delimiter", true, "délimiteur de zones du fichier csv");
        options.addRequiredOption("in", "csv-file-in", true, "fichier csv à traiter");
        options.addRequiredOption("out", "file-out", true, "fichier résultat");
        
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(new Option("ctlsize", "csv-delimiter", false, "active le contrôle du nombre de colonnes du fichier csv"));
        optionGroup.addOption(new Option("wh", "csv-with-header", false, "la première ligne du fichier csv est une ligne de titre"));
        options.addOptionGroup(optionGroup);
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        logLevel = (cmd.hasOption("d") ? Level.DEBUG : Level.INFO);
        
        fileOut = cmd.getOptionValue("out");
        
        CsvReaderArgs csvReaderArgs = new CsvReaderArgs();
        csvReaderArgs.delimiter = (cmd.hasOption("del") ? cmd.getOptionValue("del") : CsvReader.DEFAULT_DELIMITER);
        csvReaderArgs.fileIn = cmd.getOptionValue("in");
        csvReaderArgs.withHeader = cmd.hasOption("wh");
        
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("csvReaderArguments", csvReaderArgs);
        
        return arguments;        
    }
    
    private static class CsvReaderArgs {
        private boolean controlColumnsSize;
        private String delimiter;
        private String fileIn;
        private boolean withHeader; 
        
        @Override
        public String toString() {
            return String.format("-> nom du fichier : <%s>\n"
                    + "-> délimiteur : <%s>\n"
                    + "-> entêtes de colonnes incluses : <%s>\n"
                    + "-> contrôle du nombre de colonnes : <%s>", 
                    fileIn,
                    delimiter,
                    withHeader ? "oui" : "non",
                    controlColumnsSize ? "oui" : "non");
        }
    }

}
