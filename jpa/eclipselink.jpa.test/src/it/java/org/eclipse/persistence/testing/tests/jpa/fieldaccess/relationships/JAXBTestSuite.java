/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.persistence.*;
import jakarta.xml.bind.*;

import junit.framework.*;

import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.Customer;

/**
 * Tests marshaling JPA objects with lazy through JAXB.
 */
public class JAXBTestSuite extends JUnitTestCase {
    public JAXBTestSuite() {}

    public JAXBTestSuite(String name) {
        super(name);
    }

    public void testSetup() {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession("fieldaccess"));
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JAXBTestSuite (fieldaccess)");
        suite.addTest(new JAXBTestSuite("testSetup"));
        suite.addTest(new JAXBTestSuite("testMarshal"));

        return suite;
    }

    public void testMarshal() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        beginTransaction(em);
        Order order = new Order();
        em.persist(order);
        commitTransaction(em);
        closeEntityManager(em);

        clearCache("fieldaccess");
        em = createEntityManager("fieldaccess");
        beginTransaction(em);
        order = em.find(Order.class, order.getOrderId());
        commitTransaction(em);
        closeEntityManager(em);


        StringWriter writer = new StringWriter();
        JAXBContext.newInstance(Order.class, Customer.class).createMarshaller().marshal(order, writer);
        System.out.println(writer.toString());
        StringReader reader = new StringReader(writer.toString());

        //order = (Order)JAXBContext.newInstance(Order.class, Customer.class).createUnmarshaller().unmarshal(reader);
        //JAXBElement elem = (JAXBElement) JAXBContext.newInstance(Order.class, Customer.class).createUnmarshaller().unmarshal(reader);
        order = (Order) JAXBContext.newInstance(Order.class, Customer.class).createUnmarshaller().unmarshal(reader);

        em = createEntityManager("fieldaccess");
        beginTransaction(em);
        em.merge(order);
        commitTransaction(em);
        closeEntityManager(em);
    }
}
