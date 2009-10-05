package hudson.plugins.rubyMetrics;

import hudson.FilePath;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public abstract class HtmlPublisher extends AbstractRubyMetricsPublisher {

	protected String reportDir;
	
	public String getReportDir() {
		return reportDir;
	}
	
	protected boolean moveReportsToBuildRootDir(FilePath workspace, AbstractBuild<?, ?> build, BuildListener listener) throws InterruptedException {
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
	
	protected boolean prepareMetricsReportBeforeParse(AbstractBuild<?, ?> build, BuildListener listener,
			FilenameFilter indexFilter, String toolShortName) throws InterruptedException {
		if (!Result.SUCCESS.equals(build.getResult())) {
    		listener.getLogger().println("Build wasn't successful, skipping " + toolShortName + " coverage report");
    		return true;
    	}
    	listener.getLogger().println("Publishing " + toolShortName + " report...");
    	
        final FilePath workspace = build.getModuleRoot();
    	
        boolean copied = moveReportsToBuildRootDir(workspace, build, listener);
        if (!copied) {
        	return fail(build, listener, toolShortName + " report directory wasn't found using the pattern '" + reportDir + "'.");
        }
        
        File[] coverageFiles = build.getRootDir().listFiles(indexFilter);
        if (coverageFiles == null || coverageFiles.length == 0) {
        	return fail(build, listener, toolShortName + " report index file wasn't found");
        }
        
        return true;
	}
	
	protected File[] getCoverageFiles(AbstractBuild<?, ?> build, FilenameFilter indexFilter) {
		return build.getRootDir().listFiles(indexFilter);
	}
}
