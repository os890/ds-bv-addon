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
package org.os890.bv.addon.label.test.uc.uc004;

import static org.os890.bv.addon.label.test.uc.uc004.ViolationLabels.FIRST_NAME;
import static org.os890.bv.addon.label.test.uc.uc004.ViolationLabels.LAST_NAME;
import static org.os890.bv.addon.label.test.uc.uc004.ViolationMessages.VIOLATION_01;

public class TestPerson {
    @MySize(min = 1, messageId = VIOLATION_01, propertyLabel = FIRST_NAME)
    private String firstName;

    @MySize(min = 2, messageId = VIOLATION_01, propertyLabel = LAST_NAME)
    private String lastName;

    public TestPerson(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}