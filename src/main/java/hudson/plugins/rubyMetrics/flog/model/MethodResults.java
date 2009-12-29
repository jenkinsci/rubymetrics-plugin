package hudson.plugins.rubyMetrics.flog.model;

import java.util.HashMap;
import java.util.Map;

public class MethodResults {
	public final String name;
	public final float score;
	private Map<String, Float> operators = new HashMap<String, Float>();
	
	public MethodResults(String name, String score) {
		this.name = name;
		this.score = Float.parseFloat(score);
	}

	public Map<String, Float> getOperators() {
		return operators;
	}

	public void setOperators(Map<String, Float> operators) {
		this.operators = operators;
	}
	
	public void addOperator(String name, String score) {
		operators.put(name, Float.valueOf(score));
	}
}
