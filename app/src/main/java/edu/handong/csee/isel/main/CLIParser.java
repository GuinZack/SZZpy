package edu.handong.csee.isel.main;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import java.util.ArrayList;

public class CLIParser {

    private ArrayList<String> address;
    private String path;
    private boolean help;

    public ArrayList<String> CommonCLI (String[] args) {
        Options options = createOptions();
        address = new ArrayList<String>();
        if(parseOptions(options, args)){
            if (help) {
                printHelp(options);
            }
        }
        return address;
    }

    public String getPath() { return path; }


    private boolean parseOptions(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            path = cmd.getOptionValue("p");
            help = cmd.hasOption("h");

        } catch (Exception e) {
            e.printStackTrace();
            printHelp(options);
            return false;
        }

        return true;
    }

    private Options createOptions() {

        Options options = new Options();

        options.addOption(Option.builder("p").longOpt("path")
                .desc("Set a path of a directory of a cloned project, a URL, or a path with a csv file")
                .hasArg()
                .argName("Local path")
                .build());

        options.addOption(Option.builder("h").longOpt("help")
                .desc("Help")
                .build());

        return options;
    }

    private void printHelp(Options options) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        String header = "AST Change Analyzer";
        String footer ="\nPlease report issues at https://github.com/ISEL-HGU/ASTChangeAnalyzer";
        formatter.printHelp("ASTChangeAnalyzer", header, options, footer, true);
    }
}


