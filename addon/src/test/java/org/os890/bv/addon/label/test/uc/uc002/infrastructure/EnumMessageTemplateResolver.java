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
package org.os890.bv.addon.label.test.uc.uc002.infrastructure;

import org.apache.deltaspike.core.util.ExceptionUtils;
import org.os890.bv.addon.label.spi.MessageTemplateResolver;
import org.os890.bv.addon.label.test.uc.UseCase;
import org.os890.bv.addon.label.test.uc.uc002.MessageId;
import org.os890.bv.addon.label.test.uc.uc002.TypedViolationCase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.reflect.Method;

@Alternative
@UseCase(TypedViolationCase.class)

@ApplicationScoped
public class EnumMessageTemplateResolver implements MessageTemplateResolver {
    @Override
    public String resolveMessageTemplateFor(ConstraintDescriptor<?> constraintDescriptor) {
        for (Method annotationMethod : constraintDescriptor.getAnnotation().annotationType().getDeclaredMethods()) {
            if (MessageId.class.isAssignableFrom(annotationMethod.getReturnType())) {
                try {
                    MessageId messageId = (MessageId) annotationMethod.invoke(constraintDescriptor.getAnnotation());
                    return "{" + messageId.getId() + "}";
                } catch (Exception e) {
                    throw ExceptionUtils.throwAsRuntimeException(e);
                }
            }
        }
        return null;
    }
}
