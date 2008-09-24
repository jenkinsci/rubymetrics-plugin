package hudson.plugins.rubyMetrics.rcov;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsBuildAction;
import hudson.plugins.rubyMetrics.rcov.model.MetricTarget;
import hudson.plugins.rubyMetrics.rcov.model.RcovFileDetail;
import hudson.plugins.rubyMetrics.rcov.model.RcovFileResult;
import hudson.plugins.rubyMetrics.rcov.model.RcovResult;
import hudson.plugins.rubyMetrics.rcov.model.Targets;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

import java.util.List;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class RcovBuildAction extends AbstractRubyMetricsBuildAction {
		
	private final RcovResult results;
	private final List<MetricTarget> targets;
	
	public RcovBuildAction(AbstractBuild<?, ?> owner, RcovResult results, List<MetricTarget> targets) {
		super(owner);
		this.results = results;
		this.targets = targets;
	}

	public HealthReport getBuildHealth() {
		int minValue = 100;
		Targets minMetric = null;
		for (MetricTarget target : targets) {			
			int value = calcRangeScore(target.getHealthy(), target.getUnhealthy(), 
					results.getRatioFloat(target.getMetric()).intValue());
			if (value <= minValue) {
				minValue = value;
				minMetric = target.getMetric();
			}
		}
		HealthReport report = minMetric != null?new HealthReport(minValue, results.getHealthDescription(minMetric)):null;	
		return report;
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
	
	private int calcRangeScore(Integer max, Integer min, int value) {
        if (min == null || min < 0) min = 0;
        if (max == null || max > 100) max = 100;
        if (min > max) min = max - 1;
        int result = (int) (100f * (value - min.floatValue()) / (max.floatValue() - min.floatValue()));
        if (result < 0) return 0;
        if (result > 100) return 100;
        return result;
    }
	
    public Object getDynamic(final String link, final StaplerRequest request, final StaplerResponse response) {    	
    	if (link.startsWith("file.")) {
    		String file = link.substring(link.indexOf("file.") + 5);
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
