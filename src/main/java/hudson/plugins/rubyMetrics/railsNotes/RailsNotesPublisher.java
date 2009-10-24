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
import hudson.plugins.rubyMetrics.AbstractRubyMetricsPublisher;
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
public class RailsNotesPublisher extends AbstractRubyMetricsPublisher {
    private final Rake rake;
    private final String rakeInstallation;
    private final String rakeWorkingDir;
    
    @DataBoundConstructor
    public RailsNotesPublisher(String rakeInstallation, String rakeWorkingDir) {
        this.rakeInstallation = rakeInstallation;
        this.rakeWorkingDir = rakeWorkingDir;
        this.rake = new Rake(this.rakeInstallation, null, "notes", null, this.rakeWorkingDir, true);
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        
        FilePath workspace = build.getModuleRoot();
        
        if (!isRailsProject(workspace)) {
            return fail(build, listener, "This is not a rails app directory: " + workspace.getName());
        }
        
        listener.getLogger().println("Publishing rails notes report...");
        
        StringOutputStream out = new StringOutputStream();      
        BuildListener stringListener = new StreamBuildListener(out);
                
        if (rake.perform(build, launcher, stringListener)) {
            final RailsNotesParser parser = new RailsNotesParser();
            RailsNotesResults results = parser.parse(out);
            
            RailsNotesBuildAction action = new RailsNotesBuildAction(build, results);
            build.getActions().add(action);
        }               
        
        return true;
    }
    
    public String getRakeInstallation() {
        return rakeInstallation;
    }
    
    public String getRakeWorkingDir() {
        return rakeWorkingDir;
    }
    
    private boolean isRailsProject(FilePath workspace) {
        try { //relaxed rails app schema
            return workspace.isDirectory()
                && workspace.list("app") != null && workspace.list("config") != null
                && workspace.list("db") != null && workspace.list("test") != null;
        } catch (Exception e) {
            return false;
        }
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