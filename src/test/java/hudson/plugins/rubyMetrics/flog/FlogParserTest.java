package hudson.plugins.rubyMetrics.flog;

import hudson.plugins.rubyMetrics.flog.model.FlogFileResults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.TestCase;

public class FlogParserTest extends TestCase {

    public void testParse() throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(getClass().getResourceAsStream("flog-results-sample.txt")));
        FlogParser parser = new FlogParser();

        StringBuilder mock = new StringBuilder("");
        String line;
        while ((line = reader.readLine()) != null) {
            mock.append(line + "\n");
            if (line == null || line.length() == 0) {
                mock.append("\n");
            }
        }

        FlogFileResults results = parser.parse("lib/trinidad/command_line_parser.rb", mock.toString());
        assertEquals(80.3f, results.total);
        assertEquals(40.2f, results.average);
        assertEquals(2, results.getMethodResults().size());

        assertEquals(79.2f, results.getMethodResults().get(0).score);
        assertEquals("CommandLineParser::parse lib/trinidad/command_line_parser.rb:5", results.getMethodResults().get(0).name.replaceAll("\\s+", " "));
        assertFalse(results.getMethodResults().get(0).getOperatorResults().isEmpty());
        assertEquals(1.1f, results.getMethodResults().get(1).score);
        assertNotNull(results.getMethodResults().get(0).name);
        assertFalse(results.getMethodResults().get(1).getOperatorResults().isEmpty());
    }
}
