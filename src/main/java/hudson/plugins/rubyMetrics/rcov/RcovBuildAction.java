package hudson.plugins.rubyMetrics.rcov;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsBuildAction;
import hudson.plugins.rubyMetrics.rcov.model.RcovFileDetail;
import hudson.plugins.rubyMetrics.rcov.model.RcovFileResult;
import hudson.plugins.rubyMetrics.rcov.model.RcovResult;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class RcovBuildAction extends AbstractRubyMetricsBuildAction {
		
	final RcovResult results;
	
	public RcovBuildAction(AbstractBuild<?, ?> owner, RcovResult results) {
		super(owner);
		this.results = results;
	}

	public HealthReport getBuildHealth() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayName() {
		return "Rcov report";
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getUrlName() {
		return "rcov";
	}

	public AbstractBuild<?, ?> getOwner() {
		return owner;
	}

	public RcovResult getResults() {
		return results;
	}
	
    public Object getDynamic(final String link, final StaplerRequest request, final StaplerResponse response) {    	
    	if (link.startsWith("file.")) {
    		String file = StringUtils.substringAfter(link, "file.");
        	RcovFileResult fileResult = getResults().getFile(file);    	
        	return new RcovFileDetail(owner, fileResult);
    	}
    	
    	return null;    	
    }

	@Override
	protected DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilder() {
		DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (RcovBuildAction a = this; a != null; a = a.getPreviousResult()) {
            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.owner);
            
            dsb.add(a.getResults().getTotalCoverageFloat(), "total coverage", label);
            dsb.add(a.getResults().getCodeCoverageFloat(), "code coverage", label);            
        }
        return dsb;
	}

	@Override
	protected String getRangeAxisLabel() {
		return "%";
	}    

}
