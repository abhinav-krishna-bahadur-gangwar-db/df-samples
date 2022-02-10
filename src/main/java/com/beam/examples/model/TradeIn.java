package com.beam.examples.model;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

import java.io.Serializable;

@Data
public class TradeIn implements Serializable {
    @CsvBindByPosition(position = 0)
    private String tradeId;
    @CsvBindByPosition(position = 1)
    private String tradeType;
    @CsvBindByPosition(position = 2)
    private String currency;
    @CsvBindByPosition(position = 3)
    private String units;
}
