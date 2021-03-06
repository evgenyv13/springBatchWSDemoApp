package com.evgenyv13.demoapp.rest.service;

import com.evgenyv13.demoapp.batch.statistic.model.ResponseDataDto;
import com.evgenyv13.demoapp.rest.exception.ReportGenerationExeption;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class BatchService {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job statisticJob;

    public ResponseDataDto runStatsJob(String fileName) throws Exception {

        final ResponseDataDto[] responseDataDto = {null};

        CommandLineRunner commandLineRunner = args -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("input.file", fileName, true)
                    .toJobParameters();
            try {
                ResponseDataDto stats = (ResponseDataDto) jobLauncher.run(statisticJob, jobParameters).getExecutionContext().get("stats");
                responseDataDto[0] = stats;
            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
                new ReportGenerationExeption("Could't generate report");
            }
        };
        commandLineRunner.run("");

        return responseDataDto[0];
    }
}
