package hudson.plugins.rubyMetrics.flog;

import hudson.plugins.rubyMetrics.flog.model.FlogFileResults;
import hudson.plugins.rubyMetrics.flog.model.FlogMethodResults;
import jregex.Matcher;
import jregex.Pattern;

import org.codehaus.plexus.util.StringOutputStream;

public class FlogParser {
	
	private final static Pattern operatorRegex = new Pattern("\\s*({score}\\d+\\.\\d+):\\s({operator}.*)$");
	private final static Pattern methodRegex = new Pattern("\\s*({score}\\d+\\.\\d+):\\s+({method}[A-Za-z:]+(?:#|::).*)");
	
	public FlogFileResults parse(StringOutputStream results) {
		return parse(results.toString());
	}
	
	public FlogFileResults parse(String results) {
		String[] resultsSplit = results.split("\n\n");
		if (resultsSplit == null || resultsSplit.length == 0) {
			return null;
		}
		String[] totalAndAverage = resultsSplit[0].split("\n");
		String total = getScoreFromOperator(totalAndAverage[0]);
		String average = getScoreFromOperator(totalAndAverage[1]);
		
		FlogFileResults flogResults = new FlogFileResults(total, average);
		for (int index = 1; index < resultsSplit.length; index++) {
			for (String line : resultsSplit[index].split("\n")) {
				addFlogResults(flogResults, line);
			}
		}
		
		return flogResults;
	}
	
	private String getScoreFromOperator(String line) {
		Matcher matcher = operatorRegex.matcher(line);
		matcher.matches();
		return matcher.group("score");
	}
	
	private void addFlogResults(FlogFileResults flogResults, String line) {
		Matcher matcher = methodRegex.matcher(line);
		if (matcher.matches()) {
			FlogMethodResults methodResults = new FlogMethodResults(matcher.group("method"), matcher.group("score"));
			flogResults.addMethodResult(methodResults);
		} else {
			matcher = operatorRegex.matcher(line);
			if (matcher.matches()) {
				flogResults.addOperatorResult(matcher.group("operator"), matcher.group("score"));
			}
		}
	}
}
