package hudson.plugins.rubyMetrics.rcov;

import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsProjectAction;

public class RcovProjectAction<RcovBuildAction> extends AbstractRubyMetricsProjectAction {

    public RcovProjectAction(Job<?, ?> job) {
        super(job);
    }

    @Deprecated
    public RcovProjectAction(AbstractProject<?, ?> project) {
        super(project);
    }

    public String getDisplayName() {
        return "Rcov report";
    }

    public String getUrlName() {
        return "rcov";
    }

    @Override
    protected Class getBuildActionClass() {
        return hudson.plugins.rubyMetrics.rcov.RcovBuildAction.class;
    }

}
