package hudson.plugins.rubyMetrics.railsStats;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.model.StreamBuildListener;
import hudson.plugins.rake.Rake;
import hudson.plugins.rake.RubyInstallation;
import hudson.plugins.rubyMetrics.RubyMetricsPublisher;
import hudson.plugins.rubyMetrics.railsStats.model.RailsStatsResults;
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
public class RailsStatsPublisher extends RubyMetricsPublisher {
	
	private final Rake rake;
	private final String rakeInstallation;
	
	@DataBoundConstructor
	public RailsStatsPublisher(String rakeInstallation) {
		this.rakeInstallation = rakeInstallation;
		this.rake = new Rake(this.rakeInstallation, null, "stats", null, true);
	}
	
	public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		
		final Project<?, ?> project = build.getParent();        
        FilePath workspace = project.getModuleRoot();
        
        if (!isRailsProject(workspace)) {
        	return fail(build, listener, "This is not a rails app directory: " + workspace.getName());
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
	public Action getProjectAction(Project project) {
		return new RailsStatsProjectAction(project);
	}

	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<Publisher> {

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
    	
    }

	public Descriptor<Publisher> getDescriptor() {
		return DESCRIPTOR;
	}

}
