package hudson.plugins.rubyMetrics.saikuro;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsBuildAction;
import hudson.plugins.rubyMetrics.saikuro.model.SaikuroResult;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

public class SaikuroBuildAction extends AbstractRubyMetricsBuildAction{

	private SaikuroResult results;
	
	public SaikuroBuildAction(AbstractBuild<?, ?> owner, SaikuroResult results) {
		super(owner);
		this.results = results;
	}
	
	@Override
	protected DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilder() {
		DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (SaikuroBuildAction a = this; a != null; a = a.getPreviousResult()) {
            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.owner);
            
            dsb.add(a.getResults().getTotalComplexityAsInt(), "total complexity", label);
        }
		return dsb;
	}

	public HealthReport getBuildHealth() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected String getRangeAxisLabel() {
		return "%";
	}

	public String getDisplayName() {
		return "Saikuro report";
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getUrlName() {
		return "saikuro";
	}
	
	public AbstractBuild<?, ?> getOwner() {
		return owner;
	}

	public SaikuroResult getResults() {
		return results;
	}

}
