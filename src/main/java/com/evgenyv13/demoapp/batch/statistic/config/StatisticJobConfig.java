package com.evgenyv13.demoapp.batch.statistic.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class StatisticJobConfig {

    @Bean
    public Job statisticJob(JobBuilderFactory jobBuilderFactory,
                            Step statisticalStep) {
        return jobBuilderFactory.get("calculating statistic report")
                .incrementer(new RunIdIncrementer())
                .flow(statisticalStep)
                .end()
                .build();
    }

}
