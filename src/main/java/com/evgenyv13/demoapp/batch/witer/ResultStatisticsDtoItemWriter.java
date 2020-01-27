package com.evgenyv13.demoapp.batch.witer;

import com.evgenyv13.demoapp.batch.model.OutputStatisticsDto;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class ResultStatisticsDtoItemWriter implements ItemWriter<OutputStatisticsDto> {
    @Override
    public void write(List<? extends OutputStatisticsDto> list) throws Exception {
    }
}
