package com.beam.examples;

import com.beam.examples.model.TradeIn;
import com.beam.examples.options.SimplePipelineOptions;
import com.beam.examples.transforms.CsvRecordParser;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.ToString;
import org.apache.commons.lang3.StringUtils;

public class SimpleFilterPipeline {
    public static void main(String[] args){
        SimplePipelineOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(SimplePipelineOptions.class);
        runPipeline(options);
    }

    private static void runPipeline(SimplePipelineOptions options) {
        Pipeline p = Pipeline.create(options);
        p.apply("ReadLines", TextIO.read().from(options.getInputTradeFile()))
                .apply("FilterHeader", Filter.by(input -> !input.contains("TradeId")))
                .apply("ParseCSV",
                        ParDo.of(CsvRecordParser.<TradeIn>builder().typeParameterClass(TradeIn.class).build())
                )
                .apply("FilterSales",
                        Filter.by(trade -> StringUtils.equals("S",trade.getTradeType())))
                .apply(ToString.elements())
                .apply("WriteRecords", TextIO.write().withoutSharding().to(options.getOutputFile()));

        p.run().waitUntilFinish();
    }
}
