package com.beam.examples.transforms;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MilestoneCounter {
    public static <T> PTransform<PCollection<T>,PCollection<KV<String, Long>>> of(String milestone) {
        return new PTransform<PCollection<T>, PCollection<KV<String, Long>>>() {
            @Override
            public PCollection<KV<String, Long>> expand(PCollection<T> input) {
                return input.apply(Count.globally()).apply(WithKeys.of(milestone));
            }
        };
    }
}
