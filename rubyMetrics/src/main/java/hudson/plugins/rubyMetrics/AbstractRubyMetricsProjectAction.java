package hudson.plugins.rubyMetrics;

import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.model.ProminentProjectAction;

import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

@SuppressWarnings("unchecked")
public abstract class AbstractRubyMetricsProjectAction extends Actionable
		implements ProminentProjectAction {

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
	
	protected abstract AbstractRubyMetricsBuildAction getLastResult();
	protected abstract Integer getLastResultBuild();
	
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
	
}
