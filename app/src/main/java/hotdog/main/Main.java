package hotdog.main;

import hotdog.PCMiner.CommitMiner;
import hotdog.CPCMiner.pyszzExecutor;
import hotdog.utils.CSVReader;
import hotdog.utils.JsonConverter;
import hotdog.utils.JsonWriter;

import java.util.ArrayList;

public class Main {
    private String inputPath;
    private String workPath;
    private String szzOpt;
    private ArrayList<String> URLList = null;
    private ArrayList<ArrayList<String>> commitPerProject = null;
    private ArrayList<String> commitIDSingle = null;

    public static void main(String[] args) {
        Main main = new Main();
        main.run(args);
    }

    private void run(String[] args) {
        CLIParser cliParser = new CLIParser(args);

        this.inputPath = cliParser.getInputPath();
        this.workPath = cliParser.getWorkPath();
        this.szzOpt = cliParser.getSzzOption();
        boolean isLog = cliParser.getLog();

        mineBIC();

        pyszzExecutor.setProperties(workPath, isLog);
        pyszzExecutor.runPySZZ(szzOpt);

        JsonConverter jsonConverter = new JsonConverter();
        jsonConverter.convertObjectToCsv();

        // clean up objects (remove used files)
    }

    public void mineBIC () {
        if (inputPath.endsWith(".csv")) {
            commitPerProject = new ArrayList<>();
            ArrayList<String> projectNames = new ArrayList<>();
            CSVReader csvReader = new CSVReader(inputPath);
            csvReader.URLReader();
            URLList = csvReader.getURLList();
            CommitMiner [] commitMiners = new CommitMiner[URLList.size()];
            int i = 0;
            for (CommitMiner perProject : commitMiners) {
                perProject = new CommitMiner(URLList.get(i++), workPath);
                ArrayList<String> temp = null;
                temp = perProject.extractID();
                commitPerProject.add(temp);
                projectNames.add(perProject.getMatcherGroup());
            }
            JsonWriter jsonWriter = new JsonWriter(commitPerProject,projectNames);
            jsonWriter.writeList();
        } else {
            CommitMiner commitMiner = new CommitMiner(inputPath, workPath);
            commitIDSingle = commitMiner.extractID();
            JsonWriter jsonWriter = new JsonWriter(commitIDSingle,commitMiner.getMatcherGroup());
            jsonWriter.writeSingle();
        }
    }
}
