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
//     bdoughan - July 21/2010 - Initial implementation
package org.eclipse.persistence.testing.jaxb.interfaces;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.oxm.OXTestCase;

public class InterfaceTestCases extends OXTestCase {

    private static final String XML = "<customer><name>Jane Doe</name><address><city>My Town</city><street>123 Any Street</street></address><phone-number type=\"work\">613-555-1111</phone-number><phone-number type=\"cell\">613-555-2222</phone-number></customer>";

    private JAXBContext jaxbContext;

    public InterfaceTestCases(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
    }

    public void testLoadAndSave() throws Exception {
        Customer customer = (Customer) jaxbContext.createUnmarshaller().unmarshal(new StringReader(XML));

        assertNotNull(customer);
        assertEquals("Jane Doe", customer.getName());

        assertNotNull(customer.getAddress());
        assertEquals("My Town", customer.getAddress().getCity());

        assertNotNull(customer.getPhoneNumbers());
        assertEquals(2, customer.getPhoneNumbers().size());
        assertEquals("cell", customer.getPhoneNumbers().get(1).getType());

        StringWriter writer = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.marshal(customer, writer);
        assertEquals(XML, writer.toString());
    }

}
