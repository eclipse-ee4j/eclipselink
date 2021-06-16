/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     bdoughan - 2010/06/29 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.negative;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class XmlAdapterNegativeTestCases extends TestCase {

    public XmlAdapterNegativeTestCases(String name) {
        super(name);
    }

    public void testNegativePackageLevelXmlAdapter() throws Exception {
        Map<String, Source> metadata = new HashMap<String,Source>();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/negative/eclipselink-oxm.xml");
        metadata.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.negative", new StreamSource(stream));

        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadata);

        Class<?>[] classes = new Class[1];
        classes[0] = Customer.class;
        try {
            JAXBContextFactory.createContext(classes, properties);
        } catch (JAXBException jaxbex) {
            //jaxbex.printStackTrace();
            return;
        }
        fail("Invalid package level xml-java-type-adapter value should have caused an exception, but no exception was thrown.");
    }

    public void testNegativeClassLevelXmlAdapter() throws Exception {
        Map<String, Source> metadata = new HashMap<String,Source>();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/negative/class-adapter-oxm.xml");
        metadata.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.negative", new StreamSource(stream));

        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadata);

        Class<?>[] classes = new Class[1];
        classes[0] = Customer.class;
        try {
            JAXBContextFactory.createContext(classes, properties);
        } catch (JAXBException jaxbex) {
            //jaxbex.printStackTrace();
            return;
        }
        fail("Invalid class level xml-java-type-adapter value should have caused an exception, but no exception was thrown.");
    }

    public void testNegativePropertyLevelXmlAdapter() throws Exception {
        Map<String, Source> metadata = new HashMap<String,Source>();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/negative/property-adapter-oxm.xml");
        metadata.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.negative", new StreamSource(stream));

        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadata);

        Class<?>[] classes = new Class[1];
        classes[0] = Customer.class;
        try {
            JAXBContextFactory.createContext(classes, properties);
        } catch (JAXBException jaxbex) {
            //jaxbex.printStackTrace();
            return;
        }
        fail("Invalid property level xml-java-type-adapter value should have caused an exception, but no exception was thrown.");
    }
}
