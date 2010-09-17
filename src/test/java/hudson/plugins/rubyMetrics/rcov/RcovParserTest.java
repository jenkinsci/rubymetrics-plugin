package hudson.plugins.rubyMetrics.rcov;

import hudson.plugins.rubyMetrics.rcov.model.RcovFileResult;
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

        assertReportIsComplete(root, input);
    }

    public void testParseRcov_0_9() throws Exception {
        InputStream input = this.getClass().getResourceAsStream("index_0_9.html");
        File root = new File(this.getClass().getResource("index_0_9.html").toURI()).getParentFile();

        assertReportIsComplete(root, input);
    }

    public void testParseSourceNewHtml() throws Exception {
        assertSourceIsWellFormed("lib-trinidad-core_ext_rb.html", "<table class=\"details\">", "</table>");
    }

    public void testParseSourceLegacy() throws Exception {
        assertSourceIsWellFormed("lib-algebra_rb.html", "<pre>", "</pre>");
    }

    private void assertSourceIsWellFormed(String href, String start, String end) throws Exception {
        File root = new File(this.getClass().getResource(href).toURI()).getParentFile();
        RcovParser parser = new RcovParser(root);

        String source = parser.parseSource(href);
        assertNotNull(source);
        assertTrue(source.startsWith(start));
        assertTrue(source.endsWith(end));
    }

    private void assertReportIsComplete(File root, InputStream input) throws Exception {
        RcovParser parser = new RcovParser(root);
        RcovResult result = parser.parse(input);

        assertNotNull(result);

        assertTrue(result.getFiles().size() > 0);

        assertIsAValidNode(result.getTotalCoverage());
        assertIsAValidNode(result.getTotalLines());
        assertIsAValidNode(result.getCodeCoverage());
        assertIsAValidNode(result.getCodeLines());

        //Check first file
        RcovFileResult fileResult = result.getFiles().iterator().next();

        assertIsAValidNode(fileResult.getTotalCoverage());
        assertIsAValidNode(fileResult.getTotalLines());
        assertIsAValidNode(fileResult.getCodeCoverage());
        assertIsAValidNode(fileResult.getCodeLines());
    }

    private void assertIsAValidNode(String element) {
        assertTrue(element.matches("[0-9%.]+"));
    }
}
