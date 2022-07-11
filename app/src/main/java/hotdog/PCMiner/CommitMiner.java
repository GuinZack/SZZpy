package hotdog.PCMiner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.IterableUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;


public class CommitMiner {

    private List<RevCommit> commitList;
    private File file = null;
    private String workPath ="";
    private boolean completed = false;
    private String cloneInfo;

    public CommitMiner(String URL, String wp) {

        Pattern pattern = Pattern.compile("(git@|ssh|https://)github.com/()(.*?)$");
        Matcher matcher = pattern.matcher(URL);

        if (matcher.find()) cloneInfo = matcher.group(3).replace(".git", "");

        String[] dirs = cloneInfo.split("/");

        cd(wp); //   "/Users/leechanggong/Desktop/Exp/" , "/data/CGYW/clones/"
        build(dirs[0]);
        cd (dirs[0]);
        clone(URL);
        cd (dirs[1]);
        System.out.println(workPath);

        try {
            Git git = Git.open(new File(workPath));
            Iterable<RevCommit> walk = git.log().all().call();
            commitList = IterableUtils.toList(walk);
            completed = true;
        } catch (Exception e) { System.out.println("Exception occurred, Skip to next project\n"); }

    }

    private void cd(String dir) {
        if (workPath.length() == 0) workPath = dir;
        else workPath = workPath + "/" + dir;
    }

    private void build(String dir) {
        try {
            ProcessBuilder builder = new ProcessBuilder(makeDir(dir));
            builder.directory(new File(workPath));

            Process process = builder.start();
            process.waitFor();
            process.destroy();

        } catch(Exception e) { e.printStackTrace(); }
    }

    private void clone(String URL) {
        try {
            ProcessBuilder builder = new ProcessBuilder(gitClone(URL));
            builder.directory(new File(workPath));

            Process process = builder.start();
            process.waitFor();
            process.destroy();

        } catch(Exception e) { e.printStackTrace(); }
    }

    private ArrayList<String> makeDir(String dir) {
        ArrayList<String> command = new ArrayList<>();

        command.add("mkdir");
        command.add(dir);

        return command;
    }

    private ArrayList<String> gitClone(String URL) {
        ArrayList<String> command = new ArrayList<>();

        command.add("git");
        command.add("clone");
        command.add(URL);

        return command;
    }

    public List<RevCommit> getCommitList() {
        return commitList;
    }

    public ArrayList<String> extractID () {
        ArrayList<String> idList = new ArrayList<>();
        for (RevCommit commit : commitList) {
            idList.add(commit.getName());
        }
        return idList;
    }

    public String getMatcherGroup() { return cloneInfo; }

}



