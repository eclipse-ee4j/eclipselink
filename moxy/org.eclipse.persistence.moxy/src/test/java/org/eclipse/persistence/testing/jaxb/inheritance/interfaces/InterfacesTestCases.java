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
//  -Matt MacIvor - Initial Implementation - 2.4.1
package org.eclipse.persistence.testing.jaxb.inheritance.interfaces;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

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
