/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.jpql.advanced;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.datatypes.DataTypesTableCreator;
import org.eclipse.persistence.testing.models.jpa.datatypes.WrapperTypes;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.junit.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * <b>Purpose</b>: Test Entity alias generation EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for alias generation EJBQL functionality
 * </ul>
 * @see EmployeePopulator
 * @see JUnitDomainObjectComparer
 */
public class JUnitJPQLNoAliasTest extends JUnitTestCase {
    private static int wrapperId;

    static JUnitDomainObjectComparer comparer;        //the global comparer object used in all tests

    public JUnitJPQLNoAliasTest() {
        super();
    }

    public JUnitJPQLNoAliasTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced-noalias";
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLInheritanceTest");
        suite.addTest(new JUnitJPQLNoAliasTest("testSetup"));
        suite.addTest(new JUnitJPQLNoAliasTest("testNoAlias"));
        suite.addTest(new JUnitJPQLNoAliasTest("testNoAliasOBJECT"));
        suite.addTest(new JUnitJPQLNoAliasTest("testNoAliasCOUNT"));
        suite.addTest(new JUnitJPQLNoAliasTest("testNoAliasCASTCOUNT"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();
        //set the session for the comparer to use
        comparer.setSession(getPersistenceUnitServerSession());

        new DataTypesTableCreator().replaceTables(getPersistenceUnitServerSession());
        clearCache();
        EntityManager em = createEntityManager();
        WrapperTypes wt;

        beginTransaction(em);
        wt = new WrapperTypes(BigDecimal.ZERO, BigInteger.ZERO, Boolean.FALSE,
                Byte.valueOf("0"), 'A', Short.valueOf("0"),
                0, 0L, 0.0f, 0.0, "A String");
        em.persist(wt);
        wrapperId = wt.getId();
        commitTransaction(em);
        closeEntityManager(em);
    }

    public void testNoAlias() {
        EntityManager em = createEntityManager();

        WrapperTypes wrapperTypes = (WrapperTypes)em.createQuery("SELECT wt FROM WrapperTypes").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes)getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAlias Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasOBJECT() {
        EntityManager em = createEntityManager();

        WrapperTypes wrapperTypes = (WrapperTypes)em.createQuery("SELECT OBJECT(wt) FROM WrapperTypes").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(WrapperTypes.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(wrapperId));

        WrapperTypes tlWrapperTypes = (WrapperTypes)getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("NoAliasOBJECT Test Failed", comparer.compareObjects(wrapperTypes, tlWrapperTypes));
    }

    public void testNoAliasCOUNT() {
        EntityManager em = createEntityManager();

        long result = em.createQuery("SELECT COUNT(wt) FROM WrapperTypes", Long.class).getSingleResult();
        Assert.assertTrue("NoAliasCOUNT Test Failed", result > 0L);
    }

    public void testNoAliasCASTCOUNT() {
        EntityManager em = createEntityManager();

        String result = em.createQuery("SELECT CAST(COUNT(wt) AS CHAR) FROM WrapperTypes", String.class).getSingleResult();
        Assert.assertTrue("NoAliasCOUNT Test Failed", result.length() > 0);
    }
}
