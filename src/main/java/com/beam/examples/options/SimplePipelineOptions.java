package com.beam.examples.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.ValueProvider;

public interface SimplePipelineOptions extends PipelineOptions {

    @Description("Path of input File containing Trades")
    @Default.String("data/input/SampleTrades.csv")
    ValueProvider<String> getInputTradeFile();
    void setInputTradeFile(ValueProvider<String> value);

    @Description("Output File")
    @Default.String("data/output/Output.txt")
    ValueProvider<String> getOutputFile();
    void setOutputFile(ValueProvider<String> value);
}
