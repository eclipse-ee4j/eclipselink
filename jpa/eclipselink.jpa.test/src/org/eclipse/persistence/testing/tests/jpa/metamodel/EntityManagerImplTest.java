/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     04/30/2009-2.0 Michael O'Brien  
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 *     
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.metamodel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Metamodel;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * These tests verify the JPA 2.0 Metamodel API.
 * The framework is as follows:
 *   - initialize persistence unit
 *   - start a transaction
 *   - persist some entities to test
 *   - verify metamodel
 *   - delete test entities created above (to reset the database)
 *   - close persistence unit
 *
 */
public class EntityManagerImplTest extends MetamodelTest {

    public EntityManagerImplTest() {
        super();
    }
    
    public EntityManagerImplTest(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("EntityManagerImplTest");
        if(!isJPA10() && !isOnServer()) {
            suite.addTest(new EntityManagerImplTest("testIllegalStateExceptionOnClosedEntityManager"));
        }
        return suite;
    }
    
    public void testIllegalStateExceptionOnClosedEntityManager() {
        EntityManagerFactory emf = null;
        EntityManager em = null;;
        Metamodel metamodel = null;
        boolean exceptionThrown = false;
        try {
            emf = initialize(true);
            em = emf.createEntityManager();
            
            // Verify an ISE if the em is closed
            em.close();
            try {
                metamodel = em.getMetamodel();
            } catch (IllegalStateException e) {
                exceptionThrown = true;
               assertNull(metamodel);
               assertTrue(e instanceof IllegalStateException);
               //System.out.println("_Disclaimer: The above IllegalStateException is expected as part of testing.");
               //e.printStackTrace();
            } catch (Exception e) {
                // If we get here the ISE was not thrown - test fails
                e.printStackTrace();
            }
            assertTrue(exceptionThrown);            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //finalizeForTest(em, entityList);
            cleanup(em);
            // Close the EMF - to clear the cache for subsequent tests
            closeEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            resetEntityManagerFactory();
        }
    }
    
    /*
     * The following functions from Ch 5 of the 17 Mar 2009 JSR-317 JPA 2.0 API PFD are tested here.
     * 
EntityManagerImpl
    public CriteriaBuilder getCriteriaBuilder() {
    public Metamodel getMetamodel() {
    */
}
