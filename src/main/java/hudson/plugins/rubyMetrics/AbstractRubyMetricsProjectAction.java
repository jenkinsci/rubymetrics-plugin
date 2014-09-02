package hudson.plugins.rubyMetrics;

import hudson.model.*;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

@SuppressWarnings("unchecked")
public abstract class AbstractRubyMetricsProjectAction<T extends AbstractRubyMetricsBuildAction> extends Actionable implements ProminentProjectAction {

    protected final AbstractProject<?, ?> project;

    public AbstractRubyMetricsProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public AbstractProject<?, ?> getProject() {
        return project;
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
        for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            T r = b.getAction(getBuildActionClass());
            if (r != null)
                return r;
        }
        return null;
    }

    public Integer getLastResultBuild() {
        for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            T r = b.getAction(getBuildActionClass());
            if (r != null)
                return b.getNumber();
        }
        return null;
    }

}
