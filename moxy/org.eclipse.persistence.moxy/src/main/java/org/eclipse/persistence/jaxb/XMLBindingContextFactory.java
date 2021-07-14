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
import java.util.Map;

/**
 * Creates JAXB context.
 * Context factory supporting Java SE service loading facilities.
 *
 * Redirects to {@link JAXBContextFactory}, which is not removed due to compatibility reasons.
 */public class XMLBindingContextFactory implements jakarta.xml.bind.JAXBContextFactory {

    public XMLBindingContextFactory() {
        //no-op
    }

    @Override
    public JAXBContext createContext(Class<?>[] types, Map<String, ?> map) throws JAXBException {
        return JAXBContextFactory.createContext(types, map);
    }

    @Override
    public JAXBContext createContext(String string, ClassLoader cl, Map<String, ?> map) throws JAXBException {
        return JAXBContextFactory.createContext(string, cl, map);
    }
    
}
