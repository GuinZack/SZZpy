package hotdog.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class JsonConverter {
    private Json json;
    private List<Json> jsonList;

    private class Json {
        public String fix_commit_hash;
        public String repo_name;
        public String inducing_commit_hash;
    }

    public JsonConverter() {
        // path to directory where Json will be stored
        json = new Json();
    }

    public void convertJsonToObject (String path) {
        Collection<Json> enums = null;
        try (Reader reader = new FileReader(path)) {
            Gson gson = new Gson();
            // Convert JSON File to Java Object
//            json = gson.fromJson(reader, Json.class);

            Type collectionType = new TypeToken<Collection<Json>>(){}.getType();
            enums = gson.fromJson(reader, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert enums != null;
        jsonList = enums.stream().toList();

    }

    public void convertObjectToCsv () {
        File file = new File(System.getProperty("user.dir") + "/data/CP_CPC_pair.csv");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter out = new PrintWriter(fos);
            out.println("RepoName,CPC,PC");
            for (Json j : jsonList) {
                out.println(j.repo_name+","+j.inducing_commit_hash+","+j.fix_commit_hash);
            }
            out.flush();
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
