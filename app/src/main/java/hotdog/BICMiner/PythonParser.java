package hotdog.BICMiner;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PythonParser {
    private static String pySZZPath = System.getProperty("user.dir") + "/app/src/main/java/hotdog/BICMiner/pyszz";
    private static Process process = null;
    private static Runtime runtime = Runtime.getRuntime();
    private static StringBuffer successOutput;
    private static StringBuffer errorOutput;
    private static BufferedReader successBufferReader = null; // 성공 버퍼
    private static BufferedReader errorBufferReader = null; // 오류 버퍼
    private static String msg = null; // message
    private static List<String> cmdList;

    private static void defaultSettings() {
        cmdList = new ArrayList<String>();
        if (System.getProperty("os.name").indexOf("Windows") > -1) {
            cmdList.add("cmd");
            cmdList.add("/c");
        } else {
            cmdList.add("/bin/sh");
            cmdList.add("-c");
        }
        successOutput = new StringBuffer();
        errorOutput = new StringBuffer();
    }

    public static void runPySZZ(String szzOpt) {

        defaultSettings();

        String pySZZMain = pySZZPath + "/main.py";

        String pathToBugFixesJson = System.getProperty("user.dir") + "/data/BIC_list.json";

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

//        String pathToClonedRepoDir = "/data/CGYW/clones";
        String pathToClonedRepoDir = "/Users/nayeawon/Desktop";


        // python3 main.py /path/to/bug-fixes.json /path/to/configuration-file.yml /path/to/repo-directory
        cmdList.add("python3 " + pySZZMain + " " + pathToBugFixesJson + " " + szzYml + " " + pathToClonedRepoDir);

        String[] array = cmdList.toArray(new String[cmdList.size()]);
        try {
            process = runtime.exec(array);
            successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));
            while ((msg = successBufferReader.readLine()) != null) {
                if (successOutput != null)
                    successOutput.append(msg + System.getProperty("line.separator"));
            }
            errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "EUC-KR"));
            while ((msg = errorBufferReader.readLine()) != null) {
                if (errorOutput != null)
                    errorOutput.append(msg + System.getProperty("line.separator"));
            }
            process.waitFor();
            if (process.exitValue() == 0) {
                System.out.println("\nPySZZ Finished\n");
            } else {
                System.err.println("\nPySZZ Failed\n");
                if (errorOutput != null)
                    System.out.println(errorOutput);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                process.destroy();
                if (successBufferReader != null) successBufferReader.close();
                if (errorBufferReader != null) errorBufferReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static StringBuffer getSuccessOutput() {
        return successOutput;
    }
}
