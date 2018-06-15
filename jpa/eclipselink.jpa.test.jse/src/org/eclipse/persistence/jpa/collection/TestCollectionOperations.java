/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jpa.collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

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
