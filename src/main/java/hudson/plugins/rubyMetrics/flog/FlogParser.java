package hudson.plugins.rubyMetrics.flog;

import hudson.plugins.rubyMetrics.flog.model.FlogFileResults;
import hudson.plugins.rubyMetrics.flog.model.FlogMethodResults;

import java.util.ArrayList;
import java.util.Collection;

import jregex.Matcher;
import jregex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.StringOutputStream;

public class FlogParser {

    private final static Pattern operatorRegex = new Pattern("\\s*({score}\\d+\\.\\d+):\\s({operator}.*)$");
    private final static Pattern methodRegex = new Pattern("\\s*({score}\\d+\\.\\d+):\\s+({method}[A-Za-z:]+(?:#|::).*)");

    public FlogFileResults parse(String filePath, StringOutputStream results) {
        return parse(filePath, results.toString());
    }

    public FlogFileResults parse(String filePath, String results) {
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
                addFlogResults(filePath, flogResults, line);
            }
        }

        return flogResults;
    }

    private String getScoreFromOperator(String line) {
        Matcher matcher = operatorRegex.matcher(line);
        return matcher.matches() ? matcher.group("score") : "0.0";
    }

    private void addFlogResults(String filePath, FlogFileResults flogResults, String line) {
        Matcher matcher = methodRegex.matcher(line);
        if (matcher.matches()) {
            String methodName = prettifyMethodPath(filePath, matcher.group("method"));
            FlogMethodResults methodResults = new FlogMethodResults(methodName, matcher.group("score"));
            flogResults.addMethodResult(methodResults);
        } else {
            matcher = operatorRegex.matcher(line);
            if (matcher.matches()) {
                flogResults.addOperatorResult(matcher.group("operator"), matcher.group("score"));
            }
        }
    }

    private String prettifyMethodPath(String filePath, String methodName) {
        if (!methodName.contains(filePath)) {
            return methodName;
        }
        String[] split = methodName.split("\\s+");

        Collection<String> line = new ArrayList<String>();
        for (String s : split) {
            if (s.contains(filePath)) {
                s = s.substring(s.indexOf(filePath));
            }
            line.add(s);
        }
        return StringUtils.join(line, " ");
    }
}
