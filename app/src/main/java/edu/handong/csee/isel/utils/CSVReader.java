package edu.handong.csee.isel.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class CSVReader {
    private String CSVPath;
    private ArrayList<String> URLList = new ArrayList<>();

    public CSVReader (String path) {
        CSVPath = path;
    }

    public ArrayList<String> getURLList() { return URLList; }

    public void URLReader () {
        Reader in = null;
        try {
            in = new FileReader(CSVPath);
            int urlColumn = -1;
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            boolean isFirst = false;
            for (CSVRecord record : parser) {
                int i = 0;
                for (String content : record) {
                    if (!isFirst) {
                        if (content.contains("Github")) {
                            urlColumn = i;
                            break;
                        }
                        i++;
                        continue;
                    }
                    if (urlColumn == -1) {
                        System.err.println("Wrong CSV format detected");
                        System.exit(-1);
                    }
                    if (i++ == urlColumn)
                        URLList.add(content);
                }
                isFirst = true;

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
