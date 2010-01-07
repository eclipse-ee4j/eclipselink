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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.datatypes;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.datatypes.*;
import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
public class NullBindingJUnitTestCase extends JUnitTestCase {
    private static int wrapperId;
    private static int byteArrayId;
    private static int pByteArrayId;
    private static int characterArrayId;
    private static int pCharArrayId;

    public NullBindingJUnitTestCase() {
        super();
    }

    public NullBindingJUnitTestCase(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Null Binding DataTypes");
        suite.addTest(new NullBindingJUnitTestCase("testSetup"));
        suite.addTest(new NullBindingJUnitTestCase("testCreateWrapperTypes"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyBigDecimal"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyBigInteger"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyBoolean"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyByte"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyCharacter"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyShort"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyInteger"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyLong"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyFloat"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyDouble"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyString"));
        suite.addTest(new NullBindingJUnitTestCase("testCreateByteArrayType"));
        suite.addTest(new NullBindingJUnitTestCase("testCreatePrimitiveByteArrayType"));
        suite.addTest(new NullBindingJUnitTestCase("testCreateCharacterArrayType"));
        suite.addTest(new NullBindingJUnitTestCase("testCreateCharArrayType"));

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
        WrapperTypes wt;

        beginTransaction(em);
        wt = new WrapperTypes(BigDecimal.ZERO, BigInteger.ZERO, Boolean.FALSE,
                new Byte("0"), 'A', new Short("0"),
                0, 0L, new Float(0.0), 0.0, "A String");
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
            assertTrue("Error setting BigDecimal field to null", wt2.getBigDecimalData() == null);
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
            assertTrue("Error setting BigInteger field to null", wt2.getBigIntegerData() == null);
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
            assertTrue("Error setting Boolean field to null", wt2.getBooleanData() == null);
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
            assertTrue("Error setting Byte field to null", wt2.getByteData() == null);
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
            assertTrue("Error setting Character field to null", wt2.getCharacterData() == null);
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
            assertTrue("Error setting Short field to null", wt2.getShortData() == null);
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
            assertTrue("Error setting Integer field to null", wt2.getIntegerData() == null);
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
            assertTrue("Error setting Long field to null", wt2.getLongData() == null);
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
            assertTrue("Error setting Float field to null", wt2.getFloatData() == null);
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
            assertTrue("Error setting Double field to null", wt2.getDoubleData() == null);
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
            assertTrue("Error setting String field to null", wt2.getStringData() == null);
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
            assertTrue("Error setting Byte[] field to null", bat2.getByteArrayData() == null);
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
            assertTrue("Error setting byte[] field to null", pbat2.getPrimitiveByteArrayData() == null);
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
            assertTrue("Error setting Character[] field to null", cat2.getCharacterArrayData() == null);
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
            assertTrue("Error setting char[] field to null", pcat2.getPrimitiveCharArrayData() == null);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }
}
