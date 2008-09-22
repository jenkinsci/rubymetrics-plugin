package hudson.plugins.rubyMetrics.rcov;

import static hudson.plugins.rake.Util.getGemsDir;
import static hudson.plugins.rake.Util.hasGemsInstalled;
import static hudson.plugins.rake.Util.isRakeInstalled;
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
import hudson.plugins.rubyMetrics.rcov.model.MetricTarget;
import hudson.plugins.rubyMetrics.rcov.model.RcovResult;
import hudson.plugins.rubyMetrics.rcov.model.Targets;
import hudson.tasks.Publisher;
import hudson.util.FormFieldValidator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.beanutils.ConvertUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Rcov {@link Publisher}
 * 
 * @author David Calavera
 *
 */
@SuppressWarnings({"unchecked", "serial"})
public class RcovPublisher extends RubyMetricsPublisher {
	
	private final String reportDir;
	private List<MetricTarget> targets;	
	
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
    	
        RcovBuildAction action = new RcovBuildAction(build, results, targets);        
        build.getActions().add(action);
        
        if (failMetrics(results, listener)) {
        	build.setResult(Result.UNSTABLE);
        }
        
    	return true;
    }
    
    private boolean failMetrics(RcovResult results, BuildListener listener) {
    	float initRatio = 0;
    	float resultRatio = 0;
    	for (MetricTarget target : targets) {
    		initRatio = target.getUnstable();
    		resultRatio = results.getRatioFloat(target.getMetric());
    		
    		if (resultRatio < initRatio) {
    			listener.getLogger().println("Code coverage enforcement failed for the following metrics:");
    			listener.getLogger().println("    " + target.getMetric().getName());
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean moveReportsToBuildRootDir(FilePath workspace, Build<?, ?> build, BuildListener listener) throws InterruptedException {    	
        try {        	
        	FilePath coverageDir = workspace.child(reportDir);
            
        	if (!coverageDir.exists()) {
        		listener.getLogger().println("file not found: " + coverageDir);
        		return false;
        	}
            
            coverageDir.copyRecursiveTo("**/*", new FilePath(build.getRootDir()));
            
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
    
    public List<MetricTarget> getTargets() {
    	return targets;
    }
    
    public void setTargets(List<MetricTarget> targets) {
    	this.targets = targets;
    }
	
	/**
     * Descriptor should be singleton.
     */
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<Publisher> {

    	private final List<MetricTarget> targets;
    	
		protected DescriptorImpl() {
			super(RcovPublisher.class);
			targets = new ArrayList<MetricTarget>(){{
				add(new MetricTarget(Targets.TOTAL_COVERAGE, 80, null, null));
				add(new MetricTarget(Targets.CODE_COVERAGE, 80, null, null));
			}};
		}

		@Override
		public String getDisplayName() {
			return "Publish Rcov report";
		}
		
		public List<MetricTarget> getTargets(RcovPublisher instance) {			
			return instance != null && instance.getTargets() != null?instance.getTargets() : getDefaultTargets();            
        }
		
		private List<MetricTarget> getDefaultTargets() {
			return targets;
		}

		@Override
		public RcovPublisher newInstance(StaplerRequest req) throws FormException {
			RcovPublisher instance = req.bindParameters(RcovPublisher.class, "rcov.");
			
			ConvertUtils.register(MetricTarget.CONVERTER, Targets.class);
			List<MetricTarget> targets = req.bindParametersToList(MetricTarget.class, "rcov.target.");
			instance.setTargets(targets);
			return instance;
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
