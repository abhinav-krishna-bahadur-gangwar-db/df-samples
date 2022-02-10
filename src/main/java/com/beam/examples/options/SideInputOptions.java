package com.beam.examples.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;

public interface SideInputOptions extends SimplePipelineOptions{
    @Description("Currency Rates File Path")
    @Default.String("data/input/SampleRates.txt")
    String getCurrencyFile();
    void setCurrencyFile(String value);
}
