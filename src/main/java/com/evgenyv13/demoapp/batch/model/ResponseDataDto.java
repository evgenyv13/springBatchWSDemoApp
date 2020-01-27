package com.evgenyv13.demoapp.batch.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
public class ResponseDataDto implements Serializable {
    private final Map<String, OutputStatisticsDto> resultStats;
    private final List<String> errorLines;
}
