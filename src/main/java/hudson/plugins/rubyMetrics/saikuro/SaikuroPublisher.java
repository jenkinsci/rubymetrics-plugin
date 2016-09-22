package hudson.plugins.rubyMetrics.saikuro;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.plugins.rubyMetrics.HtmlPublisher;
import hudson.plugins.rubyMetrics.saikuro.model.SaikuroResult;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class SaikuroPublisher extends HtmlPublisher implements SimpleBuildStep {

    @DataBoundConstructor
    public SaikuroPublisher(String reportDir) {
        this.reportDir = reportDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
                        @Nonnull TaskListener listener) throws InterruptedException, IOException {
        final SaikuroFilenameFilter indexFilter = new SaikuroFilenameFilter();
        prepareMetricsReportBeforeParse(run, workspace, listener, indexFilter, DESCRIPTOR.getToolShortName());
        if (run.getResult() == Result.FAILURE) {
            return;
        }

        SaikuroParser parser = new SaikuroParser(run.getRootDir(), listener);
        SaikuroResult results = parser.parse(getCoverageFiles(run, indexFilter)[0]);

        SaikuroBuildAction action = new SaikuroBuildAction(run, results);
        run.getActions().add(action);
    }

    private static class SaikuroFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.equalsIgnoreCase("index_cyclo.html");
        }
    }

    //@Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        protected DescriptorImpl() {
            super(SaikuroPublisher.class);
        }

        public String getToolShortName() {
            return "saikuro";
        }

        @Override
        public String getDisplayName() {
            return "Publish Saikuro report";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public SaikuroPublisher newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return req.bindParameters(SaikuroPublisher.class, "saikuro.");
        }
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

}
