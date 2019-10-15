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
//     bdoughan - July 6/2010 - Initial implementation
package org.eclipse.persistence.testing.jaxb.emptystring;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;


import junit.framework.TestCase;

public class EmptyStringTestCases extends TestCase {

    private static final String CONTROL_XML = "<customer id=\"\"><name></name></customer>";

    private JAXBContext jaxbContext;

    public EmptyStringTestCases(String name) throws Exception {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        Class[] classes = new Class[1];
        classes[0] = Customer.class;
        jaxbContext = JAXBContextFactory.createContext(classes, null);
    }

    public void testMarshal() throws JAXBException {
        StringWriter xml = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        jaxbContext.createMarshaller().marshal(getControlObject(), xml);
        assertTrue(xml.toString().contains(CONTROL_XML));
    }

    public void testUnmarshal() throws JAXBException {
        StringReader xml = new StringReader(CONTROL_XML);
        Customer testCustomer = (Customer) jaxbContext.createUnmarshaller().unmarshal(xml);
        assertTrue(getControlObject().equals(testCustomer));
    }

    private Customer getControlObject() {
        Customer customer = new Customer();
        customer.setId("");
        customer.setName("");
        return customer;
    }

}
