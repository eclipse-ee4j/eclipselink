/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  -Matt MacIvor - Inital Implementation - 2.4.1
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.inheritance.interfaces;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class InterfacesTestCases extends OXTestCase {
    private static final String XML = "<customer><firstName>Jane</firstName><lastName>Doe</lastName></customer>";

    private JAXBContext jaxbContext;

    public InterfacesTestCases(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        jaxbContext = JAXBContextFactory.createContext(new Class[]{CustomerInt.class}, null);
    }

    public void testMarshal() throws Exception {
        Customer customer = new Customer();

        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        
        StringWriter writer = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.marshal(customer, writer);
        assertEquals(XML, writer.toString());
    }

}
