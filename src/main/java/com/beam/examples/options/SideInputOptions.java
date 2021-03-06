package com.beam.examples.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.ValueProvider;

public interface SideInputOptions extends SimplePipelineOptions{
    @Description("Currency Rates File Path")
    @Default.String("data/input/SampleRates.txt")
    ValueProvider<String> getCurrencyFile();
    void setCurrencyFile(ValueProvider<String> value);
}
