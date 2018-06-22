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

import org.apache.deltaspike.core.util.ExceptionUtils;
import org.os890.bv.addon.label.spi.LabelResolver;
import org.os890.bv.addon.label.test.uc.UseCase;
import org.os890.bv.addon.label.test.uc.uc004.LabelId;
import org.os890.bv.addon.label.test.uc.uc004.TypedViolationLabelCase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Alternative
@UseCase(TypedViolationLabelCase.class)

@ApplicationScoped
public class EnumLabelsResolver implements LabelResolver {
    @Override
    public List<String> resolveLabelsFor(ConstraintDescriptor<?> constraintDescriptor) {
        List<String> result = new ArrayList<>();
        for (Method annotationMethod : constraintDescriptor.getAnnotation().annotationType().getDeclaredMethods()) {
            if (LabelId.class.isAssignableFrom(annotationMethod.getReturnType())) {
                try {
                    LabelId labelId = (LabelId) annotationMethod.invoke(constraintDescriptor.getAnnotation());
                    result.add(labelId.getId());
                } catch (Exception e) {
                    throw ExceptionUtils.throwAsRuntimeException(e);
                }
            }
        }
        return result;
    }
}
