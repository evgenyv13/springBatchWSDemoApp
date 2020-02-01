package com.evgenyv13.demoapp.batch.statistic.processor;

import com.evgenyv13.demoapp.batch.statistic.model.TemporaryStatisticsProcessorDto;
import com.evgenyv13.demoapp.batch.statistic.model.OutputItemStatisticalDto;
import com.evgenyv13.demoapp.batch.statistic.model.InputStatisticalDataRowDto;
import com.evgenyv13.demoapp.batch.statistic.model.ResponseDataDto;
import lombok.Data;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Data
public class StatisticProcessor implements ItemProcessor<InputStatisticalDataRowDto, OutputItemStatisticalDto>, StepExecutionListener, InitializingBean {

    private Validator validator;

    @Value("${validation.filter}")
    private Boolean filter = false;

    public StatisticProcessor(Validator validator) {
        this.validator = validator;
    }

    private Map<String, TreeSet<TemporaryStatisticsProcessorDto>> aggregator;
    private ArrayList<String> errorLines;

    @Override
    public OutputItemStatisticalDto process(InputStatisticalDataRowDto inputStatisticalDataRowDto) {
        try {
            validator.validate(inputStatisticalDataRowDto);
            processInput(inputStatisticalDataRowDto);
        } catch (ValidationException e) {
            if (filter) {
                errorLines.add(Objects.toString(inputStatisticalDataRowDto));
                return null; // filter the item
            } else {
                throw e; // skip the item
            }
        }
        return null;
    }

    private void processInput(InputStatisticalDataRowDto inputStatisticalDataRowDto) {
        TemporaryStatisticsProcessorDto temporaryStatisticsProcessorDto = new TemporaryStatisticsProcessorDto(inputStatisticalDataRowDto);

        if (aggregator.containsKey(temporaryStatisticsProcessorDto.getClassNameMethodName())) {
            aggregator.get(temporaryStatisticsProcessorDto.getClassNameMethodName()).add(temporaryStatisticsProcessorDto);
        } else {
            TreeSet<TemporaryStatisticsProcessorDto> arrayList = new TreeSet<>();
            arrayList.add(temporaryStatisticsProcessorDto);
            aggregator.put(temporaryStatisticsProcessorDto.getClassNameMethodName(), arrayList);
        }

    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        aggregator = new HashMap<>();
        errorLines = new ArrayList<>();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        Map<String, OutputItemStatisticalDto> resultStats = new HashMap<>();

        aggregator.forEach((s, methodStatsOfExecutionsTreeSet) -> {

            OutputItemStatisticalDto outputItemStatisticalDto = new OutputItemStatisticalDto();
            outputItemStatisticalDto.setMaxTime(methodStatsOfExecutionsTreeSet.last().getExecutionTime());
            outputItemStatisticalDto.setMinTime(methodStatsOfExecutionsTreeSet.first().getExecutionTime());

            ArrayList<TemporaryStatisticsProcessorDto> arrayList = new ArrayList<>(methodStatsOfExecutionsTreeSet);
            int treeSetSize = methodStatsOfExecutionsTreeSet.size();
            int medianElementIndex = treeSetSize / 2;
            BigDecimal timeSum = BigDecimal.ZERO;

            outputItemStatisticalDto.setMedina(arrayList.get(medianElementIndex).getExecutionTime());

            for (TemporaryStatisticsProcessorDto temporaryStatisticsProcessorDto : arrayList) {
                timeSum = timeSum.add(BigDecimal.valueOf(temporaryStatisticsProcessorDto.getExecutionTime()));
            }
            outputItemStatisticalDto.setAvgTime(timeSum.divide(BigDecimal.valueOf(treeSetSize), 0, RoundingMode.CEILING));

            resultStats.put(s, outputItemStatisticalDto);
        });


        stepExecution.getJobExecution().getExecutionContext().put("stats", new ResponseDataDto(resultStats,errorLines));

        return ExitStatus.COMPLETED;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(validator, "Validator must not be null.");
    }
}
