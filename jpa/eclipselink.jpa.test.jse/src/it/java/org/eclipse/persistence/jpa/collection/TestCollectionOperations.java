/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.collection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.eclipse.persistence.jpa.collection.model.NodeHolder;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestCollectionOperations {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { NodeHolder.class })
    private EntityManagerFactory emf;

    @Test
    public void testRemoveOperation() {
        // bug #395588 -  NPE is thrown when removing elements of Simple Collection of Integers and flushing in between
        int holderId = prepareHolder(emf, 222, 444);

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            NodeHolder holderDB = em.find(NodeHolder.class, holderId);
            holderDB.removeNodes(222);
            em.flush();
            holderDB.removeNodes(444);
            em.flush();
            transaction.commit();
        } catch (Throwable t) {
            if (transaction.isActive()) {
                transaction.rollback();
                throw new RuntimeException(t);
            }
        } finally {
            em.close();
        }

    }

    private int prepareHolder(EntityManagerFactory factory, int... nodeIds) {
        EntityManager em = null;
        em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {

            NodeHolder holderDB = new NodeHolder();
            for (int nodeId : nodeIds) {
                holderDB.addNodes(nodeId);
            }

            em.persist(holderDB);
            transaction.commit();

            return holderDB.getId();
        } catch (Throwable t) {
            if (transaction.isActive()) {
                transaction.rollback();
                throw new RuntimeException(t);
            }
        } finally {
            em.close();
        }
        return 0;
    }

}
