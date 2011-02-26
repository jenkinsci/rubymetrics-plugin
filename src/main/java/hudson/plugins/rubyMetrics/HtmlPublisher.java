package hudson.plugins.rubyMetrics;

import static hudson.plugins.rubyMetrics.Utils.moveReportsToBuildRootDir;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;

import java.io.File;
import java.io.FilenameFilter;

public abstract class HtmlPublisher extends AbstractRubyMetricsPublisher {

    protected String reportDir;

    public String getReportDir() {
        return reportDir;
    }

    protected boolean prepareMetricsReportBeforeParse(AbstractBuild<?, ?> build, BuildListener listener,
            FilenameFilter indexFilter, String toolShortName) throws InterruptedException {
        if (build.getResult() == Result.FAILURE) {
            listener.getLogger().println("Build failed, skipping " + toolShortName + " coverage report");
            return true;
        }
        listener.getLogger().println("Publishing " + toolShortName + " report...");

        final FilePath workspace = build.getModuleRoot();

        boolean copied = moveReportsToBuildRootDir(workspace, build.getRootDir(), listener, reportDir, "**/*");
        if (!copied) {
            build.setResult(Result.FAILURE);
            return fail(build, listener, toolShortName + " report directory wasn't found using the pattern '" + reportDir + "'.");
        }

        File[] coverageFiles = build.getRootDir().listFiles(indexFilter);
        if (coverageFiles == null || coverageFiles.length == 0) {
            return fail(build, listener, toolShortName + " report index file wasn't found");
        }

        return true;
    }

    protected File[] getCoverageFiles(AbstractBuild<?, ?> build, FilenameFilter indexFilter) {
        return build.getRootDir().listFiles(indexFilter);
    }
}
