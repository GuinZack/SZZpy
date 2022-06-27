package hotdog.main;

import hotdog.BFCMiner.CommitMiner;
import hotdog.BICMiner.PythonParser;
import hotdog.utils.CSVReader;
import hotdog.utils.JsonWriter;

import java.util.ArrayList;

public class Main {
    private String path;
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
        this.path = cliParser.getPath();
        this.szzOpt = cliParser.getSzzOption();
        mineBIC();
        PythonParser.runPySZZ(szzOpt);
//        System.out.println(PythonParser.getSuccessOutput());
//
//        System.out.println(System.getProperty("user.dir"));
    }

    public void mineBIC () {
        if (this.path.endsWith(".csv")) {
            commitPerProject = new ArrayList<>();
            ArrayList<String> projectNames = new ArrayList<>();
            CSVReader csvReader = new CSVReader(this.path);
            csvReader.URLReader();
            URLList = csvReader.getURLList();
            CommitMiner [] commitMiners = new CommitMiner[URLList.size()];
            int i = 0;
            for (CommitMiner perProject : commitMiners) {
                perProject = new CommitMiner(URLList.get(i++));
                ArrayList<String> temp = null;
                temp = perProject.extractID();
                commitPerProject.add(temp);
                projectNames.add(perProject.getMatcherGroup());
            }
            JsonWriter jsonWriter = new JsonWriter(commitPerProject,projectNames);
            jsonWriter.writeList();
        } else {
            CommitMiner commitMiner = new CommitMiner(path);
            commitIDSingle = commitMiner.extractID();
            JsonWriter jsonWriter = new JsonWriter(commitIDSingle,commitMiner.getMatcherGroup());
            jsonWriter.writeSingle();
        }
    }
}
