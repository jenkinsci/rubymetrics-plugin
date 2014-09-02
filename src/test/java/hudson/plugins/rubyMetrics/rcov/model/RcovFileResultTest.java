package hudson.plugins.rubyMetrics.rcov.model;

import java.lang.reflect.Field;

import hudson.model.FreeStyleBuild;
import hudson.model.Run;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import hudson.plugins.rubyMetrics.rcov.RcovBuildAction;

public class RcovFileResultTest {
    @Test
    public void testLoadOldBuildXml() throws Exception {
        FreeStyleBuild build = (FreeStyleBuild)Run.XSTREAM2.fromXML(this.getClass().getResourceAsStream("build.xml"));
        assertNotNull("Build was null", build);
        RcovBuildAction action = build.getAction(RcovBuildAction.class);
        assertNotNull("Build action was null", action);
        RcovFileResult result = action.getResults().getFiles().iterator().next();
        Field sourceCode = RcovFileResult.class.getDeclaredField("sourceCode");
        sourceCode.setAccessible(true);
        assertNull(sourceCode.get(result));
    }
}
