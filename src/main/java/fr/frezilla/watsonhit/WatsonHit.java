package fr.frezilla.watsonhit;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

public final class WatsonHit {
    
    private final PrintStream err;
    private final List<String> arguments;
    
    @Option(name="-in", aliases = {"--input-csv-file"}, required = true, usage = "nom du fichier CSV à analyser")
    private File fileIn;
    
    @Option(name="-out", aliases = {"--result-file"}, required = true, usage="nom du fichier résultat")
    private File fileOut;
    
    private WatsonHit() {
        err = System.err;
        arguments = new ArrayList();
    }
    
    public static void main(String[] args) {
        new WatsonHit().doMain(args);
    }
    
    private void doMain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        ParserProperties parserProperties = ParserProperties.defaults();
        parserProperties.withUsageWidth(80);
        
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            err.println(e.getMessage());
            err.println("java WatsonHist [");
            parser.printUsage(err);
            err.println();
        }
    }    
}
