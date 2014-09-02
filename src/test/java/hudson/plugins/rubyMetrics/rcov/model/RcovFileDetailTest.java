package hudson.plugins.rubyMetrics.rcov.model;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.plugins.rubyMetrics.rcov.RcovBuildAction;
import hudson.plugins.rubyMetrics.rcov.RcovPublisher;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class RcovFileDetailTest {
    private static final String COVERAGE_DIR = "coverage/rcov";

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void testLoadSourceCode() throws Exception {
        File resourceDir = new File(this.getClass().getResource("index.html").toURI()).getParentFile();
        final FilePath resourcePath = new FilePath(resourceDir);

        FreeStyleProject project = j.createFreeStyleProject();

        // Builder to copy the coverage fixture files into the workspace
        project.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                BuildListener listener) throws InterruptedException, IOException {

                FilePath rcovDir = build.getWorkspace().child(COVERAGE_DIR);
                rcovDir.mkdirs();
                resourcePath.copyRecursiveTo("*.html", rcovDir);
                return true;
            }
        });

        // Add the rcov publisher
        RcovPublisher publisher = new RcovPublisher(COVERAGE_DIR);
        publisher.setTargets(Collections.<MetricTarget>emptyList());
        project.getPublishersList().add(publisher);

        // Run and wait for the build; assert success
        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        // Assert that the build has an rcov build action attached
        RcovBuildAction action = build.getAction(RcovBuildAction.class);
        assertNotNull("Missing RcovBuildAction for build " + build, action);

        // Check each detail object to see that it can load its source code
        for (RcovFileResult result: action.getResults().getFiles()) {
            RcovFileDetail detail = new RcovFileDetail(build, result);
            String sourceCode = detail.loadSourceCode();
            assertNotNull("Missing source code for file " + result.getName(), sourceCode);
            assertThat("Missing table tag in source code for file " + result.getName(), sourceCode, containsString("<table"));
        }
    }
}
