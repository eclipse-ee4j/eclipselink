/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.generator;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.island.Island;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestAutoGenerator extends JPA1Base {

    @Test
    public void testPersist() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Island p1 = new Island();
            em.persist(p1);
            verify(p1.getId() != 0, "id == 0");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMergeNew() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Island p1 = new Island();
            p1 = em.merge(p1);
            verify(p1.getId() != 0, "id == 0");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMergeDetached() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Island p1 = new Island();
            em.persist(p1);
            env.commitTransactionAndClear(em);
            verify(!em.contains(p1), "Island is not detached");
            env.beginTransaction(em);
            p1 = em.merge(p1);
            verify(p1.getId() != 0, "id == 0");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }
}
