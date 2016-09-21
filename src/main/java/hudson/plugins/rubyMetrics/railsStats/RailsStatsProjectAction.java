package hudson.plugins.rubyMetrics.railsStats;

import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsProjectAction;

public class RailsStatsProjectAction<RailsStatsBuildAction> extends AbstractRubyMetricsProjectAction {

    public RailsStatsProjectAction(Job<?, ?> job) {
        super(job);
    }

    @Deprecated
    public RailsStatsProjectAction(AbstractProject<?, ?> project) {
        super(project);
    }

    public String getDisplayName() {
        return "Rails stats report";
    }

    public String getUrlName() {
        return "railsStats";
    }

    @Override
    protected Class getBuildActionClass() {
        return hudson.plugins.rubyMetrics.railsStats.RailsStatsBuildAction.class;
    }

}
