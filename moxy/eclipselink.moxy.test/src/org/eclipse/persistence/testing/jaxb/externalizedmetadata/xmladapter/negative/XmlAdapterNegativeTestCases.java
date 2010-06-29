/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     bdoughan - 2010/06/29 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.negative;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class XmlAdapterNegativeTestCases extends TestCase {

    public XmlAdapterNegativeTestCases(String name) {
        super(name);
    }

    public void testNegativeXmlJavaTypeAdapter() throws Exception {
        Map<String, Source> metadata = new HashMap<String,Source>();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/negative/eclipselink-oxm.xml");
        metadata.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.negative", new StreamSource(stream));

        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadata);

        Class<?>[] classes = new Class[1];
        classes[0] = Customer.class;
        JAXBContext jaxbContext = JAXBContextFactory.createContext(classes, properties);

        InputStream xml = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/negative/customer.xml");
        Customer testCustomer = (Customer) jaxbContext.createUnmarshaller().unmarshal(xml);
        assertEquals(getControlCustomer(), testCustomer);
    }

    private Customer getControlCustomer() {
        Customer leafCustomer = new Customer();
        leafCustomer.setId(456);

        Customer rootCustomer = new Customer();
        rootCustomer.setId(123);
        rootCustomer.setCustomer(leafCustomer);

        return rootCustomer; 
    }

}