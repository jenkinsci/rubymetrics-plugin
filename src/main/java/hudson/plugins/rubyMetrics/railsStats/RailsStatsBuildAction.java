package hudson.plugins.rubyMetrics.railsStats;

import hudson.model.AbstractBuild;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsBuildAction;
import hudson.plugins.rubyMetrics.railsStats.model.RailsStatsMetrics;
import hudson.plugins.rubyMetrics.railsStats.model.RailsStatsResults;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;

import java.io.IOException;
import java.util.Map;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class RailsStatsBuildAction extends AbstractRubyMetricsBuildAction {

    private final RailsStatsResults results;

    public RailsStatsBuildAction(AbstractBuild<?, ?> owner, RailsStatsResults results) {
        super(owner);
        this.results = results;
    }

    public RailsStatsResults getResults() {
        return results;
    }

    public String getDisplayName() {
        return "Rails stats";
    }

    public String getUrlName() {
        return "railsStats";
    }

    public void doGraphClasses(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (shouldGenerateGraph(req, rsp)) {
            generateGraph(req, rsp, getDataSetBuilder(RailsStatsMetrics.CLASSES));
        }
    }

    public void doGraphLoc(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (shouldGenerateGraph(req, rsp)) {
            generateGraph(req, rsp, getDataSetBuilder(RailsStatsMetrics.LOC));
        }
    }

    @Override
    protected DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilder() {
        return getDataSetBuilderRatios();
    }

    protected DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilder(
            RailsStatsMetrics metric) {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (RailsStatsBuildAction a = this; a != null; a = a.getPreviousResult()) {
            ChartUtil.NumberOnlyBuildLabel buildLabel = new ChartUtil.NumberOnlyBuildLabel(a.owner);

            for (Map.Entry<String, Map<RailsStatsMetrics, Integer>> entry : a.results.getMetrics()
                    .entrySet()) {
                if (entry.getKey().equalsIgnoreCase("Total")) {
                    continue;
                }
                dsb.add(entry.getValue().get(metric), entry.getKey(), buildLabel);
            }
        }

        return dsb;
    }

    protected DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilderRatios() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (RailsStatsBuildAction a = this; a != null; a = a.getPreviousResult()) {
            ChartUtil.NumberOnlyBuildLabel buildLabel = new ChartUtil.NumberOnlyBuildLabel(a.owner);

            int sumLoc = 0, sumLines = 0, sumTestLoc = 0, sumClasses = 0, sumMethods = 0;

            for (Map.Entry<String, Map<RailsStatsMetrics, Integer>> entry : a.results.getMetrics()
                    .entrySet()) {
                String label = entry.getKey();
                if (label.equalsIgnoreCase("Total")) {
                    continue;
                } else if (label.endsWith(" tests")) {
                    sumTestLoc += entry.getValue().get(RailsStatsMetrics.LOC);
                } else {
                    sumLoc += entry.getValue().get(RailsStatsMetrics.LOC);
                    sumLines += entry.getValue().get(RailsStatsMetrics.LINES);
                    sumClasses += entry.getValue().get(RailsStatsMetrics.CLASSES);
                    sumMethods += entry.getValue().get(RailsStatsMetrics.METHODS);
                }
            }

            dsb.add(sumLoc / (double) sumMethods, RailsStatsMetrics.LOC_M.prettyPrint(), buildLabel);
            dsb.add(sumMethods / (double) sumClasses, RailsStatsMetrics.M_C.prettyPrint(),
                    buildLabel);
            dsb.add(sumTestLoc / (double) sumLoc, "Test/Code", buildLabel);
            dsb.add(sumLines / (double) sumLoc, "Lines/LOC", buildLabel);
        }

        return dsb;
    }

    protected DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilderOriginal() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (RailsStatsBuildAction a = this; a != null; a = a.getPreviousResult()) {
            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.owner);

            for (Map.Entry<RailsStatsMetrics, Integer> entry : a.results.getTotal().entrySet()) {
                dsb.add(entry.getValue(), entry.getKey().prettyPrint(), label);
            }
        }

        return dsb;
    }

}
