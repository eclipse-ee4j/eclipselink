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

package org.eclipse.persistence.testing.tests.wdf.jpa1.entitymanager;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.ReadOnlyEntity;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.ReadOnlyEntitySubclass;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestReadOnly extends JPA1Base {

    @Test
    @Bugzilla(bugid=309681)
    public void testIllegalFieldModification() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            ReadOnlyEntity oidem = new ReadOnlyEntity(1, "uno");
            env.beginTransaction(em);
            em.persist(oidem);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            oidem = em.find(ReadOnlyEntity.class, 1);
            oidem.setName("duo");
            env.commitTransactionAndClear(em); // ignore change for RO entity
            getEnvironment().getEntityManagerFactory().getCache().evictAll();

            env.beginTransaction(em);
            oidem = em.find(ReadOnlyEntity.class, 1);
            verify(oidem.getName().equals("uno"), "wrong name");
            em.remove(oidem);
            env.commitTransactionAndClear(em);
            verify(em.find(ReadOnlyEntity.class, 1) == null, "entity not deleted");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testQueryCachedEntity() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            ReadOnlyEntity oidem = new ReadOnlyEntity(3, "uno");
            env.beginTransaction(em);
            em.persist(oidem);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            oidem = em.find(ReadOnlyEntity.class, 3);
            oidem.setName("duo");
            env.rollbackTransactionAndClear(em);

            env.beginTransaction(em);
            Query query = em.createQuery("select R from ReadOnlyEntity R where R.id = 3 and R.name = 'uno'");
            ReadOnlyEntity oidem2 = (ReadOnlyEntity) query.getSingleResult();
            // find entity read by query in the cache
            verify(oidem2.getName().equals("duo"), "wrong name");
            verify(oidem == oidem2, "wrong reference");
            em.remove(oidem2);
            env.commitTransactionAndClear(em);
            verify(em.find(ReadOnlyEntity.class, 3) == null, "entity not deleted");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=309681)
    public void testCacheQueriedEntity() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            ReadOnlyEntity oidem = new ReadOnlyEntity(4, "uno");
            env.beginTransaction(em);
            em.persist(oidem);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            Query query = em.createQuery("select R from ReadOnlyEntity R where R.id = 4 and R.name = 'uno'");
            oidem = (ReadOnlyEntity) query.getSingleResult();
            oidem.setName("duo");
            env.rollbackTransactionAndClear(em);

            env.beginTransaction(em);
            ReadOnlyEntity oidem2 = em.find(ReadOnlyEntity.class, 4);
            verify(oidem2.getName().equals("duo"), "wrong name");
            verify(oidem == oidem2, "wrong reference");
            em.remove(oidem2);
            env.commitTransactionAndClear(em);
            verify(em.find(ReadOnlyEntity.class, 4) == null, "entity not deleted");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Skip(server=true)
    public void testInheritance() {
        // TODO decide how to handle inheritance
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            ReadOnlyEntitySubclass sub = new ReadOnlyEntitySubclass(2, "sub");
            env.beginTransaction(em);
            em.persist(sub);
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            ReadOnlyEntity upper = em.find(ReadOnlyEntity.class, 2);
            sub.setName("palim");
            env.commitTransactionAndClear(em); // ignore change for subclass if used as superclass
            getEnvironment().getEntityManagerFactory().getCache().evictAll(); // not supported on JPA 1 server

            env.beginTransaction(em);
            upper = em.find(ReadOnlyEntity.class, 2);
            verify(upper.getName().equals("sub"), "wrong name");

            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }

    }
}
