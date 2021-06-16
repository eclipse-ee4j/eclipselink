/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.relationships;

import junit.framework.Test;
import junit.framework.TestSuite;

import jakarta.persistence.*;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.IsolatedItem;

import org.eclipse.persistence.testing.models.jpa.relationships.*;

public class IsolatedCacheTestSuite extends JUnitTestCase {
    public IsolatedCacheTestSuite() {}

    public IsolatedCacheTestSuite(String name) {
        super(name);
    }

    public void testSetup () {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("IsolatedCacheTestSuite");

        suite.addTest(new IsolatedCacheTestSuite("testSetup"));
        suite.addTest(new IsolatedCacheTestSuite("testCacheIsolationDBQueryHit"));

        return suite;
    }

    public void testCacheIsolationDBQueryHit() throws Exception {
        EntityManager em = createEntityManager();

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
