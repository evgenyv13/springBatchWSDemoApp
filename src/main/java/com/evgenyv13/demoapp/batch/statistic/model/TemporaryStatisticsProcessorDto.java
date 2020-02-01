package com.evgenyv13.demoapp.batch.statistic.model;

import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Data
public class TemporaryStatisticsProcessorDto implements Comparable<TemporaryStatisticsProcessorDto> {
    private final String classNameMethodName;
    private final Long executionTime;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TemporaryStatisticsProcessorDto(InputStatisticalDataRowDto inputStatisticalDataRowDto) {
        this.classNameMethodName = inputStatisticalDataRowDto.getClassName() + "#" + inputStatisticalDataRowDto.getMethodName();

        this.executionTime = ChronoUnit.SECONDS.between(inputStatisticalDataRowDto.getStartTime(), inputStatisticalDataRowDto.getEndTime());

    }

    @Override
    public int compareTo(TemporaryStatisticsProcessorDto o) {
        return executionTime.compareTo(o.executionTime);
    }
}
