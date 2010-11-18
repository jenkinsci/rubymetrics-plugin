package hudson.plugins.rubyMetrics.flog;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsBuildAction;
import hudson.plugins.rubyMetrics.flog.model.FlogBuildResults;
import hudson.plugins.rubyMetrics.flog.model.FlogFileResults;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;

public class FlogBuildAction extends AbstractRubyMetricsBuildAction {

    private final FlogBuildResults results;

    public FlogBuildAction(AbstractBuild<?, ?> owner, FlogBuildResults results) {
        super(owner);
        this.results = results;
    }

    @Override
    protected DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilder() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (FlogBuildAction action = this; action != null; action = action.getPreviousResult()) {
            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(action.owner);

            dsb.add(action.getResults().getTotal(), "Total score", label);
            dsb.add(action.getResults().getAverage(), "Average score", label);
        }

        return dsb;
    }

    @Override
    protected NumberAxis getRangeAxis(CategoryPlot plot) {
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperBound(new BigDecimal(results.getTotal()).round(MathContext.DECIMAL32).intValue() + 20);
        rangeAxis.setLowerBound(0);

        return rangeAxis;
    }



    public FlogBuildResults getResults() {
        return results;
    }

    public String getDisplayName() {
        return "Flog report";
    }

    public String getUrlName() {
        return "flog";
    }
}
