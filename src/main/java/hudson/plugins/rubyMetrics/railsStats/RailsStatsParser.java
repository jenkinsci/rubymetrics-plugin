package hudson.plugins.rubyMetrics.railsStats;

import hudson.plugins.rubyMetrics.railsStats.model.RailsClassType;
import hudson.plugins.rubyMetrics.railsStats.model.RailsStatsMetrics;
import hudson.plugins.rubyMetrics.railsStats.model.RailsStatsResults;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.plexus.util.StringOutputStream;

public class RailsStatsParser {
	
	public RailsStatsResults parse(StringOutputStream output) {
		return parse(output.toString());
	}
	
	public RailsStatsResults parse(String output) {
		RailsStatsResults response = new RailsStatsResults();
		
		String[] aux = output.split("[\n\r]");
		Collection<String> lines = new LinkedHashSet<String>(Arrays.asList(aux));
				
		lines = removeSeparators(lines);		
		
		Iterator<String> linesIterator = lines.iterator();
		String[] header = cleanArray(linesIterator.next().split("[|]+")); //report header
				
		while (linesIterator.hasNext()) {
			String line = linesIterator.next();
			String[] columns = cleanArray(line.split("[|]+"));
			if (columns.length > 1) { //not last line
				RailsClassType classType = RailsClassType.toRailsClassType(columns[0]);
				Map<RailsStatsMetrics, Integer> metrics = new HashMap<RailsStatsMetrics, Integer>();
				
				for (int i = 1; i < columns.length; i++) { //columns[0] == rails class type
					metrics.put(RailsStatsMetrics.toRailsStatsMetrics(header[i]), Integer.valueOf(columns[i]));
				}
				
				response.addMetric(classType, metrics);
			} else {
				Pattern pattern = Pattern.compile("CodeLOC:([0-9]+)TestLOC:([0-9]+)CodetoTestRatio:([0-9:.]+)");
				Matcher matcher = pattern.matcher(columns[0]);
				if (matcher.matches()) {
					response.setCodeLocSummary(matcher.group(1));
					response.setTestLocSummary(matcher.group(2));
					response.setCodeToTestRatio(matcher.group(3));
				}
			}
				
		}
		
		return response;
	}
	
	private Collection<String> removeSeparators(Collection<String> lines) {
		Collection<String> response = new LinkedHashSet<String>();
		for (String line : lines) {
			response.add(line.replaceAll("[\\s\\r\\n+-]+", ""));
		}
		
		response.remove("");		
		return response;
	}
	
	private String[] cleanArray(String[] array) {
		Collection<String> response = new LinkedHashSet<String>(Arrays.asList(array));
		response = removeSeparators(response);
		return response.toArray(new String[response.size()]);
	}
}
