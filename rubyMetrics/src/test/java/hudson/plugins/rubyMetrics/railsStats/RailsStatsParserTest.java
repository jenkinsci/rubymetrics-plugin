package hudson.plugins.rubyMetrics.railsStats;

import hudson.plugins.rubyMetrics.railsStats.model.RailsStatsResults;
import junit.framework.TestCase;

public class RailsStatsParserTest extends TestCase {
	
	public void testParse() throws Exception {
		RailsStatsParser parser = new RailsStatsParser();
		
		String out = 	"+----------------------+-------+-------+---------+---------+-----+-------+\n" +
						"| Name                 | Lines |   LOC | Classes | Methods | M/C | LOC/M |\n" +
						"+----------------------+-------+-------+---------+---------+-----+-------+\n" +
						"| Controllers          |    15 |     4 |       1 |       0 |   0 |    	0 |\n" +
						"| Helpers              |     3 |     2 |       0 |       0 |   0 |     0 |\n" +
						"| Models               |     0 |     0 |       0 |       0 |   0 |     0 |\n" +
						"| Libraries            |     0 |     0 |       0 |       0 |   0 |     0 |\n" +
						"| Integration tests    |     0 |     0 |       0 |       0 |   0 |     0 |\n" +
						"| Functional tests     |     0 |     0 |       0 |       0 |   0 |     0 |\n" +
						"| Unit tests           |     0 |     0 |       0 |       0 |   0 |     0 |\n" +
						"+----------------------+-------+-------+---------+---------+-----+-------+\n" +
						"| Total                |    18 |     6 |       1 |       0 |   0 |     0 |\n" +
						"+----------------------+-------+-------+---------+---------+-----+-------+\n" +
						"Code LOC: 6     Test LOC: 0     Code to Test Ratio: 1:0.0";
		
		RailsStatsResults metrics = parser.parse(out);
		
		assertTrue(!metrics.getMetrics().isEmpty());
		assertNotNull(metrics.getCodeLocSummary());
		assertNotNull(metrics.getTestLocSummary());
		assertNotNull(metrics.getCodeToTestRatio());
	}
}
