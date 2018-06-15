/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.jaxb.dynamic.metadata;

import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;

public abstract class Metadata {

    protected Map<String, XmlBindings> bindings;
    protected DynamicClassLoader dynamicClassLoader;

    public Metadata(DynamicClassLoader dynamicClassLoader, Map<String, ?> properties) {
        this.dynamicClassLoader = dynamicClassLoader;
        this.bindings = JAXBContextFactory.getXmlBindingsFromProperties(properties, dynamicClassLoader);
    }

    public Map<String, XmlBindings> getBindings() {
        return bindings;
    }

    public DynamicClassLoader getDynamicClassLoader() {
        return dynamicClassLoader;
    }

    public abstract JavaModelInput getJavaModelInput() throws JAXBException;

}
