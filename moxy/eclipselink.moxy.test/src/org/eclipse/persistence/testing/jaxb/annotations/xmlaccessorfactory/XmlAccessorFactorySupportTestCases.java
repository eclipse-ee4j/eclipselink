/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 25 October 2012 - 2.4.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;

import junit.framework.TestCase;

public class XmlAccessorFactorySupportTestCases extends TestCase {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlaccessorfactory/customer-class.xml";

    public XmlAccessorFactorySupportTestCases(String name) throws Exception {
        super(name);
    }

    public CustomerClassOverride getControlObject() {
        CustomerClassOverride cust = new CustomerClassOverride();
        cust.fieldProperty = "fieldPropertyValue";
        cust.setProperty("propertyValue");

        return cust;
    }

    public void testXmlAccessorFactorySupport() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.XML_ACCESSOR_FACTORY_SUPPORT, true);

        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { CustomerClassOverride.class }, properties);

        InputStream is = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        CustomerClassOverride cust = (CustomerClassOverride) ctx.createUnmarshaller().unmarshal(is);

        CustomerClassOverride control = getControlObject();

        assertEquals(control.fieldProperty, cust.fieldProperty);
    }

    public void testXmlAccessorFactorySupportRI() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("com.sun.xml.bind.XmlAccessorFactory", true);

        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { CustomerClassOverride.class }, properties);

        InputStream is = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        CustomerClassOverride cust = (CustomerClassOverride) ctx.createUnmarshaller().unmarshal(is);

        CustomerClassOverride control = getControlObject();

        assertEquals(control.fieldProperty, cust.fieldProperty);
    }

    public void testXmlAccessorFactorySupportNotSet() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        // properties.put("com.sun.xml.bind.XmlAccessorFactory", true);
        // properties.put(JAXBContextProperties.XML_ACCESSOR_FACTORY_SUPPORT, true);

        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { CustomerClassOverride.class }, properties);

        InputStream is = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        CustomerClassOverride cust = (CustomerClassOverride) ctx.createUnmarshaller().unmarshal(is);

        CustomerClassOverride control = getControlObject();

        if (control.fieldProperty.equals(cust.fieldProperty)) {
            // fieldProperties SHOULD be different, because XmlAccessorFactory should not have been used
            fail("XmlAccessoryFactory was used even though JAXBContextProperties.XML_ACCESSOR_FACTORY_SUPPORT was not set.");
        }
    }

}
