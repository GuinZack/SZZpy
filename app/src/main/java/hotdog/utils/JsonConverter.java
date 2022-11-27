package hotdog.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class JsonConverter {
    private Json json;
    private List<Pair> jsonList;
    private String path;
    private String savePath;
    private class Json {
        public String fix_commit_hash;
        public String repo_name;
        public List<Object> inducing_commit_hash;
    }

    public JsonConverter() {
        // path to directory where Json will be stored
        json = new Json();
        if (System.getProperty("os.name").toLowerCase().contains("mac"))
            savePath = System.getProperty("user.dir");
        else
            savePath = "/home/nayeawon/sliced_CPMiner";
    }

    private class Pair {
        private String Repo;
        private String CPC;
        private String PC;
        private String File;
        private String Line;
        public Pair (String Repo, String CPC, String PC, String File, String Line) {
            this.Repo = Repo;
            this.CPC = CPC;
            this.PC = PC;
            this.File = File;
            this.Line = Line;
        }
    }

    private Pair readJsonObject (JsonReader reader) {
        String Repo = null;
        String CPC = null;
        String PC = null;
        String File = null;
        String Line = null;

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String line = reader.nextName();
                if (line.equals("repo_name")) Repo = reader.nextString();
                else if (line.equals("inducing_commit_hash")) CPC = reader.nextString();
                else if (line.equals("fix_commit_hash")) PC = reader.nextString();
                else if (line.equals("inducing_commit_file")) File = reader.nextString();
                else if (line.equals("inducing_commit_line")) Line = reader.nextString();
                else reader.skipValue();
            }
            reader.endObject();
            if (Repo == null || CPC == null || PC == null || File == null || Line == null) return null;
            return new Pair(Repo, CPC, PC, File, Line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void convertJsonToObject (String path) {
        jsonList = new ArrayList<>();
        try {
            JsonReader reader = new JsonReader(new FileReader(savePath + "/_CPC/" +path));
            reader.beginArray();
            while (reader.hasNext()) {
                Pair pair = readJsonObject(reader);
                if (pair == null) continue;
                jsonList.add(pair);
            }
            reader.endArray();
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//projectname_cpc.json
    public void convertObjectToCsv () {
        String fileName [] = path.split("_cpc.json");
        savePath += "/_PC_CPC/";
        if (!new File(savePath).exists())
            new File(savePath).mkdir();
        File file = new File(savePath + fileName[0] +"_pc_cpc.csv");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter out = new PrintWriter(fos);
            out.println("RepoName,CPC,PC,FilePath,Line");
            for (Pair pair: jsonList) {
                out.println(pair.Repo + "," + pair.CPC + "," + pair.PC + ","
                        + pair.File + "," + pair.Line);
            }
            out.flush();
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
