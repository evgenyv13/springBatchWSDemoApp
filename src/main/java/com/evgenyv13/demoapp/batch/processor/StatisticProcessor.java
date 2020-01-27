package com.evgenyv13.demoapp.batch.processor;

import com.evgenyv13.demoapp.batch.model.MethodStatsOfExecution;
import com.evgenyv13.demoapp.batch.model.OutputStatisticsDto;
import com.evgenyv13.demoapp.batch.model.RequestStatisticalDataDto;
import com.evgenyv13.demoapp.batch.model.ResponseDataDto;
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
public class StatisticProcessor implements ItemProcessor<RequestStatisticalDataDto, OutputStatisticsDto>, StepExecutionListener, InitializingBean {

    private Validator validator;

    @Value("${validation.filter}")
    private Boolean filter = false;

    public StatisticProcessor(Validator validator) {
        this.validator = validator;
    }

    private Map<String, TreeSet<MethodStatsOfExecution>> aggregator;
    private ArrayList<String> errorLines;

    @Override
    public OutputStatisticsDto process(RequestStatisticalDataDto requestStatisticalDataDto) {
        try {
            validator.validate(requestStatisticalDataDto);
            processInput(requestStatisticalDataDto);
        } catch (ValidationException e) {
            if (filter) {
                errorLines.add(Objects.toString(requestStatisticalDataDto));
                return null; // filter the item
            } else {
                throw e; // skip the item
            }
        }
        return null;
    }

    private void processInput(RequestStatisticalDataDto requestStatisticalDataDto) {
        MethodStatsOfExecution methodStatsOfExecution = new MethodStatsOfExecution(requestStatisticalDataDto);

        if (aggregator.containsKey(methodStatsOfExecution.getClassNameMethodName())) {
            aggregator.get(methodStatsOfExecution.getClassNameMethodName()).add(methodStatsOfExecution);
        } else {
            TreeSet<MethodStatsOfExecution> arrayList = new TreeSet<>();
            arrayList.add(methodStatsOfExecution);
            aggregator.put(methodStatsOfExecution.getClassNameMethodName(), arrayList);
        }

    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        aggregator = new HashMap<>();
        errorLines = new ArrayList<>();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        Map<String, OutputStatisticsDto> resultStats = new HashMap<>();

        aggregator.forEach((s, methodStatsOfExecutionsTreeSet) -> {

            OutputStatisticsDto outputStatisticsDto = new OutputStatisticsDto();
            outputStatisticsDto.setMaxTime(methodStatsOfExecutionsTreeSet.last().getExecutionTime());
            outputStatisticsDto.setMinTime(methodStatsOfExecutionsTreeSet.first().getExecutionTime());

            ArrayList<MethodStatsOfExecution> arrayList = new ArrayList<>(methodStatsOfExecutionsTreeSet);
            int treeSetSize = methodStatsOfExecutionsTreeSet.size();
            int medianElementIndex = treeSetSize / 2;
            BigDecimal timeSum = BigDecimal.ZERO;

            outputStatisticsDto.setMedina(arrayList.get(medianElementIndex).getExecutionTime());

            for (MethodStatsOfExecution methodStatsOfExecution : arrayList) {
                timeSum = timeSum.add(BigDecimal.valueOf(methodStatsOfExecution.getExecutionTime()));
            }
            outputStatisticsDto.setAvgTime(timeSum.divide(BigDecimal.valueOf(treeSetSize), 0, RoundingMode.CEILING));

            resultStats.put(s, outputStatisticsDto);
        });


        stepExecution.getJobExecution().getExecutionContext().put("stats", new ResponseDataDto(resultStats,errorLines));

        return ExitStatus.COMPLETED;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(validator, "Validator must not be null.");
    }
}
