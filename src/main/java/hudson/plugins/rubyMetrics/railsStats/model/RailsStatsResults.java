package hudson.plugins.rubyMetrics.railsStats.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RailsStatsResults {
	
	private Map<String, Map<RailsStatsMetrics, Integer>> metrics = new HashMap<String, Map<RailsStatsMetrics,Integer>>();
	private List<String> sortedLabels = new ArrayList<String>();
	
	private class SortLabelsComparator implements Comparator<String> {

		private final List<String> sortedLabels;
		public SortLabelsComparator(List<String> coll) {
			sortedLabels = coll;
		}
		
		public int compare(String o1, String o2) {
			return new Integer(sortedLabels.indexOf(o1)).compareTo(sortedLabels.indexOf(o2));
		}
		
	}
	
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
	
	public void addMetric(String classType, Map<RailsStatsMetrics, Integer> metric) {
		metrics.put(classType, metric);
		if (!sortedLabels.contains(classType)) {
			sortedLabels.add(classType);
		}
	}
	
	public Map<RailsStatsMetrics, Integer> getTotal() {		
		return metrics.get("Total");
	}
	
	public Map<String, Map<RailsStatsMetrics, Integer>> getMetrics() {
		Comparator<String> comparator = new SortLabelsComparator(sortedLabels);
		
		Map<String, Map<RailsStatsMetrics, Integer>> response = 
			new TreeMap<String, Map<RailsStatsMetrics,Integer>>(comparator);
		
		for (Map.Entry<String, Map<RailsStatsMetrics, Integer>> entry : metrics.entrySet()) {
			response.put(entry.getKey(), entry.getValue());
		}
		
		return response;
	}
	public void setMetrics(Map<String, Map<RailsStatsMetrics, Integer>> metrics) {
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
