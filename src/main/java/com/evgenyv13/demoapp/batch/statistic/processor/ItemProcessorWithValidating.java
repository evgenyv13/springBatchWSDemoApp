package com.evgenyv13.demoapp.batch.statistic.processor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ItemProcessorWithValidating<Input, Output> implements ItemProcessor<Input, Output>, InitializingBean {

    @Autowired
    private Validator<Input> validator;

    @Value("${validation.filter}")
    @Getter
    @Setter
    protected Boolean filter = false;

    protected List<String> errorLines = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(validator, "Validator must not be null.");
    }

    @Override
    public Output process(Input input) {
        try {
            validator.validate(input);
            processInput(input);
        } catch (ValidationException e) {
            if (filter) {
                errorLines.add(Objects.toString(input));
                return null; // skip item
            } else {
                throw e;
            }
        }
        return null;
    }

    public abstract void processInput(Input input);
}
