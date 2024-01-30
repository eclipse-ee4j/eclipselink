/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Creates JAXB context.
 * Context factory supporting Java SE service loading facilities.
 *
 * Redirects to {@link JAXBContextFactory}, which is not removed due to compatibility reasons.
 */
public class XMLBindingContextFactory implements jakarta.xml.bind.JAXBContextFactory {

    public XMLBindingContextFactory() {
        //no-op
    }

    @Override
    public JAXBContext createContext(Class<?>[] types, Map<String, ?> map) throws JAXBException {
        @SuppressWarnings("unchecked")
        Map<String, Object> opts = map != null ? (Map<String, Object>) map : Collections.emptyMap();
        Object factory = opts.getOrDefault(JAXBContextProperties.MOXY_FACTORY, JAXBContextProperties.Factory.DEFAULT);
        if (factory instanceof String) {
            return switch ((String) factory) {
                case JAXBContextProperties.Factory.DEFAULT -> JAXBContextFactory.createContext(types, opts);
                case JAXBContextProperties.Factory.DYNAMIC -> DynamicJAXBContextFactory.createContext(types, opts);
                default -> throw new JAXBException(ExceptionLocalization.buildMessage("jaxb_context_factory_property_invalid", new Object[]{factory}));
            };
        }
        throw new JAXBException(ExceptionLocalization.buildMessage("jaxb_context_factory_property_invalid", new Object[] {factory}));
    }

    @Override
    public JAXBContext createContext(String string, ClassLoader cl, Map<String, ?> map) throws JAXBException {
        @SuppressWarnings("unchecked")
        Map<String, Object> opts = map != null ? (Map<String, Object>) map : Collections.emptyMap();
        Object factory = opts.getOrDefault(JAXBContextProperties.MOXY_FACTORY, JAXBContextProperties.Factory.DEFAULT);
        if (factory instanceof String) {
            return switch ((String) factory) {
                case JAXBContextProperties.Factory.DEFAULT -> JAXBContextFactory.createContext(string, cl, opts);
                case JAXBContextProperties.Factory.DYNAMIC -> DynamicJAXBContextFactory.createContext(string, cl, opts);
                default -> throw new JAXBException(ExceptionLocalization.buildMessage("jaxb_context_factory_property_invalid", new Object[]{factory}));
            };
        }
        throw new JAXBException(ExceptionLocalization.buildMessage("jaxb_context_factory_property_invalid", new Object[] {factory}));
    }

}
