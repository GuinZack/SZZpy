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

        if (cliParser.getPcOrCSV().equals("pc")) {
            minePC();
            System.exit(0);
        } else if (cliParser.getPcOrCSV().equals("cpc")) {
            JsonConverter jsonConverter = new JsonConverter();
            jsonConverter.convertJsonToObject(inputPath);
            jsonConverter.convertObjectToCsv();
            System.out.println("csv file written" + "\nCPMiner completed");
            System.exit(0);
        }

        minePC();
        System.out.println("Feeding Json file to pyszz");
        pyszzExecutor.setProperties(workPath, isLog);
        pyszzExecutor.runPySZZ(szzOpt);
        System.out.println("Pyszz successfully completed" + "\nConverting output to csv file");
        JsonConverter jsonConverter = new JsonConverter();
        //jsonConverter.convertJsonToObject(System.getProperty("user.dir") + "app/src/main/java/hotdog/CPCMiner/pyszz/out/");
        jsonConverter.convertObjectToCsv();

        // clean up objects (remove used files)
    }

    public void minePC() {
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
            System.out.println("Clone clompleted");
            JsonWriter jsonWriter = new JsonWriter(commitPerProject,projectNames);
            jsonWriter.writeList();
        } else {
            CommitMiner commitMiner = new CommitMiner(inputPath, workPath);
            commitIDSingle = commitMiner.extractID();
            System.out.println("Clone clompleted");
            JsonWriter jsonWriter = new JsonWriter(commitIDSingle,commitMiner.getMatcherGroup());
            jsonWriter.writeSingle();
        }
        System.out.println("Post Changes(PC) json file successfully written");
    }
}
