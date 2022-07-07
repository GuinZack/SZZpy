package hotdog.utils;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonToCsv {
    private String path;
    private Json json;
    public JsonToCsv (String path) {
        this.path = path;
        Json json = new Json();
        this.json = json;
    }

    public void JsonToObject () {
        try (Reader reader = new FileReader(path)) {
            Gson gson = new Gson();
            // Convert JSON File to Java Object
            json = gson.fromJson(reader, Json.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ObjectToCsv (File file) {
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter out = new PrintWriter(fos);

            for (String key : map.keySet()) {
                out.print(key);
                for (String contents : map.get(key))
                    out.print("," + contents);
                out.print("\n");
            }
            out.flush();
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class Json {
        public String fix_commit_hash;
        public String repo_name;
        public String inducing_commit_hash;
    }
}
