package hudson.plugins.rubyMetrics;

import hudson.model.*;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

@SuppressWarnings("unchecked")
public abstract class AbstractRubyMetricsProjectAction<T extends AbstractRubyMetricsBuildAction> extends Actionable implements ProminentProjectAction {

    protected final Job<?, ?> job;

    public AbstractRubyMetricsProjectAction(Job<?, ?> job) {
        this.job = job;
    }

    public Job<?, ?> getJob() {
      return job;
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    protected abstract Class<T> getBuildActionClass();

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (getLastResult() != null) {
            getLastResult().doGraph(req, rsp);
        }
    }

    public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Integer buildNumber = getLastResultBuild();
        if (buildNumber == null) {
            rsp.sendRedirect2("nodata");
        } else {
            rsp.sendRedirect2("../" + buildNumber + "/" + getUrlName());
        }
    }

    public T getLastResult() {
        for (Run<?, ?> r = job.getLastStableBuild(); r != null; r = r.getPreviousNotFailedBuild()) {
            if (r.getResult() == Result.FAILURE)
                continue;
            T result = r.getAction(getBuildActionClass());
            if (result != null)
                return result;
        }
        return null;
    }

    public Integer getLastResultBuild() {
        for (Run<?, ?> r = job.getLastStableBuild(); r != null; r = r.getPreviousNotFailedBuild()) {
            if (r.getResult() == Result.FAILURE)
                continue;
            T result = r.getAction(getBuildActionClass());
            if (result != null)
                return r.getNumber();
        }
        return null;
    }

}
