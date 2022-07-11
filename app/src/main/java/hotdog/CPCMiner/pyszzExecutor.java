package hotdog.CPCMiner;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class pyszzExecutor {
    private static String pySZZPath = System.getProperty("user.dir") + "/app/src/main/java/hotdog/CPCMiner/pyszz";
    private static String pathToClonedRepoDir;
    private static boolean isLog;

    public static void setProperties(String workPath, boolean log) {
        pathToClonedRepoDir = workPath;
        isLog = log;
    }

    public static void runPySZZ(String szzOpt) {

        String pySZZMain = pySZZPath + "/main.py";

        String pathToBugFixesJson = System.getProperty("user.dir") + "/data/PC_list.json";

        String szzYml = pySZZPath + "/conf/";
        switch (szzOpt.toUpperCase()) {
            case "AG":
                szzYml += "agszz.yml"; break;
            case "L":
                szzYml += "lszz.yml"; break;
            case "R":
                szzYml += "rszz.yml"; break;
            case "MA":
                szzYml += "maszz.yml"; break;
            case "RA":
                szzYml += "raszz.yml"; break;
            default:
                szzYml += "rszz.yml";
        }

        run(getCommand(pySZZMain, pathToBugFixesJson, szzYml));

    }

    private static void run(ArrayList<String> command) {
        // python3 main.py /path/to/bug-fixes.json /path/to/configuration-file.yml /path/to/repo-directory


        BufferedReader stdout = null;

        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File(pySZZPath));

            Process process = builder.start();

            stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            process.waitFor();
            process.destroy();

        } catch(Exception e) { e.printStackTrace();  if (isLog) writeLog(stdout); System.out.println(stdout);}
        finally {
            if (isLog) { writeLog(stdout); }
        }
    }

    private static ArrayList<String> getCommand(String pySZZMain, String pathToBugFixesJson, String szzYml) {
        ArrayList<String> command = new ArrayList<>();

        command.add("python3");
        command.add(pySZZMain);
        command.add(pathToBugFixesJson);
        command.add(szzYml);
        command.add(pathToClonedRepoDir);

        return command;
    }

    public static void writeLog (BufferedReader output) {

        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            PrintWriter out = new PrintWriter(System.getProperty("user.dir") + "/data/log_" + dtf.format(now) + ".txt");
            BufferedWriter bwr = new BufferedWriter(out);
            StringBuffer outputStringBuffer = new StringBuffer();
            String outputString;
            while((outputString = output.readLine()) != null) {
                outputStringBuffer.append(outputString + System.getProperty("line.separator"));
            }

            bwr.write(outputStringBuffer.toString());

            bwr.flush();
            bwr.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
