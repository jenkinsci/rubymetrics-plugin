package hudson.plugins.rubyMetrics;

import hudson.FilePath;
import hudson.model.*;

import java.io.File;
import java.io.FilenameFilter;

import static hudson.plugins.rubyMetrics.Utils.moveReportsToBuildRootDir;

public abstract class HtmlPublisher extends AbstractRubyMetricsPublisher {

    protected String reportDir;

    public String getReportDir() {
        return reportDir;
    }

    protected boolean prepareMetricsReportBeforeParse(Run<?, ?> run, FilePath workspace, TaskListener listener,
            FilenameFilter indexFilter, String toolShortName) throws InterruptedException {
        if (run.getResult() == Result.FAILURE) {
            listener.getLogger().println("Build failed, skipping " + toolShortName + " coverage report");
            return true;
        }
        listener.getLogger().println("Publishing " + toolShortName + " report...");

        boolean copied = moveReportsToBuildRootDir(workspace, run.getRootDir(), listener, reportDir, "**/*");
        if (!copied) {
            run.setResult(Result.FAILURE);
            return fail(run, listener, toolShortName + " report directory wasn't found using the pattern '" + reportDir + "'.");
        }

        File[] coverageFiles = run.getRootDir().listFiles(indexFilter);
        if (coverageFiles == null || coverageFiles.length == 0) {
            return fail(run, listener, toolShortName + " report index file wasn't found");
        }

        return true;
    }

    protected File[] getCoverageFiles(Run<?, ?> run, FilenameFilter indexFilter) {
        return run.getRootDir().listFiles(indexFilter);
    }
}
