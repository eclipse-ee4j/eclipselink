/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/11/2014-2.5 Rick Curtis 
 *       - 440594: Tolerate invalid NamedQuery at EntityManager creation.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

public class InvalidNamedQueryTest extends JUnitTestCase {

    public InvalidNamedQueryTest() {
    }

    public InvalidNamedQueryTest(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(InvalidNamedQueryTest.class);
        
        return suite;
    }

    public void testInvalidNamedQuery() {
        try {
            EntityManager em = createEntityManager("invalid-named-query");
            fail("Shouldn't have got here!");
        } catch (PersistenceException re) {
            // expected
        }
    }

    /**
     * When eclipselink.tolerate-invalid-jpql=true and an invalid NamedQuery exists, em creation should succeed.
     */
    public void testEmCreateWithInvalidNamedQuery() {
        EntityManager em = null;
        try {
            em = createEntityManager("invalid-named-query-tolerate");
            assertNotNull(em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * When eclipselink.validation-only=true, em creation should fail due to invalid NamedQuery.
     */
    public void testEmCreateWithInvalidNamedQueryValidationOnly() {       
        EntityManager em = null;
        try {
            em = createEntityManager("invalid-named-query-validation-only");
            assertNull(em);
        }catch(PersistenceException pe){
            // expected
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * When eclipselink.tolerate-invalid-jpql=true em creation should be successful but an exception should
     * be thrown when trying to create that NamedQuery.
     */
    public void testQueryCreateInvalidNamedQuery() {
        EntityManager em = null;
        try {
            em = createEntityManager("invalid-named-query-tolerate");
            assertNotNull(em);
            
            try {
                em.createNamedQuery("non-existant-entity");
                fail();
            }catch(IllegalArgumentException pe){
                // Expected
            }
            // Ensure that we fail again.
            try {
                em.createNamedQuery("non-existant-entity");
                fail();
            }catch(IllegalArgumentException pe){
                // Expected
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}

