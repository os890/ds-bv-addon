package org.os890.bv.addon.label.test.uc.uc001;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.os890.bv.addon.label.test.infrastructure.BaseTestForLibIndependentOfDeltaSpike;
import org.os890.bv.addon.label.test.uc.shared.TestPerson;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;

@RunWith(CdiTestRunner.class)
public class AlternativeMessageSourceTest extends BaseTestForLibIndependentOfDeltaSpike {
    @Inject
    private Validator validator;

    @Test
    public void validate() {
        Set<ConstraintViolation<TestPerson>> violations = validator.validate(new TestPerson("g", "p"));
        Assert.assertThat(violations.size(), is(1));

        Assert.assertThat(violations.iterator().next().getMessage(), is("The length of 'Surname' should be between 2 and " + Integer.MAX_VALUE));
    }
}
