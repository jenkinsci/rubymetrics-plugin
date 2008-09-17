package hudson.plugins.rubyMetrics;

import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.Publisher;

public abstract class RubyMetricsPublisher extends Publisher {
	
	protected boolean fail(Build<?, ?> build, BuildListener listener, String message) {
    	listener.getLogger().println(message);
        build.setResult(Result.FAILURE);
    	return true;
    }
	
}
