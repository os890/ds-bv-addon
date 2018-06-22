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
package org.os890.bv.addon.label.test.infrastructure;

import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.spi.alternative.AlternativeBeanClassProvider;
import org.apache.deltaspike.core.util.ClassUtils;
import org.apache.deltaspike.core.util.ExceptionUtils;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.ClassLoaders;
import org.apache.xbean.finder.archive.ClasspathArchive;

import javax.enterprise.inject.Alternative;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//only needed to show different use-cases in an isolated manner (each test uses its own alternative-beans)

//allows to use type-safe binding-annotations instead of text based config
public class UseCaseAwareBeanClassProvider implements AlternativeBeanClassProvider {
    //just a simple list to test custom bindings
    private static List<Class> alternativeImplementations = new ArrayList<>();

    static {
        try {
            ClassLoader classLoader = UseCaseAwareBeanClassProvider.class.getClassLoader();
            alternativeImplementations.addAll(
                new AnnotationFinder(new ClasspathArchive(classLoader, ClassLoaders.findUrls(classLoader)))
                    .findAnnotatedClasses(Alternative.class));
        } catch (IOException e) {
            throw ExceptionUtils.throwAsRuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getAlternativeMapping() {
        Map<String, String> result = new HashMap<String, String>();
        String activeAlternativeLabelSource = ConfigResolver.getPropertyValue("activeAlternativeLabelSource");

        Class<?> labelSource = ClassUtils.tryToLoadClassForName(activeAlternativeLabelSource);

        Annotation alternativeLabelAnnotation = null;
        for (Annotation annotation : labelSource.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(TestLabelBinding.class)) {
                alternativeLabelAnnotation = annotation;
                break;
            }
        }

        if (alternativeLabelAnnotation != null) {
            for (Class alternativeImplementation : alternativeImplementations) {
                for (Annotation annotation : alternativeImplementation.getAnnotations()) {
                    if (alternativeLabelAnnotation.equals(annotation)) {
                        for (Class interfaceClass : alternativeImplementation.getInterfaces()) {
                            result.put(interfaceClass.getName(), alternativeImplementation.getName());
                        }
                        break;
                    }
                }
            }
        }
        return result;
    }
}
