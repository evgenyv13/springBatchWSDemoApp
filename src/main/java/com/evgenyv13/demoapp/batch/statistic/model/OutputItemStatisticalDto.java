package com.evgenyv13.demoapp.batch.statistic.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutputItemStatisticalDto implements Serializable {

    private Long minTime;
    private Long maxTime;
    private double avgTime;
    private Long medina;
}
