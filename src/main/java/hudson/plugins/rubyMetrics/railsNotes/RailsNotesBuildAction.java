package hudson.plugins.rubyMetrics.railsNotes;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsBuildAction;
import hudson.plugins.rubyMetrics.railsNotes.model.RailsNotesMetrics;
import hudson.plugins.rubyMetrics.railsNotes.model.RailsNotesResults;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

import java.util.Map;

public class RailsNotesBuildAction extends AbstractRubyMetricsBuildAction {
    private final RailsNotesResults results;

    public RailsNotesBuildAction(AbstractBuild<?, ?> owner, RailsNotesResults results) {
        super(owner);
        this.results = results;
    }

    public RailsNotesResults getResults() {
        return results;
    }

    public String getDisplayName() {
        return "Annotations (Rails notes)";
    }

    public String getUrlName() {
        return "railsNotes";
    }

    @Override
    protected DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilder() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (RailsNotesBuildAction a = this; a != null; a = a.getPreviousResult()) {
            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.owner);

            for (Map.Entry<RailsNotesMetrics, Integer> entry : a.getResults().getTotal().entrySet()) {
                dsb.add(entry.getValue(), entry.getKey().toString(), label);
            }
        }

        return dsb;
    }

    @Override
    protected String getRangeAxisLabel() {
        return "Annotations";
    }
}
