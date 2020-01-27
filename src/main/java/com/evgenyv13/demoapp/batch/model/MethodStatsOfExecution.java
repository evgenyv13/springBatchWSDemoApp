package com.evgenyv13.demoapp.batch.model;

import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Data
public class MethodStatsOfExecution implements Comparable<MethodStatsOfExecution> {
    private final String classNameMethodName;
    private final Long executionTime;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MethodStatsOfExecution(RequestStatisticalDataDto requestStatisticalDataDto) {
        this.classNameMethodName = requestStatisticalDataDto.getClassName() + "#" + requestStatisticalDataDto.getMethodName();

        this.executionTime = ChronoUnit.SECONDS.between(requestStatisticalDataDto.getStartTime(), requestStatisticalDataDto.getEndTime());

    }

    @Override
    public int compareTo(MethodStatsOfExecution o) {
        return executionTime.compareTo(o.executionTime);
    }
}
