package hudson.plugins.rubyMetrics.flog.model;

import java.util.HashMap;
import java.util.Map;

public class FlogBuildResults {
    private float total;
    private float average;
    private Map<String, FlogFileResults> fileResults = new HashMap<String, FlogFileResults>();

    public float getTotal() {
        return total;
    }

    public float getAverage() {
        return average;
    }

    public Map<String, FlogFileResults> getFileResults() {
        return fileResults;
    }

    public void setFileResults(Map<String, FlogFileResults> fileResults) {
        this.fileResults = fileResults;
    }

    public void addFileResults(String file, FlogFileResults results) {
        fileResults.put(file, results);
        sumTotal(results.total);
        sumAverage(results.average);
    }

    private void sumTotal(float total) {
        this.total += total;
    }

    private void sumAverage(float average) {
        this.average += average;
    }
}
