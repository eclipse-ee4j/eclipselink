/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships;

import java.io.StringReader;
import java.io.StringWriter;

import javax.persistence.*;
import javax.xml.bind.*;

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
