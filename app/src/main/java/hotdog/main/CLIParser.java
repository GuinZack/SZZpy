package hotdog.main;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import java.util.ArrayList;

public class CLIParser {

    private String inputPath;
    private String workPath;
    private String szzOption = "r";
    private boolean log;
    private boolean help;
    private String pcOrcpc;

    public CLIParser (String[] args) {
        Options options = createOptions();
        if(parseOptions(options, args)){
            if (help) {
                printHelp(options);
            }
        }
    }

    public String getInputPath() { return inputPath; }
    public String getWorkPath() { return workPath; }
    public String getSzzOption() { return szzOption; }
    public boolean getLog() { return log; }
    public String getPcOrCpc () { return pcOrcpc; }

    private boolean parseOptions(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            inputPath = cmd.getOptionValue("ip");
            workPath = cmd.getOptionValue("wp");
            szzOption = cmd.getOptionValue("szz");
            log = cmd.hasOption("l");
            if (cmd.hasOption("pc")) pcOrcpc = "pc";
            else if (cmd.hasOption("cpc")) pcOrcpc = "cpc";
            else pcOrcpc = null;
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

        options.addOption(Option.builder("ip").longOpt("inputPath")
                .desc("Set a path of a directory of a cloned project, a URL, or a path with a csv file")
                .hasArg()
                .argName("Local path")
                .required()
                .build());

        options.addOption(Option.builder("wp").longOpt("workPath")
                .desc("Set a path of a directory of a cloned project, or a path of a working directory")
                .hasArg()
                .argName("Local path")
                .build());

        options.addOption(Option.builder("szz").longOpt("szzOption")
                .desc("Set a SZZ algorithm")
                .hasArg()
                .argName("Option for SZZ Algorithm")
                .build());

        options.addOption(Option.builder("l").longOpt("log")
                .desc("adding this flag leaves log file")
                .build());

        options.addOption(Option.builder("pc").longOpt("postChange")
                .desc("exit after making PC_list.json")
                .build());

        options.addOption(Option.builder("cpc").longOpt("cpc")
                .desc("make csv file from the output of pyszz")
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


