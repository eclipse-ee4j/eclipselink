/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.datatypes.arraypks;

import java.util.UUID;

import javax.persistence.EntityManager;
import junit.extensions.TestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.datatypes.arraypks.PrimByteArrayPKType;
import org.eclipse.persistence.testing.models.jpa.datatypes.arraypks.PrimitiveArraysAsPrimaryKeyTableCreator;

/**
 * <p>
 * <b>Purpose</b>: Tests caching of entities that use primitive arrays such as byte arrays
 * as primary keys in TopLink's JPA implementation.
 * <p>
 * <b>Description</b>: This class creates a test suite and adds tests to the
 * suite. The database gets initialized prior to the test methods.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for caching of Entities with primitive array types for primary keys
 * in TopLink's JPA implementation.
 * </ul>
 * @see org.eclipse.persistence.essentials.testing.models.cmp3.datatypes.arraypks.PrimitiveArraysAsPrimaryKeyTableCreator
 */
public class PrimitiveArrayPKCachingJUnitTestCase extends JUnitTestCase{
    public PrimitiveArrayPKCachingJUnitTestCase() {
    }
    public PrimitiveArrayPKCachingJUnitTestCase(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Caching Primitive Array pk types");
        suite.addTest(new PrimitiveArrayPKCachingJUnitTestCase("testPrimitiveByteArrayPK"));

        return new TestSetup(suite) {

            protected void setUp(){
                DatabaseSession session = JUnitTestCase.getServerSession();
                if (!(JUnitTestCase.getServerSession()).getPlatform().isOracle()){
                    session.logMessage("Warning, RAW type used for Primary keys only supported on Oracle");
                    return;
                }
                new PrimitiveArraysAsPrimaryKeyTableCreator().replaceTables(session);
            }

            protected void tearDown() {
                new PrimitiveArrayPKCachingJUnitTestCase().clearCache();
            }
        };
    }

    /**
     * Creates a PrimByteArrayPKType instance and then verifies that the same instance
     * is returned from the database.
     */
    public void testPrimitiveByteArrayPK() {
        if (!(JUnitTestCase.getServerSession()).getPlatform().isOracle()){
            JUnitTestCase.getServerSession().logMessage("Warning, RAW type used for Primary keys only supported on Oracle");
            return;
        }
        EntityManager em = createEntityManager();

        java.util.UUID uuid = UUID.randomUUID();
        PrimByteArrayPKType originalEntity = new PrimByteArrayPKType(PrimByteArrayPKType.getBytes(uuid));
        try {
            beginTransaction(em);
            em.persist(originalEntity);
            em.flush();
            PrimByteArrayPKType objectReadIn = em.find(PrimByteArrayPKType.class, PrimByteArrayPKType.getBytes(uuid));
            rollbackTransaction(em);
            assertTrue("Different instances of the same PrimByteArrayPKType object was returned", originalEntity == objectReadIn);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }
}
