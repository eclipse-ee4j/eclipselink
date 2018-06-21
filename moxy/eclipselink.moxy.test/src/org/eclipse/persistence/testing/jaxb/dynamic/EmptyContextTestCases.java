/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;

import junit.framework.TestCase;

public class EmptyContextTestCases extends TestCase {

    public EmptyContextTestCases(String name) {
        super(name);
    }

    public void testEmptyContext() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new TestMetadataSource());
        DynamicJAXBContextFactory.createContextFromOXM(null, properties);
    }

    private static class TestMetadataSource implements MetadataSource {

        public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
            XmlBindings xmlBindings = new XmlBindings();
            xmlBindings.setPackageName("test");
            JavaTypes javaTypes = new JavaTypes();
            xmlBindings.setJavaTypes(javaTypes);
            return xmlBindings;
        }

    }

}
