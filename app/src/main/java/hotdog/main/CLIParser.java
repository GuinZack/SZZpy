package hotdog.main;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import java.util.ArrayList;

public class CLIParser {

    private String path;
    private String szzOption = "r";
    private boolean log;
    private boolean help;


    public CLIParser (String[] args) {
        Options options = createOptions();
        if(parseOptions(options, args)){
            if (help) {
                printHelp(options);
            }
        }
    }

    public String getPath() { return path; }
    public String getSzzOption() { return szzOption; }
    public boolean getLog() { return log; }


    private boolean parseOptions(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            path = cmd.getOptionValue("p");
            szzOption = cmd.getOptionValue("szzOpt");
            log = cmd.hasOption("l");
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
                .required()
                .build());

        options.addOption(Option.builder("szzOpt").longOpt("szzOption")
                .desc("")
                .hasArg()
                .argName("Option for SZZ Algorithm")
                .build());

        options.addOption(Option.builder("l").longOpt("log")
                .desc("adding this flag leaves log file")
                .build());

        options.addOption(Option.builder("h").longOpt("help")
                .desc("Help")
                .build());

        return options;
    }

    private void printHelp(Options options) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        String header = "SZZpy";
        String footer ="\nPlease report issues at https://github.com/ISEL-HGU/ASTChangeAnalyzer";
        formatter.printHelp("SZZpy", header, options, footer, true);
    }
}


