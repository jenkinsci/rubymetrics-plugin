package hudson.plugins.rubyMetrics.rcov;

import hudson.plugins.rubyMetrics.rcov.model.RcovResult;

import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * RcovParser test
 * 
 * @author David Calavera
 *
 */
public class RcovParserTest extends TestCase {
	
	public void testParse() throws Exception {		
		InputStream input = this.getClass().getResourceAsStream("index.html");
		File root = new File(this.getClass().getResource("index.html").toURI()).getParentFile();
		
		RcovParser parser = new RcovParser(root);
		RcovResult result = parser.parse(input);
		
		assertNotNull(result);
		
		assertTrue(result.getFiles().size() > 0);
		
		assertNotNull(result.getTotalCoverage());
		assertNotNull(result.getTotalLines());
		assertNotNull(result.getCodeCoverage());
		assertNotNull(result.getCodeLines());
	}
}
