package hudson.plugins.rubyMetrics.saikuro;

import hudson.Launcher;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.plugins.rubyMetrics.HtmlPublisher;
import hudson.plugins.rubyMetrics.saikuro.model.SaikuroResult;
import hudson.tasks.Publisher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

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
    public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
    	final SaikuroFilenameFilter indexFilter = new SaikuroFilenameFilter();
    	boolean success = prepareMetricsReportBeforeParse(build, listener, indexFilter, DESCRIPTOR.getToolShortName());
    	if (!success) {
    		return false;
    	}
    	
    	SaikuroParser parser = new SaikuroParser(build.getRootDir());
    	SaikuroResult results = parser.parse(getCoverageFiles(build, indexFilter)[0]);
    	
    	return true;
    }
    
    private static class SaikuroFilenameFilter implements FilenameFilter {		
        public boolean accept(File dir, String name) {            
            return name.equalsIgnoreCase("index_cyclo.html");
        }
    }
    
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<Publisher> {
    	
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
		public SaikuroPublisher newInstance(StaplerRequest req) throws FormException {
			return req.bindParameters(SaikuroPublisher.class, "saikuro.");			
		}		
    }

	public Descriptor<Publisher> getDescriptor() {
		return DESCRIPTOR;
	}

}
