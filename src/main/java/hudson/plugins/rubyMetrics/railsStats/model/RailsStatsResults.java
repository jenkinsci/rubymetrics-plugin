package hudson.plugins.rubyMetrics.railsStats.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RailsStatsResults {
	
	private Map<RailsClassType, Map<RailsStatsMetrics, Integer>> metrics = new HashMap<RailsClassType, Map<RailsStatsMetrics,Integer>>();
	
	private String codeLocSummary;
	private String testLocSummary;
	private String codeToTestRatio;
	
	
	public Collection<String> getHeaders() {
		Collection<String> headers = new ArrayList<String>();
		headers.add("Name");
		
		for (RailsStatsMetrics metric : RailsStatsMetrics.values()) {
			headers.add(metric.prettyPrint());
		}
		
		return headers;
	}
	
	public void addMetric(RailsClassType classType, Map<RailsStatsMetrics, Integer> metric) {
		metrics.put(classType, metric);
	}
	
	public Map<RailsStatsMetrics, Integer> getTotal() {
		return metrics.get(RailsClassType.TOTAL);
	}
	
	public Map<RailsClassType, Map<RailsStatsMetrics, Integer>> getMetrics() {
		return metrics;
	}
	public void setMetrics(Map<RailsClassType, Map<RailsStatsMetrics, Integer>> metrics) {
		this.metrics = metrics;
	}
	public String getCodeLocSummary() {
		return codeLocSummary;
	}
	public void setCodeLocSummary(String codeLocSummary) {
		this.codeLocSummary = codeLocSummary;
	}
	public String getTestLocSummary() {
		return testLocSummary;
	}
	public void setTestLocSummary(String testLocSummary) {
		this.testLocSummary = testLocSummary;
	}
	public String getCodeToTestRatio() {
		return codeToTestRatio;
	}
	public void setCodeToTestRatio(String codeToTestRatio) {
		this.codeToTestRatio = codeToTestRatio;
	}
}
