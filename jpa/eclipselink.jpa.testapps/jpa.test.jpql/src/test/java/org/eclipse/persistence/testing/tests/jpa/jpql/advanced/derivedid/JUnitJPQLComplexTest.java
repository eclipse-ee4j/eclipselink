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
//
//     04/11/2017-2.6 Will Dazey
//       - 512386: Add constructor initialization with CONCAT test
//     01/23/2018-2.7 Will Dazey
//       - 530214: trim operation should not bind parameters
package org.eclipse.persistence.testing.tests.jpa.jpql.advanced.derivedid;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.CompositePKTableCreator;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.eclipse.persistence.tools.schemaframework.StoredFunctionDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Test complex EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for complex EJBQL functionality
 * </ul>
 * @see EmployeePopulator
 * @see JUnitDomainObjectComparer
 */

public class JUnitJPQLComplexTest extends JUnitTestCase
{
//    static JUnitDomainObjectComparer comparer;        //the global comparer object used in all tests

    public JUnitJPQLComplexTest()
    {
        super();
    }

    public JUnitJPQLComplexTest(String name)
    {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced-derivedid";
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown()
    {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLComplexTest");
        suite.addTest(new JUnitJPQLComplexTest("testSetup"));

        List<String> tests = new ArrayList<>();
        tests.add("testEmbeddableDistinct");

        Collections.sort(tests);
        for (String test : tests) {
            suite.addTest(new JUnitJPQLComplexTest(test));
        }
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        //get session to start setup
        DatabaseSession session = getPersistenceUnitServerSession();
        new CompositePKTableCreator().replaceTables(getPersistenceUnitServerSession());
    }

    public StoredFunctionDefinition buildStoredFunction() {
        StoredFunctionDefinition func = new StoredFunctionDefinition();
        func.setName("StoredFunction_In");
        func.addArgument("P_IN", Long.class);
        func.setReturnType(Long.class);
        func.addStatement("RETURN P_IN * 1000");
        return func;
    }

    // Bug 245652
    // Test distinct on an embeddable.
    public void testEmbeddableDistinct() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select count(distinct c) from Captain c");
        query.getResultList();
        query = em.createQuery("Select count(distinct c) from Captain c where c.id.name <> ''");
        query.getResultList();
        if (getPlatform().isMySQL()) {
            // This only works on MySQL currently.
            query = em.createQuery("Select c, count(distinct c) from Captain c group by c");
            query.getResultList();
        }
        closeEntityManager(em);
    }
}
