package hotdog.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class JsonConverter {
    private Json json;
    private List<Json> jsonList;
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
            savePath = "/data/CGYW/CPMiner";
    }

    public void convertJsonToObject (String path) {
        this.path = path;
        Collection<Json> enums = null;
        try (Reader reader = new FileReader(savePath + "/_CPC/" +path)) {
            Gson gson = new Gson();
            // Convert JSON File to Java Object
//            json = gson.fromJson(reader, Json.class);

            Type collectionType = new TypeToken<Collection<Json>>(){}.getType();
            enums = gson.fromJson(reader, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert enums != null;
        jsonList = new ArrayList<>(enums);
//        jsonList = enums.stream().toList();

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
            for (Json j : jsonList) {
                for (Object tuple : j.inducing_commit_hash) {
                    // Ex. "inducing_commit_hash": [["face_recognition/api.py",
                    //                              "99641c5052a944e887fb786983e5575301c604a2",
                    //                              ["return np.linalg.norm(faces - face_to_compare, axis=1)"]]
                    String [] path_hash = tuple.toString().split(", ");
                    path_hash[0] = path_hash[0].replace("[",""); // FilePath
                    if (path_hash[2].contains("\"]"))
                        path_hash[2] = path_hash[2].substring(path_hash[2].indexOf("[")+1,
                                                                path_hash[2].lastIndexOf("]")-2); // Line
                    else path_hash[2] = path_hash[2].substring(1);
                    out.println(j.repo_name + "," + path_hash[1] + "," + j.fix_commit_hash + ","
                                    + path_hash[0] + "," + path_hash[2]);
                }

            }
            out.flush();
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
