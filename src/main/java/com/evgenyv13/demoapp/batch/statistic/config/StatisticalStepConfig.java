package com.evgenyv13.demoapp.batch.statistic.config;

import com.evgenyv13.demoapp.batch.statistic.model.OutputItemStatisticalDto;
import com.evgenyv13.demoapp.batch.statistic.model.InputStatisticalDataRowDto;
import com.evgenyv13.demoapp.batch.statistic.processor.StatisticProcessor;
import com.evgenyv13.demoapp.batch.statistic.witer.ResultStatisticsDtoItemWriter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
public class StatisticalStepConfig {

    private static final String STATISTICALSTEP = "STATISTICALSTEP";

    @Bean(STATISTICALSTEP)
    public Step statisticalStep(StepBuilderFactory stepBuilderFactory,
                                ItemReader<InputStatisticalDataRowDto> statisticalDataDtoItemReader,
                                ItemProcessor<InputStatisticalDataRowDto, OutputItemStatisticalDto> statisticProcessor,
                                ItemWriter<OutputItemStatisticalDto> resultStatisticsDtoItemWriter,
                                @Value("${chunk-size}") int chunkSize) {
        return stepBuilderFactory.get(STATISTICALSTEP)
                .<InputStatisticalDataRowDto, OutputItemStatisticalDto>chunk(chunkSize)
                .reader(statisticalDataDtoItemReader)
                .processor(statisticProcessor)
                .writer(resultStatisticsDtoItemWriter)
                .listener(statisticProcessor)
                .build();
    }

    @Bean
    @StepScope
    public StatisticProcessor statisticProcessor(
            Validator<OutputItemStatisticalDto> springValidator
    ) {
        StatisticProcessor statisticProcessor = new StatisticProcessor
                (springValidator);
        statisticProcessor.setFilter(true);

        return statisticProcessor;
    }

    @Bean
    public ItemWriter<OutputItemStatisticalDto> resultStatisticsDtoItemWriter() {
        return new ResultStatisticsDtoItemWriter();
    }

    @Bean
    public Validator<OutputItemStatisticalDto> springValidator() {
        SpringValidator<OutputItemStatisticalDto> springValidator = new SpringValidator<>();
        springValidator.setValidator(validator());
        return springValidator;
    }

    @Bean
    public org.springframework.validation.Validator validator() {
        return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
    }
}
