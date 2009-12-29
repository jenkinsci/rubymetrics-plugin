package hudson.plugins.rubyMetrics.flog;

import hudson.plugins.rubyMetrics.flog.model.FlogResults;

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
		
		FlogResults results = parser.parse(mock.toString());
		assertEquals(80.3f, results.total);
		assertEquals(40.2f, results.average);
		assertEquals(2, results.getMethodsResults().size());
		
		assertEquals(79.2f, results.getMethodsResults().get(0).score);
		assertNotNull(results.getMethodsResults().get(0).name);
		assertFalse(results.getMethodsResults().get(0).getOperators().isEmpty());
		assertEquals(1.1f, results.getMethodsResults().get(1).score);
		assertNotNull(results.getMethodsResults().get(0).name);
		assertFalse(results.getMethodsResults().get(1).getOperators().isEmpty());
	}
}
