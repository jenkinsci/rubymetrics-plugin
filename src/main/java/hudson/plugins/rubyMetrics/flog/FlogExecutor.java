package hudson.plugins.rubyMetrics.flog;

import static hudson.plugins.rubyMetrics.Utils.moveReportsToBuildRootDir;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.util.StringOutputStream;

public class FlogExecutor {

    public boolean isFlogInstalled(Launcher launcher, EnvVars environment, FilePath workspace) {
        try {
            OutputStream out = launch(arguments("--help"), launcher, environment, workspace);

            return out != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, StringOutputStream> execute(String[] rbDirectories, Launcher launcher, EnvVars environment, FilePath workspace, File buildRootDir) throws InterruptedException, IOException {
        Map<String, StringOutputStream> results = new HashMap<String, StringOutputStream>();

        for (String relativePath : rbDirectories) {
            if (workspace.child(relativePath) == null) {
                launcher.getListener().getLogger().println("the path: " + relativePath + " doesn't exist into the workpace, ignoring it");
                continue;
            }

            FilePath[] rubyFiles = getRubyFiles(workspace, buildRootDir, relativePath, launcher);
            for (FilePath rubyFile : rubyFiles) {
                String rubyFilePath = rubyFile.toURI().getPath();
                ArgumentListBuilder arguments = arguments("-ad", rubyFilePath);

                StringOutputStream out = launch(arguments, launcher, environment, workspace);
                if (out == null) {
                    results.clear();
                    return results;
                }
                results.put(prettifyFilePath(relativePath, rubyFilePath), out);
            }
        }

        return results;
    }

    public StringOutputStream launch(ArgumentListBuilder arguments, Launcher launcher, EnvVars environment, FilePath workspace) throws InterruptedException, IOException {
        StringOutputStream out = new StringOutputStream();

        int result = launcher.launch()
            .cmds(arguments)
            .envs(environment)
            .stdout(out)
            .pwd(workspace)
            .join();

        return result >= 0 ? out : null;
    }

    public ArgumentListBuilder arguments(String... args) {
        ArgumentListBuilder flogArguments = new ArgumentListBuilder();
        flogArguments.add("flog");
        for (String arg : args) {
            flogArguments.add(arg);
        }

        return flogArguments;
    }

    private FilePath[] getRubyFiles(FilePath workspace, File buildRootDir, String relativePath, Launcher launcher) throws InterruptedException, IOException {
        moveReportsToBuildRootDir(workspace, buildRootDir, launcher.getListener(), relativePath, "**/*.rb", true);

        FilePath classesLocation = new FilePath(new File(buildRootDir, relativePath));
        launcher.getListener().getLogger().println("searching ruby classes into: " + classesLocation.toURI().getPath());

        return classesLocation.list("**/*.rb");
    }

    private String prettifyFilePath(String path, String rubyFilePath) {
        return rubyFilePath.substring(rubyFilePath.indexOf(path));
    }
}
