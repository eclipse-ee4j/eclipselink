/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.datatypes;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.datatypes.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Test case to select boolean values only according to bug fix for GitHub bug #716.
 */
public class BooleanJUnitTestCase extends JUnitTestCase {

    public BooleanJUnitTestCase() {
        super();
    }

    public BooleanJUnitTestCase(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Boolean DataTypes");
        suite.addTest(new BooleanJUnitTestCase("testSetup"));
        suite.addTest(new BooleanJUnitTestCase("testCreateWrapperTypes"));
        suite.addTest(new BooleanJUnitTestCase("testBoolean"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new DataTypesTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }

    /**
     * Creates the WrapperTypes instance used in later tests.
     */
    public void testCreateWrapperTypes() {
        EntityManager em = createEntityManager();

        beginTransaction(em);
        WrapperTypes[] wrapperTypes = new WrapperTypes[4];
        wrapperTypes[0] = new WrapperTypes(BigDecimal.ZERO, BigInteger.ZERO, Boolean.FALSE,
                Byte.valueOf("0"), 'A', Short.valueOf("0"),
                0, 0L, 0.0f, 0.0, "A String");
        wrapperTypes[1] = new WrapperTypes(BigDecimal.ONE, BigInteger.ONE, Boolean.TRUE,
                Byte.valueOf("1"), 'B', Short.valueOf("1"),
                1, 1L, 1.0f, 1.0, "B String");
        wrapperTypes[2] = new WrapperTypes(new BigDecimal(2), new BigInteger("2"), Boolean.FALSE,
                Byte.valueOf("2"), 'C', Short.valueOf("2"),
                2, 2L, 2.0f, 2.0, "C String");
        wrapperTypes[3] = new WrapperTypes(new BigDecimal(3), new BigInteger("3"), Boolean.TRUE,
                Byte.valueOf("3"), 'D', Short.valueOf("3"),
                3, 3L, 3.0f, 3.0, "D String");
        for (WrapperTypes wrapperType: wrapperTypes) {
            em.persist(wrapperType);
        }
        commitTransaction(em);
    }

    /**
     * Tests to select boolean values only according to bug fix for GitHub bug #716.
     */
    public void testBoolean() {
        EntityManager em = createEntityManager();
        Query q;

        try {
            q = em.createQuery("SELECT wt.booleanData FROM WrapperTypes wt");
            List<Object> result = q.getResultList();
            assertTrue("Not all boolean rows are selected", result.size() == 4);
        } catch (RuntimeException e) {
            closeEntityManager(em);
            throw e;
        }
    }
}
