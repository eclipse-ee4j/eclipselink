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

    @Override
    public JAXBContext createContext(Class<?>[] types, Map<String, ?> map) throws JAXBException {
        Object value = map != null ? map.get(JAXBContextProperties.MOXY_FACTORY) : null;
        // Property vas not set, use default factory
        if (value == null) {
            return JAXBContextFactory.createContext(types, map);
        }
        // Handle valid String properties
        if (String.class.isInstance(value)) {
            switch ((String) value) {
                case JAXBContextProperties.Factory.DEFAULT:
                    return JAXBContextFactory.createContext(types, map);
                case JAXBContextProperties.Factory.DYNAMIC:
                    return DynamicJAXBContextFactory.createContext(types, (Map<String, Object>) map);
                default:
                    throw new JAXBException(String.format("Property eclipselink.moxy.factory value \"%s\" is invalid", value));
            }
        // Non String values are invalid
        } else {
            throw new JAXBException(String.format("Property eclipselink.moxy.factory value \"%s\" is invalid", value.toString()));
        }
    }

    @Override
    public JAXBContext createContext(String string, ClassLoader cl, Map<String, ?> map) throws JAXBException {
        Object value = map != null ? map.get(JAXBContextProperties.MOXY_FACTORY) : null;
        // Property vas not set, use default factory
        if (value == null) {
            return JAXBContextFactory.createContext(string, cl, map);
        }
        // Handle valid String properties
        if (String.class.isInstance(value)) {
            switch ((String) value) {
                case JAXBContextProperties.Factory.DEFAULT:
                    return JAXBContextFactory.createContext(string, cl, map);
                case JAXBContextProperties.Factory.DYNAMIC:
                    return DynamicJAXBContextFactory.createContext(string, cl, (Map<String, Object>) map);
                default:
                    throw new JAXBException(String.format("Property eclipselink.moxy.factory value \"%s\" is invalid", value));
            }
        // Non String values are invalid
        } else {
            throw new JAXBException(String.format("Property eclipselink.moxy.factory value \"%s\" is invalid", value.toString()));
        }
    }

}
