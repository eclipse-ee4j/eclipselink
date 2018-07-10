/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     04/30/2009-2.0 Michael O'Brien
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
//
package org.eclipse.persistence.testing.tests.jpa.metamodel;

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
public class EntityManagerFactoryImplTest extends MetamodelTest {

    public EntityManagerFactoryImplTest() {
        super();
    }

    public EntityManagerFactoryImplTest(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("EntityManagerImplFactoryTest");
        if(!isJPA10() && !isOnServer()) {
            suite.addTest(new EntityManagerFactoryImplTest("testIllegalStateExceptionOnClosedEntityManagerFactory"));
        }
        return suite;
    }


    public void testIllegalStateExceptionOnClosedEntityManagerFactory() {
        EntityManagerFactory emf = null;
        Metamodel metamodel = null;
        boolean exceptionThrown = false;
        try {
            emf = initialize(true);

            // Verify an ISE if the emf is closed
            emf.close();
            closeEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            try {
                metamodel = emf.getMetamodel();
            } catch (IllegalStateException e) {
                exceptionThrown = true;
               assertNull(metamodel);
               assertTrue(e instanceof IllegalStateException);
               //System.out.println("_Disclaimer: The above IllegalStateException is expected as part of testing.");
               //e.printStackTrace();
            }
            assertTrue(exceptionThrown);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the EMF - to clear the cache for subsequent tests
            resetEntityManagerFactory();
        }
    }

/*
 * The following functions from Ch 5 of the 17 Mar 2009 JSR-317 JPA 2.0 API PFD are tested here.
 *
EntityManagerFactoryImpl
    public CriteriaBuilder getCriteriaBuilder() {
    public Metamodel getMetamodel() {
    */
}
