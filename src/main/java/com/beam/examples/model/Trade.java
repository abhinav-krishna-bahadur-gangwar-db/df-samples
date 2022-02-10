package com.beam.examples.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
public class Trade implements Serializable {
    private final String tradeId;
    private final BigDecimal price;
}
