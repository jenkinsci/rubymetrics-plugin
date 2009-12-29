package hudson.plugins.rubyMetrics.flog;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsPublisher;
import hudson.plugins.rubyMetrics.flog.model.FlogResults;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

import java.io.IOException;
import java.util.Map;

import org.codehaus.plexus.util.StringOutputStream;
import org.kohsuke.stapler.DataBoundConstructor;

public class FlogPublisher extends AbstractRubyMetricsPublisher {

	private final String[] rbDirectories;
	
	@DataBoundConstructor
	public FlogPublisher(String rbDirectories) {
		this.rbDirectories = rbDirectories.split("[\t\r\n]+");
	}
	
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		final FlogExecutor flog = new FlogExecutor();
		final FlogParser parser = new FlogParser();
		
		EnvVars environment = build.getEnvironment(listener);
		FilePath workspace = build.getWorkspace();
		
		if (!flog.isFlogInstalled(launcher, environment, workspace)) {
			return fail(build, listener, "Seems flog is not installed. Ensure flog is in your PATH");
		}
		
		Map<String, StringOutputStream> execResults = flog.execute(rbDirectories, launcher, environment, workspace);
		for (Map.Entry<String, StringOutputStream> entry : execResults.entrySet()) {
			FlogResults resultsForFile = parser.parse(entry.getValue());
		}
		
		return true;
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
			return "Publish flog reports";
		}
    }
}
