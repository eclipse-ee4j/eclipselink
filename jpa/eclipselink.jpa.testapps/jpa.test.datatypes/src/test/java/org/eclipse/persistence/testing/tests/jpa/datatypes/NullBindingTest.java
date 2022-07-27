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
package org.eclipse.persistence.testing.tests.jpa.datatypes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.datatypes.ByteArrayType;
import org.eclipse.persistence.testing.models.jpa.datatypes.CharArrayType;
import org.eclipse.persistence.testing.models.jpa.datatypes.CharacterArrayType;
import org.eclipse.persistence.testing.models.jpa.datatypes.DataTypesTableCreator;
import org.eclipse.persistence.testing.models.jpa.datatypes.PrimitiveByteArrayType;
import org.eclipse.persistence.testing.models.jpa.datatypes.WrapperTypes;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * <b>Purpose</b>: Test binding of null values to primitive wrapper and LOB type fields
 * in TopLink's JPA implementation.
 * <p>
 * <b>Description</b>: This class creates a test suite and adds tests to the
 * suite. The database gets initialized prior to the test methods.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for binding of null values to primitive wrapper and LOB type fields
 * in TopLink's JPA implementation.
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.datatypes.DataTypesTableCreator
 */
public class NullBindingTest extends JUnitTestCase {
    private static int wrapperId;
    private static int byteArrayId;
    private static int pByteArrayId;
    private static int characterArrayId;
    private static int pCharArrayId;

    public NullBindingTest() {
        super();
    }

    public NullBindingTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "datatypes";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Null Binding DataTypes");
        suite.addTest(new NullBindingTest("testSetup"));
        suite.addTest(new NullBindingTest("testCreateWrapperTypes"));
        suite.addTest(new NullBindingTest("testNullifyBigDecimal"));
        suite.addTest(new NullBindingTest("testNullifyBigInteger"));
        suite.addTest(new NullBindingTest("testNullifyBoolean"));
        suite.addTest(new NullBindingTest("testNullifyByte"));
        suite.addTest(new NullBindingTest("testNullifyCharacter"));
        suite.addTest(new NullBindingTest("testNullifyShort"));
        suite.addTest(new NullBindingTest("testNullifyInteger"));
        suite.addTest(new NullBindingTest("testNullifyLong"));
        suite.addTest(new NullBindingTest("testNullifyFloat"));
        suite.addTest(new NullBindingTest("testNullifyDouble"));
        suite.addTest(new NullBindingTest("testNullifyString"));
        suite.addTest(new NullBindingTest("testCreateByteArrayType"));
        suite.addTest(new NullBindingTest("testCreatePrimitiveByteArrayType"));
        suite.addTest(new NullBindingTest("testCreateCharacterArrayType"));
        suite.addTest(new NullBindingTest("testCreateCharArrayType"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new DataTypesTableCreator().replaceTables(getPersistenceUnitServerSession());
        clearCache();
    }

    /**
     * Creates the WrapperTypes instance used in later tests.
     */
    public void testCreateWrapperTypes() {
        EntityManager em = createEntityManager();
        WrapperTypes wt;

        beginTransaction(em);
        wt = new WrapperTypes(BigDecimal.ZERO, BigInteger.ZERO, Boolean.FALSE,
                Byte.valueOf("0"), 'A', Short.valueOf("0"),
                0, 0L, 0.0f, 0.0, "A String");
        em.persist(wt);
        wrapperId = wt.getId();
        commitTransaction(em);
    }

    /**
     */
    public void testNullifyBigDecimal() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setBigDecimalData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting BigDecimal field to null", wt2.getBigDecimalData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyBigInteger() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setBigIntegerData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting BigInteger field to null", wt2.getBigIntegerData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyBoolean() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setBooleanData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting Boolean field to null", wt2.getBooleanData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyByte() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setByteData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting Byte field to null", wt2.getByteData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyCharacter() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setCharacterData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting Character field to null", wt2.getCharacterData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyShort() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setShortData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting Short field to null", wt2.getShortData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyInteger() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setIntegerData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting Integer field to null", wt2.getIntegerData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyLong() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setLongData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting Long field to null", wt2.getLongData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyFloat() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setFloatData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting Float field to null", wt2.getFloatData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyDouble() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setDoubleData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting Double field to null", wt2.getDoubleData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyString() {
        EntityManager em = createEntityManager();
        Query q;
        WrapperTypes wt, wt2;

        try {
            beginTransaction(em);
            wt = em.find(WrapperTypes.class, wrapperId);
            wt.setStringData(null);
            commitTransaction(em);
            q = em.createQuery("SELECT wt FROM WrapperTypes wt WHERE wt.id = " + wrapperId);
            wt2 = (WrapperTypes) q.getSingleResult();
            assertNull("Error setting String field to null", wt2.getStringData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     * Creates a ByteArrayType instance.
     * Note that the Byte[] field is null.
     */
    public void testCreateByteArrayType() {
        EntityManager em = createEntityManager();

        if ((getPersistenceUnitServerSession()).getPlatform().isDerby()) {
            warning("Warning: Derby does not support setting a BLOB to null. For details, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=296293");
            return;
        }

        Query q;
        ByteArrayType bat, bat2;

        try {
            beginTransaction(em);
            bat = new ByteArrayType();
            em.persist(bat);
            byteArrayId = bat.getId();
            commitTransaction(em);
            q = em.createQuery("SELECT bat FROM ByteArrayType bat WHERE bat.id = " + byteArrayId);
            bat2 = (ByteArrayType) q.getSingleResult();
            assertNull("Error setting Byte[] field to null", bat2.getByteArrayData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     * Creates a PrimitiveByteArrayType instance.
     * Note that the byte[] field is null.
     */
    public void testCreatePrimitiveByteArrayType() {
        EntityManager em = createEntityManager();
        Query q;
        PrimitiveByteArrayType pbat, pbat2;

        try {
            beginTransaction(em);
            pbat = new PrimitiveByteArrayType();
            em.persist(pbat);
            pByteArrayId = pbat.getId();
            commitTransaction(em);
            q = em.createQuery("SELECT pbat FROM PrimitiveByteArrayType pbat WHERE pbat.id = " + pByteArrayId);
            pbat2 = (PrimitiveByteArrayType) q.getSingleResult();
            assertNull("Error setting byte[] field to null", pbat2.getPrimitiveByteArrayData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     * Creates a CharacterArrayType instance.
     * Note that the Character[] field is null.
     */
    public void testCreateCharacterArrayType() {
        EntityManager em = createEntityManager();
        Query q;
        CharacterArrayType cat, cat2;

        try {
            beginTransaction(em);
            cat = new CharacterArrayType();
            em.persist(cat);
            characterArrayId = cat.getId();
            commitTransaction(em);
            q = em.createQuery("SELECT cat FROM CharacterArrayType cat WHERE cat.id = " + characterArrayId);
            cat2 = (CharacterArrayType) q.getSingleResult();
            assertNull("Error setting Character[] field to null", cat2.getCharacterArrayData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     * Creates a CharArrayType instance.
     * Note that the char[] field is null.
     */
    public void testCreateCharArrayType() {
        EntityManager em = createEntityManager();
        Query q;
        CharArrayType pcat, pcat2;

        try {
            beginTransaction(em);
            pcat = new CharArrayType();
            em.persist(pcat);
            pCharArrayId = pcat.getId();
            commitTransaction(em);
            q = em.createQuery("SELECT pcat FROM CharArrayType pcat WHERE pcat.id = " + pCharArrayId);
            pcat2 = (CharArrayType) q.getSingleResult();
            assertNull("Error setting char[] field to null", pcat2.getPrimitiveCharArrayData());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }
}
