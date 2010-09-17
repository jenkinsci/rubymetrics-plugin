package hudson.plugins.rubyMetrics.railsNotes;

import hudson.plugins.rubyMetrics.railsNotes.model.RailsNotesMetrics;
import hudson.plugins.rubyMetrics.railsNotes.model.RailsNotesResults;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.StringOutputStream;

public class RailsNotesParser {
    public RailsNotesResults parse(StringOutputStream output) {
        return parse(output.toString());
    }

    public RailsNotesResults parse(String output) {
        RailsNotesResults response = new RailsNotesResults();

        String[] aux = output.split("[\n\r]");
        Collection<String> lines = new LinkedHashSet<String>(Arrays.asList(aux));
        lines = removeSeparators(lines);

        // Fortunately a LinkedHashSet has a predictable order, so this can use the filenames
        Iterator<String> linesIterator = lines.iterator();
        String lastFile = "";
        while (linesIterator.hasNext()) {
            String line = linesIterator.next();
            if (StringUtils.isEmpty(line.trim())) continue;
            // Filename lines don't start with a space. Annotation lines do.
            if (line.charAt(0) != ' ') {
                lastFile = line.substring(0, line.length() - 1); // remove colon from end of line
            } else {
                for (RailsNotesMetrics metric : RailsNotesMetrics.values()) {
                    // Match "  * [line#] [ANNOTATION]"
                    Pattern metricPattern = Pattern.compile("^  \\* \\[[\\s\\d]+\\] \\[" + metric.toString() + "\\]");
                    if (metricPattern.matcher(line).find()) {
                        response.addAnnotationFor(lastFile, metric);
                        break; // next line
                    }
                }
            }
        }

        response.setOutput(output);
        return response;
    }

    private Collection<String> removeSeparators(Collection<String> lines) {
        Collection<String> response = new LinkedHashSet<String>();
        for (String line : lines) {
            response.add(line.replaceAll("[\\r\\n+-]+", ""));
        }

        response.remove("");
        return response;
    }
}
