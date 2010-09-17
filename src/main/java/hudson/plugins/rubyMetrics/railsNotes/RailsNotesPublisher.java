package hudson.plugins.rubyMetrics.railsNotes;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;
import hudson.plugins.rake.Rake;
import hudson.plugins.rake.RubyInstallation;
import hudson.plugins.rubyMetrics.AbstractRailsTaskPublisher;
import hudson.plugins.rubyMetrics.railsNotes.model.RailsNotesResults;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

import java.io.IOException;

import org.codehaus.plexus.util.StringOutputStream;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Rails notes {@link Publisher}
 *
 * @author Adam Stegman
 */
public class RailsNotesPublisher extends AbstractRailsTaskPublisher {

    @DataBoundConstructor
    public RailsNotesPublisher(String rakeInstallation, String rakeWorkingDir) {
        super(rakeInstallation, rakeWorkingDir, "notes");
    }

    protected void buildAction(StringOutputStream out, AbstractBuild<?, ?> build) {
        final RailsNotesParser parser = new RailsNotesParser();
        RailsNotesResults results = parser.parse(out);

        RailsNotesBuildAction action = new RailsNotesBuildAction(build, results);
        build.getActions().add(action);
    }

    @Override
    public Action getProjectAction(AbstractProject<?,?> project) {
        return new RailsNotesProjectAction(project);
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        protected DescriptorImpl() {
            super(RailsNotesPublisher.class);
        }

        @Override
        public String getHelpFile() {
            return "/plugin/rubyMetrics/railsNotesHelp.html";
        }

        @Override
        public String getDisplayName() {
            return "Publish Rails Notes report";
        }

        public RubyInstallation[] getRakeInstallations() {
            return Rake.DESCRIPTOR.getInstallations();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }
}
