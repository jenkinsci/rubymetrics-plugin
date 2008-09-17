package hudson.plugins.rubyMetrics.railsStats;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsBuildAction;
import hudson.plugins.rubyMetrics.railsStats.model.RailsStatsMetrics;
import hudson.plugins.rubyMetrics.railsStats.model.RailsStatsResults;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

import java.util.Map;

public class RailsStatsBuildAction extends AbstractRubyMetricsBuildAction {
	
	private final RailsStatsResults results;
		
	public RailsStatsBuildAction(AbstractBuild<?, ?> owner, RailsStatsResults results) {		
		super(owner);
		this.results = results;
	}
	
	public HealthReport getBuildHealth() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public RailsStatsResults getResults() {
		return results;
	}

	public String getDisplayName() {
		return "Rails stats";
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getUrlName() {
		return "railsStats";
	}
	
	@Override
	protected DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilder() {
		DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
        
		Map<RailsStatsMetrics, Integer> total = results.getTotal();		
		
		for (RailsStatsBuildAction a = this; a != null; a = a.getPreviousResult()) {
			ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.owner);
			
			for (Map.Entry<RailsStatsMetrics, Integer> entry : total.entrySet()) {
				dsb.add(entry.getValue(), entry.getKey().prettyPrint(), label);
			}
		}		
		
        return dsb;
	}

	@Override
	protected String getRangeAxisLabel() {
		return "";
	}    

}
