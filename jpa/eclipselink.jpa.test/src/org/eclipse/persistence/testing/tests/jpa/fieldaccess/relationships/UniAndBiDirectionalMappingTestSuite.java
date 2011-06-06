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

import java.util.Vector;

import javax.persistence.*;

import junit.framework.*;

import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * Test transactional operations with uni and bi-directional relationships.
 */
public class UniAndBiDirectionalMappingTestSuite extends JUnitTestCase {
    public UniAndBiDirectionalMappingTestSuite() {}
    
    public UniAndBiDirectionalMappingTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("UniAndBiDirectionalMappingTestSuite (fieldaccess)");
        suite.addTest(new UniAndBiDirectionalMappingTestSuite("testSetup"));
        suite.addTest(new UniAndBiDirectionalMappingTestSuite("selfReferencingManyToManyTest"));
        suite.addTest(new UniAndBiDirectionalMappingTestSuite("testManyToManyClearDelete"));
                
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession("fieldaccess"));
        clearCache("fieldaccess");
    }
    
    public void selfReferencingManyToManyTest() throws Exception {
        EntityManager em = createEntityManager("fieldaccess");
        
        beginTransaction(em);
  
        Customer owen = new Customer();
        owen.setName("Owen Pelletier");
        owen.setCity("Ottawa");
        em.persist(owen);
        int owenId = owen.getCustomerId();
        
        Customer kirty = new Customer();
        kirty.setName("Kirsten Pelletier");
        kirty.setCity("Ottawa");
        kirty.addCCustomer(owen);
        em.persist(kirty);
        int kirtyId = kirty.getCustomerId();
        
        Customer guy = new Customer();
        guy.setName("Guy Pelletier");
        guy.setCity("Ottawa");
        guy.addCCustomer(owen);
        guy.addCCustomer(kirty);
        kirty.addCCustomer(guy); // guess I'll allow this one ... ;-)
        em.persist(guy);
        int guyId = guy.getCustomerId();
        
        commitTransaction(em);
        
        clearCache("fieldaccess");
        
        Customer newOwen = em.find(Customer.class, owenId);
        Customer newKirty = em.find(Customer.class, kirtyId);
        Customer newGuy = em.find(Customer.class, guyId);
        
        assertTrue("Owen has controlled customers .", newOwen.getCCustomers().isEmpty());
        assertFalse("Kirty did not have any controlled customers.", newKirty.getCCustomers().isEmpty());
        assertFalse("Guy did not have any controlled customers.", newGuy.getCCustomers().isEmpty());

        closeEntityManager(em);
    }

    /**
     * Test deletion of both sides of a many-to-many.
     * This test emulates a CTS test that failed.
     */
    public void testManyToManyClearDelete() throws Exception {
        EntityManager entityManager = createEntityManager("fieldaccess");

        beginTransaction(entityManager);
        Customer owen = new Customer();
        owen.setName("Owen Pelletier");
        owen.setCity("Ottawa");
        entityManager.persist(owen);
        int owenId = owen.getCustomerId();

        Customer kirty = new Customer();
        kirty.setName("Kirsten Pelletier");
        kirty.setCity("Ottawa");
        kirty.addCCustomer(owen);
        entityManager.persist(kirty);
        int kirtyId = kirty.getCustomerId();

        owen.addCCustomer(kirty);

        commitTransaction(entityManager);

        beginTransaction(entityManager);

        owen = entityManager.find(Customer.class, owenId);
        kirty = entityManager.find(Customer.class, kirtyId);
        
        owen.setCCustomers(new Vector());
        kirty.setCCustomers(new Vector());
        entityManager.merge(owen);
        entityManager.merge(kirty);
        entityManager.remove(owen);
        entityManager.remove(kirty);
        
        commitTransaction(entityManager);
        closeEntityManager(entityManager);
    }
}
