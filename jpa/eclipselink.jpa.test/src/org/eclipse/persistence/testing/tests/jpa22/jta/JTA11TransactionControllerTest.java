/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     10/24/2017-3.0 Tomas Kraus
//       - 526419: Modify EclipseLink to reflect changes in JTA 1.1.
package org.eclipse.persistence.testing.tests.jpa22.jta;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa22.jta.Animal;
import org.eclipse.persistence.testing.models.jpa22.jta.AnimalCheck;
import org.eclipse.persistence.testing.models.jpa22.jta.AnimalDAO;
import org.eclipse.persistence.testing.models.jpa22.jta.AnimalDAOUpdate;
import org.eclipse.persistence.testing.models.jpa22.jta.AnimalEvent;
import org.eclipse.persistence.transaction.JTA11TransactionController;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test transaction controller extensions for JTA 1.1.
 * Test suite is activated only for server side tests.
 */
public class JTA11TransactionControllerTest extends JUnitTestCase {

    /** AnimalDAO JNDI name. */
    static final String ANIMAL_DAO_JNDI = "java:global/eclipselink-jpa22-model/eclipselink-jpa22-model_ejb/AnimalDAO";

    /** AnimalDAOUpdate JNDI name. */
    static final String ANIMAL_DAO_UPDATE_JNDI = "java:global/eclipselink-jpa22-model/eclipselink-jpa22-model_ejb/AnimalDAOUpdate";

    /** AnimalCheck JNDI name. */
    static final String ANIMAL_CHECK_JNDI = "java:global/eclipselink-jpa22-model/eclipselink-jpa22-model_ejb/AnimalCheck";

    /**
     * Creates jUnit test suite for this test class.
     *
     * @return jUnit test suite for this test class
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JTA11TransactionControllerTest");
        suite.addTest(new JTA11TransactionControllerTest("testCreateAndUpdateEntity"));
        return suite;
    }

    /**
     * Creates an instance of transaction controller extensions for JTA 1.1 test.
     */
    public JTA11TransactionControllerTest() {
        super();
    }

    /**
     * Creates an instance of transaction controller extensions for JTA 1.1 test.
     *
     * @param name jUnit test name
     */
    public JTA11TransactionControllerTest(final String name) {
        super(name);
    }

    /**
     * Test create and update entity using {@code JTA11TransactionController}.
     * Check that cached and persisted values are correct.
     */
   public void testCreateAndUpdateEntity() throws Exception {
       if (!isJTA11) {
           return;
       }
       AbstractSessionLog.getLog().log(SessionLog.INFO, "Running test: {0}", new String[] { "JTA11TransactionControllerTest.testCreateAndUpdateEntity" }, false);

       AnimalDAO animalDAO = lookupEJB(AnimalDAO.class, ANIMAL_DAO_JNDI);
       if (animalDAO == null) {
           fail("Could not lookup AnimalDAO Stateful EJB");
       }
       Animal animal = animalDAO.createAnimal(1, "Toby", null);
       AnimalDAOUpdate animalDAOUpdate = lookupEJB(AnimalDAOUpdate.class, ANIMAL_DAO_UPDATE_JNDI);
       if (animalDAO == null) {
           fail("Could not lookup AnimalDAOUpdate Stateful EJB");
       }
       AnimalCheck check = lookupEJB(AnimalCheck.class, ANIMAL_CHECK_JNDI);
       if (animalDAO == null) {
           fail("Could not lookup AnimalCheck Singleton EJB");
       }
       // Update EJB does the check before transaction commit so we expect "Toby" to be there
       Animal animal2 = animalDAOUpdate.updateAnimal(1, "Buster", check);
       String preRefreshName1 = check.getPreRefreshName();
       String postRefreshName1 = check.getPostRefreshName();
       assertEquals("Toby", preRefreshName1);
       assertEquals("Toby", postRefreshName1);
       // Calling check after exiting from EJB (after transaction commit) so we expect "Buster" to be there
       check.onEvent(AnimalEvent.update(animal, animal2));
       String preRefreshName2 = check.getPreRefreshName();
       String postRefreshName2 = check.getPostRefreshName();
       assertEquals("Buster", preRefreshName2);
       assertEquals("Buster", postRefreshName2);
   }

}
