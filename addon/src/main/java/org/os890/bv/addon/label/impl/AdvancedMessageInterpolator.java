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

import org.apache.deltaspike.core.api.message.LocaleResolver;
import org.apache.deltaspike.core.api.message.MessageContext;
import org.apache.deltaspike.core.api.message.MessageResolver;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.api.provider.DependentProvider;
import org.os890.bv.addon.label.spi.MessageSourceAdapter;

import javax.enterprise.inject.Vetoed;
import javax.validation.MessageInterpolator;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

@Vetoed
public class AdvancedMessageInterpolator implements MessageInterpolator, Serializable {
    private static final String MESSAGE_ATTRIBUTE_NAME = "message";
    private static final String EXPRESSION_START = "{";
    private static final String EXPRESSION_END = "}";

    private final MessageInterpolator wrappedMessageInterpolator;

    public AdvancedMessageInterpolator() {
        wrappedMessageInterpolator = null;
    }

    public AdvancedMessageInterpolator(MessageInterpolator wrappedMessageInterpolator) { //allows manual wrapping
        this.wrappedMessageInterpolator = wrappedMessageInterpolator;
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return interpolate(messageTemplate, context, null);
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        MessageInterpolator defaultMessageInterpolator = wrappedMessageInterpolator;
        if (defaultMessageInterpolator == null) {
            defaultMessageInterpolator = BeanProvider.getContextualReference(MessageInterpolatorFactory.class).getDefaultMessageInterpolator();
        }

        DependentProvider<MessageContext> messageContextProvider = BeanProvider.getDependent(MessageContext.class);

        try {
            String result = interpolateText(messageTemplate, context, locale, defaultMessageInterpolator, messageContextProvider.get());

            if (result.contains(EXPRESSION_START) && result.contains(EXPRESSION_END)) {
                //TODO we could interpolate the result again - for now it isn't needed
                return interpolateAdditionalSyntax(result, context, locale, defaultMessageInterpolator, messageContextProvider.get());
            }
            return result;
        } finally {
            messageContextProvider.destroy();
        }
    }

    private String interpolateAdditionalSyntax(String textToInterpolate, Context context, Locale locale, MessageInterpolator messageInterpolator, MessageContext messageContext) {
        String result = textToInterpolate;
        ConstraintDescriptor<?> constraintDescriptor = context.getConstraintDescriptor();

        Map<String, Object> annotationAttributes = constraintDescriptor.getAttributes();

        for (Map.Entry<String, Object> entry : annotationAttributes.entrySet()) {
            Object attributeValue = entry.getValue();
            if (!(attributeValue instanceof String)) {
                continue;
            }
            String attributeValueAsString = (String) attributeValue;

            if (isSubexpressionToProcess(textToInterpolate, entry.getKey(), attributeValueAsString)) {
                String subMessageKey = (String) entry.getValue();

                String interpolatedSubtext = interpolateText(subMessageKey, context, locale, messageInterpolator, messageContext);
                if (!subMessageKey.equals(interpolatedSubtext)) {
                    result = result.replace(attributeValueAsString, interpolatedSubtext);
                }
            }
        }
        return result;
    }

    private String interpolateText(String messageTemplate, Context context, final Locale locale, MessageInterpolator defaultMessageInterpolator, MessageContext messageContext) {
        String result = defaultMessageInterpolator.interpolate(messageTemplate, context, locale != null ? locale : messageContext.getLocale()); //always delegate first - simple bv-message can be resolved already

        if (messageTemplate.equals(result) && messageTemplate.startsWith(EXPRESSION_START) && messageTemplate.endsWith(EXPRESSION_END)) {
            if (locale != null) {
                messageContext.localeResolver((LocaleResolver) () -> locale);
            }
            messageContext.messageInterpolator((org.apache.deltaspike.core.api.message.MessageInterpolator) (messageText, arguments, currentLocal) -> defaultMessageInterpolator.interpolate(messageText, context, currentLocal));
            final MessageResolver defaultMessageResolver = messageContext.getMessageResolver();

            messageContext.messageResolver((MessageResolver) (currentMessageContext, currentMessageTemplate, category) -> {
                for (MessageSourceAdapter messageSourceAdapter : BeanProvider.getContextualReferences(MessageSourceAdapter.class, true)) {
                    String resolvedMessage = messageSourceAdapter.resolveMessage(currentMessageTemplate, currentMessageContext.getLocale());

                    if (!currentMessageTemplate.equals(resolvedMessage)) {
                        return resolvedMessage;
                    }
                }
                return defaultMessageResolver.getMessage(messageContext, messageTemplate, null);
            });
            result = messageContext.message().template(messageTemplate).toString();
        }
        return result;
    }

    private boolean isSubexpressionToProcess(String textToInterpolate, String key, String value) {
        return !key.equals(MESSAGE_ATTRIBUTE_NAME) && value.startsWith(EXPRESSION_START) && value.endsWith(EXPRESSION_END) &&
            textToInterpolate.contains(value);
    }
}