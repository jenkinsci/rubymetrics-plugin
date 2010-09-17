package hudson.plugins.rubyMetrics.saikuro;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.plugins.rubyMetrics.HtmlPublisher;
import hudson.plugins.rubyMetrics.saikuro.model.SaikuroResult;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class SaikuroPublisher extends HtmlPublisher {

    @DataBoundConstructor
    public SaikuroPublisher(String reportDir) {
        this.reportDir = reportDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        final SaikuroFilenameFilter indexFilter = new SaikuroFilenameFilter();
        prepareMetricsReportBeforeParse(build, listener, indexFilter, DESCRIPTOR.getToolShortName());
        if (build.getResult() == Result.FAILURE) {
            return false;
        }

        SaikuroParser parser = new SaikuroParser(build.getRootDir(), listener);
        SaikuroResult results = parser.parse(getCoverageFiles(build, indexFilter)[0]);

        SaikuroBuildAction action = new SaikuroBuildAction(build, results);
        build.getActions().add(action);

        return true;
    }

    private static class SaikuroFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.equalsIgnoreCase("index_cyclo.html");
        }
    }

    @Override
    public Action getProjectAction(final AbstractProject<?, ?> project) {
        return new SaikuroProjectAction(project);
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
