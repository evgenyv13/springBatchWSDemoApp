package com.evgenyv13.demoapp.batch.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OutputStatisticsDto implements Serializable {

    private Long minTime;
    private Long maxTime;
    private Long avgTime;
    private Long medina;

    public void setAvgTime(BigDecimal avgTime) {
        this.avgTime = avgTime.longValue();
    }
}
