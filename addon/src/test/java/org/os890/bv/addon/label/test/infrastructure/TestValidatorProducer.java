package org.os890.bv.addon.label.test.infrastructure;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@ApplicationScoped
public class TestValidatorProducer {
    private ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure().buildValidatorFactory();

    @Produces
    @Dependent
    protected Validator validator() {
        return validatorFactory.getValidator();
    }
}
