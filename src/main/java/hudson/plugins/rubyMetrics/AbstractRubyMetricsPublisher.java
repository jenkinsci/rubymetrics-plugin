package hudson.plugins.rubyMetrics;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

public abstract class AbstractRubyMetricsPublisher extends Recorder {

    protected boolean fail(AbstractBuild<?, ?> build, BuildListener listener, String message) {
        listener.getLogger().println(message);
        build.setResult(Result.FAILURE);
        return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
}
