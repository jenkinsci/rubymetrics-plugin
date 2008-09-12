package hudson.plugins.rubyMetrics.rcov;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.model.ProminentProjectAction;
import hudson.model.Result;

import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class RcovProjectAction extends Actionable implements ProminentProjectAction {

	private final AbstractProject<?, ?> project;
	
	public RcovProjectAction(AbstractProject<?, ?> project) {
		this.project = project;
	}
	
	public AbstractProject<?, ?> getProject() {
        return project;
    }
	
	public String getDisplayName() {
		return "Rcov report";
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getUrlName() {
		return "rcov";
	}

    public String getSearchUrl() {
        return getUrlName();
    }
	
	public RcovBuildAction getLastResult() {
		for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
	        if (b.getResult() == Result.FAILURE)
	            continue;
	        RcovBuildAction r = b.getAction(RcovBuildAction.class);
	        if (r != null)
	            return r;
	    }
	    return null;
	}
	
	public Integer getLastResultBuild() {
		for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            RcovBuildAction r = b.getAction(RcovBuildAction.class);
            if (r != null)
                return b.getNumber();
        }
        return null;
	}
	
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
            rsp.sendRedirect2("../" + buildNumber + "/rcov");
        }
    }

}
