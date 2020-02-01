package com.evgenyv13.demoapp.batch.statistic.processor;

import com.evgenyv13.demoapp.batch.statistic.model.OutputItemStatisticalDto;
import com.evgenyv13.demoapp.batch.statistic.model.InputStatisticalDataRowDto;
import com.evgenyv13.demoapp.batch.statistic.model.ResponseDataDto;
import lombok.Data;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Data
public class StatisticProcessor extends ItemProcessorWithValidating<InputStatisticalDataRowDto, OutputItemStatisticalDto> implements StepExecutionListener{

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

            ArrayList<TemporaryStatisticsItemDtoInProcessor> arrayList = new ArrayList<>(methodStatsOfExecutionsTreeSet);
            int treeSetSize = methodStatsOfExecutionsTreeSet.size();
            int medianElementIndex = treeSetSize / 2;
            BigDecimal timeSum = BigDecimal.ZERO;

            outputItemStatisticalDto.setMedina(arrayList.get(medianElementIndex).getExecutionTime());

            for (TemporaryStatisticsItemDtoInProcessor temporaryStatisticsItemDtoInProcessor : arrayList) {
                timeSum = timeSum.add(BigDecimal.valueOf(temporaryStatisticsItemDtoInProcessor.getExecutionTime()));
            }
            outputItemStatisticalDto.setAvgTime(timeSum.divide(BigDecimal.valueOf(treeSetSize), 0, RoundingMode.CEILING));

            resultStats.put(s, outputItemStatisticalDto);
        });


        stepExecution.getJobExecution().getExecutionContext().put("stats", new ResponseDataDto(resultStats,errorLines));

        return ExitStatus.COMPLETED;
    }


}
