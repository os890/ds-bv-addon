/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.os890.bv.addon.label.test.uc.uc003.module2;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.os890.bv.addon.label.test.infrastructure.BaseTestForLibIndependentOfDeltaSpike;
import org.os890.bv.addon.label.test.uc.UseCase;
import org.os890.bv.addon.label.test.uc.uc003.TypedViolationModuleCase;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;

@RunWith(CdiTestRunner.class)
@UseCase(TypedViolationModuleCase.class)
public class TypedViolationModule2Test extends BaseTestForLibIndependentOfDeltaSpike {
    @Inject
    private Validator validator;

    @Test
    public void validate() {
        Set<ConstraintViolation<TestPerson2>> violations = validator.validate(new TestPerson2("g", "p"));
        Assert.assertThat(violations.size(), is(1));

        Assert.assertThat(violations.iterator().next().getMessage(), is("[2] The length of 'Surname' should be between 2 and " + Integer.MAX_VALUE));
    }
}
