package com.beam.examples.transforms;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Builder;
import org.apache.beam.sdk.transforms.DoFn;

import java.io.Serializable;
import java.io.StringReader;

@Builder
public class CsvRecordParser<T extends Serializable>  extends DoFn<String, T> {
    final Class<T> typeParameterClass;

    @ProcessElement
    public void processElement(@Element String line, final OutputReceiver<T> receiver){
        new CsvToBeanBuilder<T>(new StringReader(line))
                .withType(typeParameterClass)
                .build()
                .parse()
                .forEach(receiver::output);
    }
}
