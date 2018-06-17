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
package org.os890.bv.addon.label.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.util.*;

//partial copy of org.apache.deltaspike.core.api.provider.BeanProvider
final class BeanProvider {
    private BeanProvider() {
        // this is a utility class which doesn't get instantiated.
    }

    static <T> T getContextualReference(Class<T> type, boolean optional, Annotation... qualifiers) {
        BeanManager beanManager = getBeanManager();

        Set<Bean<?>> beans = getBeanDefinitions(type, optional, beanManager, qualifiers);
        return getContextualReference(type, beanManager, beans);
    }

    static <T> List<T> getContextualReferences(Class<T> type, boolean optional) {
        BeanManager beanManager = getBeanManager();
        Set<Bean<?>> beans = getBeanDefinitions(type, optional, beanManager);

        List<T> result = new ArrayList<>(beans.size());
        for (Bean<?> bean : beans) {
            result.add(getContextualReference(type, beanManager, bean));
        }
        return result;
    }

    private static <T> T getContextualReference(Class<T> type, BeanManager beanManager, Bean<?> bean) {
        return getContextualReference(type, beanManager, Collections.singleton(bean));
    }

    private static <T> Set<Bean<?>> getBeanDefinitions(Class<T> type, boolean optional, BeanManager beanManager, Annotation... qualifiers) {
        if (qualifiers == null || qualifiers.length == 0) {
            qualifiers = new Annotation[] {new AnyLiteral()};
        }

        Set<Bean<?>> beans = beanManager.getBeans(type, qualifiers);

        beans = filterDefaultScopedBeans(beans);

        if (beans == null || beans.isEmpty()) {
            if (optional) {
                return Collections.emptySet();
            }
            throw new IllegalStateException("Could not find beans for Type=" + type);
        }

        return beans;
    }

    //it's just limited to reduce the amount of helpers (copied from deltaspike)
    private static Set<Bean<?>> filterDefaultScopedBeans(Set<Bean<?>> beans) {
        Set<Bean<?>> result = new HashSet<Bean<?>>(beans.size());

        for (Bean<?> currentBean : beans) {
            if (!Dependent.class.isAssignableFrom(currentBean.getScope())) {
                result.add(currentBean);
            }
        }
        return result;
    }

    private static <T> T getContextualReference(Class<T> type, BeanManager beanManager, Set<Bean<?>> beans) {
        Bean<?> bean = beanManager.resolve(beans);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);

        T result = (T) beanManager.getReference(bean, type, creationalContext);
        return result;
    }

    private static BeanManager getBeanManager() {
        return BeanManagerProvider.getInstance().getBeanManager();
    }
}
