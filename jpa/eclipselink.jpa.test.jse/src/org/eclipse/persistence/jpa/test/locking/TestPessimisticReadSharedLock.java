/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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
//     05/12/2026 - Jonathan S. Fisher
//       - 2738: LockModeType.PESSIMISTIC_READ generates FOR UPDATE instead of FOR SHARE
package org.eclipse.persistence.jpa.test.locking;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLCallListener;
import org.eclipse.persistence.jpa.test.locking.model.LockingDog;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestPessimisticReadSharedLock {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { LockingDog.class }, properties = {
            @Property(name = "eclipselink.cache.shared.default", value = "false"))
    private EntityManagerFactory emf;

    @SQLCallListener
    private List<String> sql;

    private int dogId;

    @Before
    public void before() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            LockingDog dog = new LockingDog();
            em.persist(dog);
            em.getTransaction().commit();
            dogId = dog.getId();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    private DatabasePlatform getPlatform() {
        return (DatabasePlatform) emf.unwrap(JpaEntityManagerFactory.class).getDatabaseSession().getPlatform();
    }

    @Test
    public void testPessimisticReadGeneratesForShare() {
        // Verify LOCK_SHARED creates correct ForUpdateClause
        ReadAllQuery query = new ReadAllQuery(LockingDog.class);
        query.setLockMode(ObjectBuildingQuery.LOCK_SHARED);
        Assert.assertNotNull("lockingClause should be set", query.getLockingClause());
        Assert.assertEquals("lockMode should be LOCK_SHARED",
                ObjectBuildingQuery.LOCK_SHARED, query.getLockMode());
        Assert.assertTrue("query should be a lock query", query.isLockQuery());

        // Verify platform produces correct shared lock syntax
        DatabasePlatform platform = getPlatform();
        if (platform.isMySQL()) {
            Assert.assertEquals(" LOCK IN SHARE MODE", platform.getSelectForShareString());
        } else if (platform instanceof PostgreSQLPlatform) {
            Assert.assertEquals(" FOR SHARE", platform.getSelectForShareString());
        }
    }

    @Test
    public void testPessimisticWriteStillGeneratesForUpdate() {
        DatabasePlatform platform = getPlatform();
        if (!platform.isMySQL() && !(platform instanceof PostgreSQLPlatform)) {
            return;
        }
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            sql.clear();
            LockingDog result = em.createQuery("SELECT d FROM LockingDog d WHERE d.id = :id", LockingDog.class)
                    .setParameter("id", dogId)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();
            Assert.assertNotNull(result);
            Assert.assertFalse("Expected SQL to be captured", sql.isEmpty());
            boolean foundForUpdate = false;
            for (int i = 0; i < sql.size(); i++) {
                if (sql.get(i).contains("FOR UPDATE")) {
                    foundForUpdate = true;
                }
            }
            Assert.assertTrue("PESSIMISTIC_WRITE should generate FOR UPDATE. All SQL: " + sql, foundForUpdate);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test
    public void testMySQLPlatformReturnsLockInShareMode() {
        MySQLPlatform mysql = new MySQLPlatform();
        Assert.assertEquals(" LOCK IN SHARE MODE", mysql.getSelectForShareString());
        Assert.assertEquals(" FOR UPDATE", mysql.getSelectForUpdateString());
    }

    @Test
    public void testPostgreSQLPlatformReturnsForShare() {
        PostgreSQLPlatform postgres = new PostgreSQLPlatform();
        Assert.assertEquals(" FOR SHARE", postgres.getSelectForShareString());
        Assert.assertEquals(" FOR UPDATE", postgres.getSelectForUpdateString());
    }

    @Test
    public void testDefaultPlatformFallsBackToForUpdate() {
        DatabasePlatform base = new DatabasePlatform();
        Assert.assertEquals(base.getSelectForUpdateString(), base.getSelectForShareString());
    }
}
