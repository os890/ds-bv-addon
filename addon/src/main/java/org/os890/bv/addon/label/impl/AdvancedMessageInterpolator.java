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

import org.os890.bv.addon.label.spi.LabelResolver;
import org.os890.bv.addon.label.spi.MessageTemplateResolver;
import org.os890.bv.addon.label.spi.MessageSourceAdapter;

import javax.validation.MessageInterpolator;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;
import java.util.*;

public class AdvancedMessageInterpolator implements MessageInterpolator, Serializable {
    private static final String MESSAGE_ATTRIBUTE_NAME = "message";
    private static final String EXPRESSION_START = "{";
    private static final String EXPRESSION_END = "}";
    private static final String DELEGATE_EXPRESSION = "{}";

    private final MessageInterpolator wrappedMessageInterpolator;

    public AdvancedMessageInterpolator() {
        wrappedMessageInterpolator = null;
    }

    public AdvancedMessageInterpolator(MessageInterpolator wrappedMessageInterpolator) { //allows manual wrapping
        this.wrappedMessageInterpolator = wrappedMessageInterpolator;
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return interpolate(messageTemplate, context, Locale.getDefault());
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        MessageInterpolator defaultMessageInterpolator = wrappedMessageInterpolator;
        if (defaultMessageInterpolator == null) {
            defaultMessageInterpolator = BeanProvider.getContextualReference(MessageInterpolatorFactory.class, false).getDefaultMessageInterpolator();
        }

        if (DELEGATE_EXPRESSION.equals(messageTemplate)) {
            List<MessageTemplateResolver> messageTemplateResolvers = BeanProvider.getContextualReferences(MessageTemplateResolver.class, true);

            for (MessageTemplateResolver messageTemplateResolver : messageTemplateResolvers) {
                String resolvedMessageTemplate = messageTemplateResolver.resolveMessageTemplateFor(context.getConstraintDescriptor());

                if (resolvedMessageTemplate != null) {
                    messageTemplate = resolvedMessageTemplate;
                }
            }
        }

        List<MessageSourceAdapter> messageSourceAdapters = BeanProvider.getContextualReferences(MessageSourceAdapter.class, true);

        String result = interpolateText(messageTemplate, context, locale, defaultMessageInterpolator, messageSourceAdapters);

        if (result == null) {
            return messageTemplate;
        }

        if (result.contains(EXPRESSION_START) && result.contains(EXPRESSION_END)) {
            return interpolateAdditionalSyntax(result, context, locale, defaultMessageInterpolator, messageSourceAdapters);
        }
        return result;
    }

    private String interpolateAdditionalSyntax(String textToInterpolate, Context context, Locale locale, MessageInterpolator messageInterpolator, List<MessageSourceAdapter> messageSourceAdapters) {
        String result = textToInterpolate;
        ConstraintDescriptor<?> constraintDescriptor = context.getConstraintDescriptor();

        List<LabelResolver> labelResolvers = BeanProvider.getContextualReferences(LabelResolver.class, true);

        Set<String> labels = new HashSet<>();
        for (LabelResolver labelResolver : labelResolvers) {
            labels.addAll(labelResolver.resolveLabelsFor(constraintDescriptor));
        }

        for (String label : labels) {
            String resolvedLabel = messageInterpolator.interpolate(label, context);

            if (resolvedLabel != null && resolvedLabel.startsWith(EXPRESSION_START) && resolvedLabel.endsWith(EXPRESSION_END)) {
                String key = resolvedLabel.substring(1, resolvedLabel.length() - 1);
                for (MessageSourceAdapter messageSourceAdapter : messageSourceAdapters) {
                    resolvedLabel = messageSourceAdapter.resolveMessage(key, locale);

                    if (resolvedLabel != null && !label.equals(resolvedLabel)) {
                        break;
                    }
                }
            }

            if (resolvedLabel != null && !label.equals(resolvedLabel)) {
                result = result.replace(label, resolvedLabel);
            }
        }

        if (result.contains(EXPRESSION_START) && result.contains(EXPRESSION_END)) {
            return interpolateAdditionalStringSyntax(textToInterpolate, context, locale, messageInterpolator, messageSourceAdapters);
        }
        return result;
    }

    private String interpolateAdditionalStringSyntax(String textToInterpolate, Context context, Locale locale, MessageInterpolator messageInterpolator, List<MessageSourceAdapter> messageSourceAdapters) {
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

                String interpolatedSubtext = interpolateText(subMessageKey, context, locale, messageInterpolator, messageSourceAdapters);
                if (!subMessageKey.equals(interpolatedSubtext)) {
                    result = result.replace(attributeValueAsString, interpolatedSubtext);
                }
            }
        }
        return result;
    }

    private String interpolateText(String messageTemplate, Context context, Locale locale, MessageInterpolator defaultMessageInterpolator, List<MessageSourceAdapter> messageSourceAdapters) {
        String result = defaultMessageInterpolator.interpolate(messageTemplate, context, locale);

        if (messageTemplate.equals(result) && messageTemplate.startsWith(EXPRESSION_START) && messageTemplate.endsWith(EXPRESSION_END)) {
            for (MessageSourceAdapter messageResolver : messageSourceAdapters) {
                result = messageResolver.resolveMessage(messageTemplate.substring(1, messageTemplate.length() - 1), locale);

                if (result == null) {
                    continue;
                }

                if (result.contains(EXPRESSION_START) && result.contains(EXPRESSION_END)) {
                    result = defaultMessageInterpolator.interpolate(result, context, locale); //do the default interpolation/replacement
                }

                if (!messageTemplate.equals(result)) {
                    break;
                }
            }
        }
        return result;
    }

    private boolean isSubexpressionToProcess(String textToInterpolate, String key, String value) {
        return !key.equals(MESSAGE_ATTRIBUTE_NAME) && value.startsWith(EXPRESSION_START) && value.endsWith(EXPRESSION_END) &&
            textToInterpolate.contains(value);
    }
}