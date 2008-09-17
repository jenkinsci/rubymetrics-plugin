package hudson.plugins.rubyMetrics.rcov;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.model.Result;
import hudson.plugins.rubyMetrics.RubyMetricsPublisher;
import hudson.plugins.rubyMetrics.rcov.model.RcovResult;
import hudson.tasks.Publisher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Rcov {@link Publisher}
 * 
 * @author David Calavera
 *
 */
public class RcovPublisher extends RubyMetricsPublisher {
	
	private final String reportDir;
	
	@DataBoundConstructor
	public RcovPublisher(String reportDir) {
		this.reportDir = reportDir;				
	}
	
	public String getReportDir() {
		return reportDir;
	}
	
	/**
     * {@inheritDoc}
     */
    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
    	if (!Result.SUCCESS.equals(build.getResult())) {
    		listener.getLogger().println("Build wasn't successful, skipping rcov coverage report");
    		return true;
    	}
    	listener.getLogger().println("Publishing Rcov report...");
    	    	    	
        final Project<?, ?> project = build.getParent();        
        final FilePath workspace = project.getModuleRoot();        
    	
        boolean copied = moveReportsToBuildRootDir(workspace, build, listener);
        if (!copied) {
        	return fail(build, listener, "Rcov report directory wasn't found using the pattern '" + reportDir + "'.");
        }
        
        final RcovFilenameFilter indexFilter = new RcovFilenameFilter();
        
        File[] coverageFiles = build.getRootDir().listFiles(indexFilter);
        if (!(coverageFiles != null && coverageFiles.length > 0)) {
        	return fail(build, listener, "Rcov report index file wasn't found");
        }
        
        RcovParser parser = new RcovParser(build.getRootDir());
        RcovResult results = parser.parse(coverageFiles[0]);
    	
        RcovBuildAction action = new RcovBuildAction(build, results);        
        build.getActions().add(action);
        
    	return true;
    }
    
    private boolean moveReportsToBuildRootDir(FilePath workspace, Build<?, ?> build, BuildListener listener) throws InterruptedException {    	
        try {        	
        	FilePath coverageDir = workspace.child(reportDir);
            
        	if (!coverageDir.exists()) {
        		listener.getLogger().println("file not found: " + coverageDir);
        		return false;
        	}
            
            int copied = coverageDir.copyRecursiveTo("**/*", new FilePath(build.getRootDir()));
            
            return true;
        } catch (IOException e) {
            Util.displayIOException(e, listener);
            e.printStackTrace(listener.fatalError("Unable to find coverage results"));
            build.setResult(Result.FAILURE);
        }
        return false;
    }
    
    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final AbstractProject<?, ?> project) {
        return new RcovProjectAction(project);
    }
	
	/**
     * Descriptor should be singleton.
     */
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<Publisher> {

		protected DescriptorImpl() {
			super(RcovPublisher.class);			
		}

		@Override
		public String getDisplayName() {
			return "Publish Rcov report";
		}
    	
    }

	public Descriptor<Publisher> getDescriptor() {
		return DESCRIPTOR;
	}
	
	private static class RcovFilenameFilter implements FilenameFilter {		
        public boolean accept(File dir, String name) {            
            return name.equalsIgnoreCase("index.html");
        }
    }
	
}
