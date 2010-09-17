package hudson.plugins.rubyMetrics.flog.model;

import java.util.HashMap;
import java.util.Map;

public class FlogMethodResults {
    public final String name;
    public final float score;
    private Map<String, Float> operatorResults = new HashMap<String, Float>();

    public FlogMethodResults(String name, String score) {
        this.name = name;
        this.score = Float.parseFloat(score);
    }

    public Map<String, Float> getOperatorResults() {
        return operatorResults;
    }

    public void setOperatorResults(Map<String, Float> operators) {
        this.operatorResults = operators;
    }

    public void addOperator(String name, String score) {
        operatorResults.put(name, Float.valueOf(score));
    }
}
