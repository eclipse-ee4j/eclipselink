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
//  - rbarkhouse - 9/20/2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class InverseReferenceAdapterTestCases extends OXTestCase {

    private static final String PACKAGE = "org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference";

    private static final String XML_INPUT = "org/eclipse/persistence/testing/jaxb/annotations/xmlinversereference/input.xml";
    private static final String OXM_METADATA = "org/eclipse/persistence/testing/jaxb/annotations/xmlinversereference/oxm.xml";

    private ClassLoader loader;

    private JAXBContext jc;

    public InverseReferenceAdapterTestCases(String name) throws Exception {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        loader = Parent.class.getClassLoader();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, OXM_METADATA);
        jc = JAXBContextFactory.createContext(new Class[] { Customer.class, Address.class, PhoneNumber.class }, properties);
    }


    public void testMarshal() throws Exception {
        Customer c = new Customer();
        c.setAddress(new Address());

        StringWriter writer = new StringWriter();
        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(c, writer);


        assertTrue("LinkAdapter was not hit on marshal.", writer.toString().contains("MARSHALLED LINK"));
    }

    public void testUnmarshal() throws Exception {
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Customer c = (Customer) unmarshaller.unmarshal(loader.getResourceAsStream(XML_INPUT));

        assertEquals("LinkAdapter was not hit on unmarshal.", "UNMARSHALLED LINK", c.getAddress().getLink());
    }

}
