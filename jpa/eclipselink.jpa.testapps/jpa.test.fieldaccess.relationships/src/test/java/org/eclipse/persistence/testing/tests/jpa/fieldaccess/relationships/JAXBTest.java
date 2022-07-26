/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.persistence.EntityManager;
import jakarta.xml.bind.JAXBContext;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.Order;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.RelationshipsTableManager;

/**
 * Tests marshaling JPA objects with lazy through JAXB.
 */
public class JAXBTest extends JUnitTestCase {
    public JAXBTest() {}

    public JAXBTest(String name) {
        super(name);
    }

    public void testSetup() {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession("fieldaccess-relationships"));
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JAXBTest (fieldaccess)");
        suite.addTest(new JAXBTest("testSetup"));
        suite.addTest(new JAXBTest("testMarshal"));

        return suite;
    }

    public void testMarshal() throws Exception {
        EntityManager em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        Order order = new Order();
        em.persist(order);
        commitTransaction(em);
        closeEntityManager(em);

        clearCache("fieldaccess-relationships");
        em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        order = em.find(Order.class, order.getOrderId());
        commitTransaction(em);
        closeEntityManager(em);


        StringWriter writer = new StringWriter();
        JAXBContext.newInstance(Order.class, Customer.class).createMarshaller().marshal(order, writer);
        System.out.println(writer);
        StringReader reader = new StringReader(writer.toString());

        //order = (Order)JAXBContext.newInstance(Order.class, Customer.class).createUnmarshaller().unmarshal(reader);
        //JAXBElement elem = (JAXBElement) JAXBContext.newInstance(Order.class, Customer.class).createUnmarshaller().unmarshal(reader);
        order = (Order) JAXBContext.newInstance(Order.class, Customer.class).createUnmarshaller().unmarshal(reader);

        em = createEntityManager("fieldaccess-relationships");
        beginTransaction(em);
        em.merge(order);
        commitTransaction(em);
        closeEntityManager(em);
    }
}
