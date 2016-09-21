package hudson.plugins.rubyMetrics.flog;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsPublisher;
import hudson.plugins.rubyMetrics.flog.model.FlogBuildResults;
import hudson.plugins.rubyMetrics.flog.model.FlogFileResults;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class FlogPublisher extends AbstractRubyMetricsPublisher {

    private final String rbDirectories;
    private final String[] splittedDirectories;

    @DataBoundConstructor
    public FlogPublisher(String rbDirectories) {
        this.rbDirectories = rbDirectories;
        this.splittedDirectories = (this.rbDirectories != null && this.rbDirectories.length() > 0 ? this.rbDirectories : ".").split("[\t\r\n]+");
    }

    public String getRbDirectories() {
        return rbDirectories;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        final FlogExecutor flog = new FlogExecutor();

        EnvVars environment = build.getEnvironment(listener);
        FilePath workspace = build.getModuleRoot();

        if (!flog.isFlogInstalled(launcher, environment, workspace)) {
            return fail(build, listener, "Seems flog is not installed. Ensure flog is in your PATH");
        }
        listener.getLogger().println("Publishing flog report...");

        Map<String, ByteArrayOutputStream> execResults = flog.execute(splittedDirectories, launcher, environment, workspace, build.getRootDir());

        FlogBuildResults buildResults = buildResults(build, execResults);

        FlogBuildAction action = new FlogBuildAction(build, buildResults);
        build.getActions().add(action);

        return true;
    }

    private FlogBuildResults buildResults(AbstractBuild<?, ?> build, Map<String, ByteArrayOutputStream> execResults) {
        final FlogParser parser = new FlogParser();
        FlogBuildResults buildResults = new FlogBuildResults();

        for (Map.Entry<String, ByteArrayOutputStream> entry : execResults.entrySet()) {
            FlogFileResults resultsForFile = parser.parse(entry.getKey(), entry.getValue());
            if (resultsForFile != null) {
                buildResults.addFileResults(entry.getKey(), resultsForFile);
            }
        }

        return buildResults;
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> arg0) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Publish Flog report";
        }
    }
}
