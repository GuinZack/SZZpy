package edu.handong.csee.isel.utils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PythonParser {
    private String filePath;
    private static Process process = null;
    private static Runtime runtime = Runtime.getRuntime();
    private static StringBuffer successOutput;
    private static StringBuffer errorOutput;
    private static BufferedReader successBufferReader = null; // 성공 버퍼
    private static BufferedReader errorBufferReader = null; // 오류 버퍼
    private static String msg = null; // 메시지
    private static List<String> cmdList;

    public PythonParser(String filePath) {
        this.filePath = filePath;
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

    public void runPy() {

        cmdList.add("python3 " + filePath + " " + filePath);
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
                System.out.println("\nSettings Completed\n");
                //if (successOutput != null)
                //System.out.println(successOutput);


            } else {
                System.err.println("\nSettings Failed\n");
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
