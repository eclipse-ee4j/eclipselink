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
 *  - rbarkhouse - 23 April 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.cycle;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CycleRecoverableTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/cycle/cycle.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/cycle/cycle.json";

    public CycleRecoverableTestCases(String name) throws Exception {
        super(name);

        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] { Company.class, EmployeePointer.class });
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

    public void testEqualsUsingEqualsMethod() throws Exception {
        Marshaller m = getJAXBMarshaller();

        // Use equals() method for object comparison.
        // Email's equals() always returns false, so Cycle cannot be detected.
        // Expecting StackOverflowError.
        m.setProperty("com.sun.xml.bind.objectIdentitityCycleDetection", false);

        Email em1 = new Email(); em1.user = "me"; em1.domain = "here.com";
        Email em2 = new Email(); em2.user = "myself"; em2.domain = "overthere.co.uk";
        em1.forward = em2; em2.forward = em1;

        Throwable expectedException = null;
        try {
            m.marshal(em1, new ByteArrayOutputStream());
        } catch (Throwable ex) {
            expectedException = ex;
        }

        assertNotNull("No exception caught as expected.", expectedException);
        assertEquals("Incorrect exception caught.", StackOverflowError.class, expectedException.getClass());

        m.setProperty("com.sun.xml.bind.objectIdentitityCycleDetection", true);
    }

    public void testUnhandledCycle() throws Exception {
        Company c = (Company) getControlObject();

        Employee e = new Employee();
        e.id = 40;
        e.name = "Fred Flintstone";
        ContactInfo info = new ContactInfo();
        Email em = new Email();
        em.user = "bcfred";
        em.domain = "slate.com";
        info.email = em;

        // Introduce unhandled cycle
        em.parentInfo = info;

        e.contactInfos.add(info);
        c.employees.add(e);

        // javax.xml.bind.MarshalException
        //      -> XMLMarshalException: An error occurred marshalling the object
        //              -> XMLMarshalException: A cycle is detected in the object graph.
        XMLMarshalException expectedException = null;
        try {
            Marshaller m = getJAXBContext().createMarshaller();
            //m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //m.setProperty(JAXBMarshaller.MEDIA_TYPE, "application/json");
            //m.marshal(c, System.out);
            m.marshal(c, new ByteArrayOutputStream());
        } catch (Exception ex) {
            expectedException = (XMLMarshalException) ex.getCause();
        }

        assertNotNull("No exception caught as expected.", expectedException);
        XMLMarshalException nested = (XMLMarshalException) expectedException.getCause();
        assertEquals("Incorrect exception code.", nested.getErrorCode(), XMLMarshalException.OBJECT_CYCLE_DETECTED);
    }

    @Override
    protected Object getControlObject() {
        Company c = new Company();
        c.name = "ACME Incorporated";

        Employee e1 = new Employee();
        e1.id = 789;
        e1.name = "Bob Smith";

        Employee e2 = new Employee();
        e2.id = 1234;
        e2.name = "Jane Doe";

        ContactInfo info1 = new ContactInfo();
        Address a1 = new Address();
        a1.addressString = "123 Main St.";
        info1.address = a1;
        PhoneNumber p1 = new PhoneNumber();
        p1.number = 5551212;
        info1.phoneNumber = p1;
        e1.contactInfos.add(info1);

        ContactInfo info2 = new ContactInfo();
        Address a2 = new Address();
        a2.addressString = "727 Water St.";
        info2.address = a2;
        PhoneNumber p2 = new PhoneNumber();
        p2.number = 5551655;
        info2.phoneNumber = p2;
        e1.contactInfos.add(info2);

        ContactInfo info3 = new ContactInfo();
        Address a3 = new Address();
        a3.addressString = "711 Maple Ave.";
        info3.address = a3;
        PhoneNumber p3 = new PhoneNumber();
        p3.number = 5559876;
        info3.phoneNumber = p3;
        Email em3 = new Email();
        em3.user = "Jane.Doe";
        em3.domain = "oracle.com";
        info3.email = em3;
        e2.contactInfos.add(info3);

        // Introduce object-cycles, which will be handled by
        // Employee.onCycleDetected
        p2.owningEmployee = e1;
        p3.owningEmployee = e2;

        c.employees.add(e1);
        c.employees.add(e2);

        return c;
    }

}
