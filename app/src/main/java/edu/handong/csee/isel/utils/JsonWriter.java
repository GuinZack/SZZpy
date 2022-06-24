package edu.handong.csee.isel.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class JsonWriter {
    private ArrayList<String> idList = null;
    private ArrayList<ArrayList<String>> idLists = null;
    private String savePath;
    private String projectName;
    private ArrayList<String> projectNames = null;

    public JsonWriter (ArrayList<String> idLIST, String projectName) {
        this.idList = idLIST;
        this.projectName = projectName;
    }

    public JsonWriter (ArrayList<ArrayList<String>> idLISTs, ArrayList<String> projectNames) {
        this.idLists = idLISTs;
        this.projectNames = projectNames;
    }

    public void writeList() {
        savePath = System.getProperty("user.dir");
        try {
            FileOutputStream fos = new FileOutputStream(savePath + "/data/BIC_list.json");
            PrintWriter out = new PrintWriter(fos);
            out.println("[");
            int i = 0;
            for (ArrayList<String> list : idLists) {
                int j = 0;
                for (String content : list) {
                    out.println("\t{");
                    out.print("\t\t\"fix_commit_hash\":");
                    out.println("\"" + content + "\",");
                    out.print("\t\t\"repo_name\":");
                    out.println("\"" + projectNames.get(j) + "\"");
                    out.print("\t}");
                    if (i < idLists.size()-1 && j++ < list.size()-1)
                        out.print(",");
                    out.println();
                }
                i++;
            }

            out.print("]");


            out.flush();
            out.close();
            fos.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeSingle() {
        savePath = System.getProperty("user.dir");
        try {
            FileOutputStream fos = new FileOutputStream(savePath + "/data/BIC_list.json");
            PrintWriter out = new PrintWriter(fos);
            out.println("[");
            int i = 0;
            for (String content : idList) {
                out.println("\t{");
                out.print("\t\t\"fix_commit_hash\":");
                out.println("\"" + content + "\",");
                out.print("\t\t\"repo_name\":");
                out.println("\"" + projectName + "\"");
                out.print("\t}");
                if (i++ < idList.size()-1)
                    out.print(",");
                out.println();
            }
            out.print("]");


            out.flush();
            out.close();
            fos.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
