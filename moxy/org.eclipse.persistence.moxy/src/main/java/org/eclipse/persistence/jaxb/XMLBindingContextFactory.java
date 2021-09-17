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
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;

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

    @FunctionalInterface
    private interface ContextSupplier {
        JAXBContext get() throws JAXBException;
    }

    // PERF: null check is much faster than instanceof for missing property
    @SuppressWarnings({"SwitchStatementWithTooFewBranches", "ConditionCoveredByFurtherCondition"})
    private static JAXBContext context(final Map<String, ?> map,
                                       final ContextSupplier defaultValue, final ContextSupplier dynamicValue) throws JAXBException {
        Object value = map.get(JAXBContextProperties.MOXY_FACTORY_TYPE);
        if (value == null || !String.class.isInstance(value)) {
            return defaultValue.get();
        }
        // More suppliers may be added here
        switch ((String) value) {
            case JAXBContextProperties.FactoryType.DYNAMIC:
                return dynamicValue.get();
            default:
                return defaultValue.get();
        }
    }

    @Override
    public JAXBContext createContext(Class<?>[] types, Map<String, ?> map) throws JAXBException {
        return context(map,
                () -> JAXBContextFactory.createContext(types, map),
                () -> DynamicJAXBContextFactory.createContext(types, (Map<String, Object>) map)
        );
    }

    @Override
    public JAXBContext createContext(String string, ClassLoader cl, Map<String, ?> map) throws JAXBException {
        return context(map,
                () -> JAXBContextFactory.createContext(string, cl, map),
                () -> DynamicJAXBContextFactory.createContext(string, cl, (Map<String, Object>) map)
        );
    }

}

