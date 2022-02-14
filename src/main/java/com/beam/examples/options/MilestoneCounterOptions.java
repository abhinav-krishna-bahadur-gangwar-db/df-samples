package com.beam.examples.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.options.ValueProvider;

public interface MilestoneCounterOptions extends SideInputOptions{
    @Description("Minimum Price Limit")
    @Default.String("100")
    ValueProvider<String> getMinPriceLimit();
    void setMinPriceLimit(ValueProvider<String> valueProvider);

    @Description("Path to write Milestone metrics")
    @Default.String("data/output/milestone-metrics.log")
    @Validation.Required
    ValueProvider<String> getMetricsPath();
    void setMetricsPath(ValueProvider<String> path);
}
