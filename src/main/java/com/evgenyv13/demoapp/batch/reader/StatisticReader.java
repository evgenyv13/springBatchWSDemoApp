package com.evgenyv13.demoapp.batch.reader;

import com.evgenyv13.demoapp.batch.model.RequestStatisticalDataDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class StatisticReader {

    @Bean
    @StepScope
    FlatFileItemReader<RequestStatisticalDataDto> itemReader(@Value("#{jobParameters['input.file']}") String path,
                                                             LineMapper<RequestStatisticalDataDto> lineMapper) {

        FlatFileItemReader<RequestStatisticalDataDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(path));
        flatFileItemReader.setLineMapper(lineMapper);

        return flatFileItemReader;
    }

    @Bean
    LineMapper<RequestStatisticalDataDto> lineMapper(LineTokenizer lineTokenizer,
                                                     FieldSetMapper<RequestStatisticalDataDto> fieldSetMapper) {
        DefaultLineMapper<RequestStatisticalDataDto> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }

    @Bean
    LineTokenizer lineTokenizer() {
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setNames("className", "methodName", "startTime", "endTime", "userThatRun");
        return delimitedLineTokenizer;
    }


    @Bean
    FieldSetMapper<RequestStatisticalDataDto> fieldSetMapper(ConversionService conversionService) {
        BeanWrapperFieldSetMapper<RequestStatisticalDataDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(RequestStatisticalDataDto.class);
        fieldSetMapper.setConversionService(conversionService);
        return fieldSetMapper;
    }

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        DefaultConversionService.addDefaultConverters(conversionService);
        conversionService.addConverter(new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String text) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(text, formatter);
            }
        });
        return conversionService;
    }

}
