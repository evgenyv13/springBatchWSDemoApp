package com.evgenyv13.demoapp.batch.statistic.processor;

import com.evgenyv13.demoapp.batch.statistic.model.InputStatisticalDataRowDto;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Data
public class TemporaryStatisticsItemDtoInProcessor implements Comparable<TemporaryStatisticsItemDtoInProcessor> {
    private final String classNameMethodName;
    private final Long executionTime;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TemporaryStatisticsItemDtoInProcessor(InputStatisticalDataRowDto inputStatisticalDataRowDto) {
        this.classNameMethodName = inputStatisticalDataRowDto.getClassName() + "#" + inputStatisticalDataRowDto.getMethodName();

        this.executionTime = ChronoUnit.SECONDS.between(inputStatisticalDataRowDto.getStartTime(), inputStatisticalDataRowDto.getEndTime());

    }

    @Override
    public int compareTo(TemporaryStatisticsItemDtoInProcessor o) {
        return executionTime.compareTo(o.executionTime);
    }
}
