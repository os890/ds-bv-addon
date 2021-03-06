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
package org.os890.bv.addon.label.test.inmemory;

import org.os890.bv.addon.label.spi.MessageSourceAdapter;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import java.util.Locale;

//enable it autom. for this module (= tests)
@Alternative
@Priority(Interceptor.Priority.APPLICATION)

@ApplicationScoped //don't use @Dependent
public class TestMessageSourceAdapter implements MessageSourceAdapter {
    @Inject
    private TestMessageStorage messageStorage;

    @Override
    public String resolveMessage(String key, Locale locale) {
        return messageStorage.resolveMessage(key);
    }
}
