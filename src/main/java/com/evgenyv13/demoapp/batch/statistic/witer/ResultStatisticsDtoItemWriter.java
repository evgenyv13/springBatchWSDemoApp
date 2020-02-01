package com.evgenyv13.demoapp.batch.statistic.witer;

import com.evgenyv13.demoapp.batch.statistic.model.OutputItemStatisticalDto;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class ResultStatisticsDtoItemWriter implements ItemWriter<OutputItemStatisticalDto> {
    @Override
    public void write(List<? extends OutputItemStatisticalDto> list) throws Exception {
    }
}
