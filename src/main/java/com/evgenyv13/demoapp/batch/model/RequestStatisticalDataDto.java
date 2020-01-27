package com.evgenyv13.demoapp.batch.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class RequestStatisticalDataDto {

    @Size(min = 2, max = 200)
    @NotEmpty
    private String className;
    @Size(min = 2, max = 200)
    @NotEmpty
    private String methodName;

    @Past
    private LocalDateTime startTime;
    @Past
    private LocalDateTime endTime; /*todo validation date range*/

    @NotEmpty
    @Size(min = 2, max = 200)
    private String userThatRun;

}