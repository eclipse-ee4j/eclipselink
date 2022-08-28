/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.jpql.advanced.compositepk;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.CompositePKTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Cubicle;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Scientist;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Test complex aggregate EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for complex aggregate EJBQL functionality
 * </ul>
 */
public class JUnitJPQLComplexAggregateTest extends JUnitTestCase {

    public JUnitJPQLComplexAggregateTest() {
        super();
    }

    public JUnitJPQLComplexAggregateTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    //This suite contains all tests contained in this class
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLComplexAggregateTest");
        suite.addTest(new JUnitJPQLComplexAggregateTest("testSetup"));

        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountOnJoinedCompositePK"));

        return suite;
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced-compositepk";
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown() {
        clearCache();
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        //get session to start setup
        DatabaseSession session = getPersistenceUnitServerSession();
        new CompositePKTableCreator().replaceTables(session);
    }

    /**
     * Test case bug 6155093:
     */
    public void complexCountOnJoinedCompositePK() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            Scientist s = new Scientist();
            s.setFirstName("John");
            s.setLastName("Doe");
            Cubicle c = new Cubicle();
            c.setCode("G");

            c.setScientist(s);
            s.setCubicle(c);
            em.persist(c);
            em.persist(s);
            em.flush();

            // Need to create the expected result manually, because using the
            // TopLink query API would run into the same issue 6155093.
            List<Long> expectedResult = Arrays.asList(1L);
            Collections.sort(expectedResult);

            // COUNT DISTINCT with inner join
            String jpql = "SELECT COUNT(DISTINCT p) FROM Scientist e JOIN e.cubicle p WHERE e.lastName LIKE 'D%'";
            TypedQuery<Long> q = em.createQuery(jpql, Long.class);
            List<Long> result = q.getResultList();
            Collections.sort(result);

            Assert.assertEquals("Complex COUNT on joined variable composite PK", expectedResult, result);
        } finally {
            rollbackTransaction(em);
        }
    }

}
