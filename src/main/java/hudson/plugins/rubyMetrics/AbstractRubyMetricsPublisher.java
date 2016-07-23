package hudson.plugins.rubyMetrics;

import hudson.model.*;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

public abstract class AbstractRubyMetricsPublisher extends Recorder {

    protected boolean fail(Run<?, ?> run, TaskListener listener, String message) {
        listener.getLogger().println(message);
        run.setResult(Result.FAILURE);
        return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
}
