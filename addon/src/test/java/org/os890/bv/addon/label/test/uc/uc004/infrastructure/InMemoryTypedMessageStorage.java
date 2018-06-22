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
package org.os890.bv.addon.label.test.uc.uc004.infrastructure;

import org.os890.bv.addon.label.test.inmemory.TestMessageStorage;
import org.os890.bv.addon.label.test.uc.UseCase;
import org.os890.bv.addon.label.test.uc.uc004.TypedViolationLabelCase;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import java.util.HashMap;
import java.util.Map;

@Alternative
@UseCase(TypedViolationLabelCase.class)

@ApplicationScoped
public class InMemoryTypedMessageStorage implements TestMessageStorage {
    private Map<String, String> messageCodeToTextMap;

    @PostConstruct
    protected void init() {
        messageCodeToTextMap = new HashMap<>();
        messageCodeToTextMap.put("VIOLATION_01", "The length of '{propertyLabel}' should be between {min} and {max}");

        messageCodeToTextMap.put("firstName", "Firstname");
        messageCodeToTextMap.put("lastName", "Surname");
    }

    public String resolveMessage(String key) {
        return messageCodeToTextMap.get(key);
    }
}
