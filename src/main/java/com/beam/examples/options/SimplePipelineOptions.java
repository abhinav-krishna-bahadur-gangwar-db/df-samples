package com.beam.examples.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface SimplePipelineOptions extends PipelineOptions {

    @Description("Path of input File containing Trades")
    @Default.String("data/input/SampleTrades.csv")
    String getInputTradeFile();
    void setInputTradeFile(String value);

    @Description("Output File")
    @Default.String("data/output/Output.txt")
    String getOutputFile();
    void setOutputFile(String value);
}
