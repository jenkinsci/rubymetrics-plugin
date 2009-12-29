package hudson.plugins.rubyMetrics.flog.model;

import java.util.ArrayList;
import java.util.List;

public class FlogResults {
	public final float total;
	public final float average;
	private List<MethodResults> methodsResults = new ArrayList<MethodResults>();

	public FlogResults(String total, String average) {
		this.total = Float.parseFloat(total);
		this.average = Float.parseFloat(average);		
	}
	
	public List<MethodResults> getMethodsResults() {
		return methodsResults;
	}

	public void setMethodsResults(List<MethodResults> methodsResults) {
		this.methodsResults = methodsResults;
	}
	
	public void addMethodResult(MethodResults result) {
		methodsResults.add(result);
	}
	
	public void addOperatorResult(String name, String score) {
		if (!methodsResults.isEmpty()) {
			methodsResults.get(methodsResults.size() - 1).addOperator(name, score);
		}
	}
}
