package hudson.plugins.rubyMetrics.flog;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.FreeStyleProject;
import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.StringOutputStream;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;

public class FlogExecutorTest extends HudsonTestCase {

    FlogExecutor flog = new FlogExecutor();
    EnvVars environment = new EnvVars();
    Launcher launcher;
    FilePath workspace;
    FreeStyleProject project;

    @BeforeClass
    public void setup() throws InterruptedException, IOException {
        FreeStyleProject project = createFreeStyleProject();
        launcher = super.createLocalLauncher();
        workspace = project.getSomeWorkspace();
    }

    @Test
    public void testExecute() throws InterruptedException, IOException {
        if (flog.isFlogInstalled(launcher, environment, workspace)) {
            assertTrue(flog.execute(new String[]{"/tmp"}, launcher, environment, workspace, project.getRootDir()).isEmpty());
        }
    }

    @Test
    public void testJoin() throws InterruptedException, IOException {
        if (flog.isFlogInstalled(launcher, environment, workspace)) {
            ArgumentListBuilder arguments = flog.arguments("-ad", new File("command_line_parser.rb").getAbsolutePath());
            StringOutputStream out = flog.launch(arguments, launcher, environment, workspace);
            assertNotNull(out);
            assertTrue(out.toString().contains("CommandLineParser::parse"));
        }
    }
}
