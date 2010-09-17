package hudson.plugins.rubyMetrics.railsNotes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RailsNotesResults {
    private Map<String, Map<RailsNotesMetrics, Integer>> metrics = new HashMap<String, Map<RailsNotesMetrics,Integer>>();
    private List<String> sortedLabels = new ArrayList<String>();

    private class SortLabelsComparator implements Comparator<String> {

        private final List<String> labels;
        public SortLabelsComparator(List<String> coll) {
            labels = coll;
        }

        public int compare(String o1, String o2) {
            return new Integer(labels.indexOf(o1)).compareTo(labels.indexOf(o2));
        }

    }

    private String output;

    public Collection<String> getHeaders() {
        Collection<String> headers = new LinkedHashSet<String>();
        headers.add("Filename");

        for (RailsNotesMetrics metric : RailsNotesMetrics.values()) {
            headers.add(metric.toString());
        }

        return headers;
    }

    /**
     * Add an instance of <code>annotation</code> in the file <code>fileName</code>. This will
     * create an entry in <code>metrics</code> for <code>fileName</code> if one does not already
     * exist.
     *
     * @param fileName the filename from the output of Rails notes.
     * @param annotation the <code>RailsNotesMetrics</code> value associated with the annotation
     *                   in the report.
     */
    public void addAnnotationFor(String fileName, RailsNotesMetrics annotation) {
        if (!sortedLabels.contains(fileName)) {
            sortedLabels.add(fileName);
        }

        // Create the map if it doesn't already exist, seed each annotation with a count of 0.
        if (!metrics.containsKey(fileName)) {
            Map<RailsNotesMetrics, Integer> metric = new TreeMap<RailsNotesMetrics, Integer>();
            for (RailsNotesMetrics value : RailsNotesMetrics.values()) {
                metric.put(value, new Integer(0));
            }
            metrics.put(fileName, metric);
        }
        // Get this file's map and add 1 to the count of that metric.
        Map<RailsNotesMetrics, Integer> metric = metrics.get(fileName);
        metric.put(annotation, new Integer(metric.get(annotation).intValue() + 1));
    }

    /**
     * Generate the total count of each annotation by adding together all the annotations in
     * <code>metrics</code>.
     *
     * @return A <code>Map</code> to insert into <code>metrics</code> with each annotation and a
     *         total count.
     */
    public Map<RailsNotesMetrics, Integer> getTotal() {
        Map<RailsNotesMetrics, Integer> total = new TreeMap<RailsNotesMetrics, Integer>();
        // Initialize all the values
        for (RailsNotesMetrics metric : RailsNotesMetrics.values()) total.put(metric, new Integer(0));

        // For each class entered, add its counts to the total
        for (Map<RailsNotesMetrics, Integer> fileEntry : metrics.values()) {
            for (RailsNotesMetrics metric : fileEntry.keySet()) {
                total.put(metric, new Integer(total.get(metric).intValue() + fileEntry.get(metric).intValue()));
            }
        }

        return total;
    }

    public Map<String, Map<RailsNotesMetrics, Integer>> getMetrics() {
        Comparator<String> comparator = new SortLabelsComparator(sortedLabels);

        Map<String, Map<RailsNotesMetrics, Integer>> response =
            new TreeMap<String, Map<RailsNotesMetrics,Integer>>(comparator);

        for (Map.Entry<String, Map<RailsNotesMetrics, Integer>> entry : metrics.entrySet()) {
            response.put(entry.getKey(), entry.getValue());
        }

        // Include the total
        response.put("Total", getTotal());

        return response;
    }

    /**
     * @return the output of the "rake notes" command.
     */
    public String getOutput() {
        return output;
    }

    /**
     * @param output the output of the "rake notes" command.
     */
    public void setOutput(String output) {
        this.output = output;
    }
}
