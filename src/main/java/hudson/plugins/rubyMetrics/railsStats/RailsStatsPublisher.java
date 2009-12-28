package hudson.plugins.rubyMetrics.railsStats;

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
import hudson.plugins.rubyMetrics.railsStats.model.RailsStatsResults;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

import java.io.IOException;

import org.codehaus.plexus.util.StringOutputStream;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Rails stats {@link Publisher}
 * 
 * @author David Calavera
 *
 */
@SuppressWarnings("unchecked")
public class RailsStatsPublisher extends AbstractRubyMetricsPublisher {
	
	private final Rake rake;
	private final String rakeInstallation;
	private final String rakeWorkingDir;
	
	@DataBoundConstructor
	public RailsStatsPublisher(String rakeInstallation, String rakeWorkingDir) {
		this.rakeInstallation = rakeInstallation;
		this.rakeWorkingDir = rakeWorkingDir;
		this.rake = new Rake(this.rakeInstallation, null, "stats", null, this.rakeWorkingDir, true);
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		
        FilePath workspace = build.getModuleRoot();
        
        if (!isRailsProject(workspace)) {
            String message = "Your workspace is not a valid rails application directory";
            if (workspace != null) {
                message += ": " + workspace.getName();
            }
        	return fail(build, listener, message);
        }
		
		listener.getLogger().println("Publishing rails stats report...");
		
		StringOutputStream out = new StringOutputStream();		
		BuildListener stringListener = new StreamBuildListener(out);
				
		if (rake.perform(build, launcher, stringListener)) {
			final RailsStatsParser parser = new RailsStatsParser();
			RailsStatsResults results = parser.parse(out);
			
			RailsStatsBuildAction action = new RailsStatsBuildAction(build, results);
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
			return workspace != null && workspace.isDirectory()
				&& workspace.list("app") != null && workspace.list("config") != null
				&& workspace.list("db") != null && workspace.list("test") != null;
		} catch (Exception e) {
			return false;
		}
	}	
	
		
	@Override
	public Action getProjectAction(AbstractProject<?,?> project) {
		return new RailsStatsProjectAction(project);
	}

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		protected DescriptorImpl() {
			super(RailsStatsPublisher.class);			
		}
		
		@Override
        public String getHelpFile() {
        	return "/plugin/rubyMetrics/railsStatsHelp.html";
        }

		@Override
		public String getDisplayName() {
			return "Publish Rails stats report";
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
