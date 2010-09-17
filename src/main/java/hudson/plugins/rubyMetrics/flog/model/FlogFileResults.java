package hudson.plugins.rubyMetrics.flog.model;

import java.util.ArrayList;
import java.util.List;

public class FlogFileResults {
    public final float total;
    public final float average;
    private List<FlogMethodResults> methodResults = new ArrayList<FlogMethodResults>();

    public FlogFileResults(String total, String average) {
        this.total = Float.parseFloat(total);
        this.average = Float.parseFloat(average);
    }

    public List<FlogMethodResults> getMethodResults() {
        return methodResults;
    }

    public void setMethodResults(List<FlogMethodResults> methodResults) {
        this.methodResults = methodResults;
    }

    public void addMethodResult(FlogMethodResults result) {
        methodResults.add(result);
    }

    public void addOperatorResult(String name, String score) {
        if (!methodResults.isEmpty()) {
            methodResults.get(methodResults.size() - 1).addOperator(name, score);
        }
    }

    public float getTotal() {
        return total;
    }

    public float getAverage() {
        return average;
    }
}
