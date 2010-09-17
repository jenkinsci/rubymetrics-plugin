package hudson.plugins.rubyMetrics.railsNotes;

import hudson.plugins.rubyMetrics.railsNotes.model.RailsNotesResults;
import junit.framework.TestCase;

public class RailsNotesParserTest extends TestCase {
    public void testParse() throws Exception {
        RailsNotesParser parser = new RailsNotesParser();

        String out =    "app/controllers/a_controller.rb:\n" +
                        "  * [ 53] [TODO] do this\n" +
                        "\n" +
                        "app/models/b model.rb:\n" +
                        "  * [  1] [FIXME] [TODO]\n" + // should end up as a FIXME
                        "\n" +
                        "app/models/c_model.rb:\n" +
                        "  * [111] [OPTIMIZE]\n" +
                        "  * [222] [TODO]\n" +
                        "\n" +
                        "test/unit/b test.rb:\n" +
                        "  * [  2] [TODO]\n" +
                        "\n" +
                        "test/unit/c_test.rb:\n" +
                        "  * [  5] [FIXME]\n" +
                        "\n" +
                        "\n";

        RailsNotesResults metrics = parser.parse(out);

        assertFalse(metrics.getMetrics().isEmpty());
        assertNotNull(metrics.getOutput());
        assertFalse(metrics.getOutput() == "");
    }
}
