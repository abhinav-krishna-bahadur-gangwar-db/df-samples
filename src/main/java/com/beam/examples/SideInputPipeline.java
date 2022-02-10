package com.beam.examples;

import com.beam.examples.model.Trade;
import com.beam.examples.model.TradeIn;
import com.beam.examples.options.SideInputOptions;
import com.beam.examples.transforms.CsvRecordParser;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class SideInputPipeline {
    public static void main(String[] args){
        // Register options for help menu integration
        PipelineOptionsFactory.register(SideInputOptions.class);
        SideInputOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(SideInputOptions.class);

        runPipeline(options);
    }

    private static void runPipeline(SideInputOptions options) {
        Pipeline p = Pipeline.create(options);
        //Load Rates as View
        PCollectionView<Map<String, BigDecimal>> ratesMapView = p.apply("ReadRates", TextIO.read().from(options.getCurrencyFile()))
                .apply(MapElements.via(new SimpleFunction<String, KV<String, BigDecimal>>() {
                    @Override
                    public KV<String, BigDecimal> apply(String line) {
                        String[] split = line.split("=");
                        return KV.of(split[0], new BigDecimal(split[1]));
                    }
                }))
                .apply(View.asMap());

        p.apply("ReadLines", TextIO.read().from(options.getInputTradeFile()))
                .apply("FilterHeader", Filter.by(input -> !input.contains("TradeId")))
                .apply("ParseCSV",
                        ParDo.of(CsvRecordParser.<TradeIn>builder().typeParameterClass(TradeIn.class).build())
                )
                .apply("FilterSales",
                        Filter.by(trade -> StringUtils.equals("S",trade.getTradeType())))
                .apply(ParDo.of(new DoFn<TradeIn, Trade>() {
                    @ProcessElement
                    public void processElement(ProcessContext c){
                        TradeIn trade = c.element();
                        Map<String, BigDecimal> rates = c.sideInput(ratesMapView);
                        if(Optional.of(trade).map(TradeIn::getCurrency).map(rates::containsKey).orElse(Boolean.FALSE)){
                            c.output(Trade.builder()
                                    .tradeId(trade.getTradeId())
                                    .price(
                                            rates.get(trade.getCurrency())
                                            .multiply(new BigDecimal(trade.getUnits()))
                                    )
                                    .build()
                            );
                        }
                    }
                }).withSideInputs(ratesMapView))
                .apply(ToString.elements())
                .apply("WriteRecords", TextIO.write().withoutSharding().to(options.getOutputFile()));

        p.run().waitUntilFinish();
    }
}
