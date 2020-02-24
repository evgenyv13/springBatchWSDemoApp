package com.evgenyv13.demoapp.batch.statistic.processor;

import com.evgenyv13.demoapp.batch.statistic.model.OutputItemStatisticalDto;
import com.evgenyv13.demoapp.batch.statistic.model.InputStatisticalDataRowDto;
import com.evgenyv13.demoapp.batch.statistic.model.ResponseDataDto;
import lombok.Data;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.util.*;

@Data
public class StatisticProcessor extends ItemProcessorWithValidating<InputStatisticalDataRowDto, OutputItemStatisticalDto> implements StepExecutionListener {

    private Map<String, TreeSet<TemporaryStatisticsItemDtoInProcessor>> aggregator;


    public void processInput(InputStatisticalDataRowDto inputStatisticalDataRowDto) {
        TemporaryStatisticsItemDtoInProcessor temporaryStatisticsProcessorDto = new TemporaryStatisticsItemDtoInProcessor(inputStatisticalDataRowDto);

        if (aggregator.containsKey(temporaryStatisticsProcessorDto.getClassNameMethodName())) {
            aggregator.get(temporaryStatisticsProcessorDto.getClassNameMethodName()).add(temporaryStatisticsProcessorDto);
        } else {
            TreeSet<TemporaryStatisticsItemDtoInProcessor> arrayList = new TreeSet<>();
            arrayList.add(temporaryStatisticsProcessorDto);
            aggregator.put(temporaryStatisticsProcessorDto.getClassNameMethodName(), arrayList);
        }

    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        aggregator = new HashMap<>();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        Map<String, OutputItemStatisticalDto> resultStats = new HashMap<>();

        aggregator.forEach((s, methodStatsOfExecutionsTreeSet) -> {

            OutputItemStatisticalDto outputItemStatisticalDto = new OutputItemStatisticalDto();
            outputItemStatisticalDto.setMaxTime(methodStatsOfExecutionsTreeSet.last().getExecutionTime());
            outputItemStatisticalDto.setMinTime(methodStatsOfExecutionsTreeSet.first().getExecutionTime());

            int medianElementIndex = methodStatsOfExecutionsTreeSet.size() / 2;
            Optional<TemporaryStatisticsItemDtoInProcessor> first = methodStatsOfExecutionsTreeSet.stream().skip(medianElementIndex).findFirst();
            outputItemStatisticalDto.setMedina(first.get().getExecutionTime());

            double avgTime = methodStatsOfExecutionsTreeSet.stream()
                    .map(TemporaryStatisticsItemDtoInProcessor::getExecutionTime)
                    .mapToLong(Long::longValue)
                    .average().orElse(0);
            outputItemStatisticalDto.setAvgTime(avgTime);

            resultStats.put(s, outputItemStatisticalDto);
        });

        stepExecution.getJobExecution().getExecutionContext().put("stats", new ResponseDataDto(resultStats, errorLines));

        return ExitStatus.COMPLETED;
    }


}
