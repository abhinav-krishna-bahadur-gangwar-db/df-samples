package com.beam.examples;

import com.beam.examples.model.Trade;
import com.beam.examples.model.TradeIn;
import com.beam.examples.options.MilestoneCounterOptions;
import com.beam.examples.transforms.CsvRecordParser;
import com.beam.examples.transforms.MilestoneCounter;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class MilestoneCountExample {
    public static void main(String[] args) {
        // Register options for help menu integration
        PipelineOptionsFactory.register(MilestoneCounterOptions.class);
        MilestoneCounterOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(MilestoneCounterOptions.class);

        runPipeline(options);
    }

    private static void runPipeline(MilestoneCounterOptions options) {
        Pipeline p = Pipeline.create(options);
        ValueProvider<String> minPriceLimit = options.getMinPriceLimit();
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

        PCollection<String> acquiredTrades = p.apply("ReadLines", TextIO.read().from(options.getInputTradeFile()))
                .apply("FilterHeader", Filter.by(input -> !input.contains("TradeId")));

        PCollection<TradeIn> eligibleTrades = acquiredTrades
                .apply("ParseCSV",
                    ParDo.of(CsvRecordParser.<TradeIn>builder().typeParameterClass(TradeIn.class).build()))
                .apply("FilterSales",
                    Filter.by(trade -> StringUtils.equals("S", trade.getTradeType())));

        PCollection<Trade> transformedTrades = eligibleTrades
                .apply(ParDo.of(new DoFn<TradeIn, Trade>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        TradeIn trade = c.element();
                        Map<String, BigDecimal> rates = c.sideInput(ratesMapView);
                        if (Optional.of(trade).map(TradeIn::getCurrency).map(rates::containsKey).orElse(Boolean.FALSE)) {
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
                }).withSideInputs(ratesMapView));

        PCollection<Trade> reportedTrades = transformedTrades.apply("HighValueFilter", Filter.by(t ->
                Optional.ofNullable(minPriceLimit.get())
                        .map(BigDecimal::new)
                        .map(limit -> t.getPrice().compareTo(limit) > 0)
                        .orElse(Boolean.TRUE)
        ));
        reportedTrades.apply(ToString.elements())
                .apply("WriteRecords", TextIO.write().withoutSharding().to(options.getOutputFile()));

        PCollection<KV<String, Long>> acquired = acquiredTrades.apply(Count.globally()).apply(WithKeys.of("ACQUIRED"));
        PCollection<KV<String, Long>> eligible = eligibleTrades.apply(Count.globally()).apply(WithKeys.of("ELIGIBLE"));
        PCollection<KV<String, Long>> transformed = transformedTrades.apply(Count.globally()).apply(WithKeys.of("TRANSFORMED"));
        PCollection<KV<String, Long>> reported = reportedTrades.apply(MilestoneCounter.of("REPORTED"));

        PCollectionList.of(acquired).and(eligible).and(transformed).and(reported)
                .apply(Flatten.pCollections())
                .apply(ToString.kvs(":"))
                .apply("WriteMetrics",TextIO.write().withoutSharding().to(options.getMetricsPath()));

        p.run();
    }
}
