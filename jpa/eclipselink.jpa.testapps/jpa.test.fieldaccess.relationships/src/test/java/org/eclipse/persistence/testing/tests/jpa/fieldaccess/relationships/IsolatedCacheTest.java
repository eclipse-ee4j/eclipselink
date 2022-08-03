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
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.IsolatedItem;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.RelationshipsTableManager;

public class IsolatedCacheTest extends JUnitTestCase {
    public IsolatedCacheTest() {}

    public IsolatedCacheTest(String name) {
        super(name);
    }

    public void testSetup () {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession("fieldaccess-relationships"));
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("IsolatedCacheTest (field access)");

        suite.addTest(new IsolatedCacheTest("testSetup"));
        suite.addTest(new IsolatedCacheTest("testCacheIsolationDBQueryHit"));

        return suite;
    }

    public void testCacheIsolationDBQueryHit() {
        EntityManager em = createEntityManager("fieldaccess-relationships");

        // Step 1 - get an isolated item in the cache.
        beginTransaction(em);
        try {
            IsolatedItem item = new IsolatedItem();
            item.setDescription("A phoney item");
            item.setName("Phoney name");
            em.persist(item);

            commitTransaction(em);

            // Step 2 - clear the entity manager and see if the item still exists
            // in the uow cache.
            beginTransaction(em);

            em.clear();
            RepeatableWriteUnitOfWork uow = (RepeatableWriteUnitOfWork)((EntityManagerImpl) em.getDelegate()).getUnitOfWork();

            assertFalse("The isolated item was not cleared from the shared cache", uow.getIdentityMapAccessor().containsObjectInIdentityMap(item));
            assertFalse("The isolated item was not cleared from the uow cache", uow.getParent().getIdentityMapAccessor().containsObjectInIdentityMap(item));

            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
