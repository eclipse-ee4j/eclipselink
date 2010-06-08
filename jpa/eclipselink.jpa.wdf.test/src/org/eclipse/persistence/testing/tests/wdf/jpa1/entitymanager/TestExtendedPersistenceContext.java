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

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestExtendedPersistenceContext extends JPA1Base {

    @Test
    /**
     * just for the case that all other methods are skipped 
     */
    public void dummyTestMethod() {
        return;
    }

    @Test
    // @TestProperties(unsupportedEnvironments = { JTASharedPCEnvironment.class })
    public void testPersistOutsideTx() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(1, "test");
            em.persist(dep);
            env.beginTransaction(em);
            verify(em.contains(dep), "persistence context does not contain entity");
            env.commitTransaction(em);
            verify(em.contains(dep), "persistence context does not contain entity");
            em.clear();
            dep = em.find(Department.class, new Integer(dep.getId()));
            verify(dep != null, "entity not found");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    // @TestProperties(unsupportedEnvironments = { JTASharedPCEnvironment.class })
    public void testUpdateOutsideTx() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(11, "original");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            dep = em.find(Department.class, new Integer(dep.getId()));
            dep.setName("modified");
            env.beginTransaction(em);
            verify(em.contains(dep), "persistence context does not contain entity");
            env.commitTransaction(em);
            verify(em.contains(dep), "persistence context does not contain entity");
            em.clear();
            dep = em.find(Department.class, new Integer(dep.getId()));
            verify("modified".equals(dep.getName()), "entity not updated");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    // @TestProperties(unsupportedEnvironments = { JTASharedPCEnvironment.class })
    public void testRemoveOutsideTx() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(21, "test");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransaction(em);
            em.remove(dep);
            env.beginTransaction(em);
            env.commitTransactionAndClear(em);
            dep = em.find(Department.class, new Integer(dep.getId()));
            verify(dep == null, "entity not removed");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    // @TestProperties(unsupportedEnvironments = { JTASharedPCEnvironment.class })
    public void testMergeOutsideTx() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(31, "test");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransactionAndClear(em);
            dep.setName("detached");
            em.merge(dep);
            verify("detached".equals(dep.getName()), "entity not merged");
            env.beginTransaction(em);
            env.commitTransactionAndClear(em);
            dep = em.find(Department.class, new Integer(dep.getId()));
            verify("detached".equals(dep.getName()), "entity not merged");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    // @TestProperties(unsupportedEnvironments = { JTASharedPCEnvironment.class })
    public void testRefreshOutsideTx() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(41, "test");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransaction(em);
            dep.setName("modified");
            em.refresh(dep);
            verify("test".equals(dep.getName()), "entity not refreshed");
            env.beginTransaction(em);
            env.commitTransactionAndClear(em);
            dep = em.find(Department.class, new Integer(dep.getId()));
            verify("test".equals(dep.getName()), "entity not refreshed");
        } finally {
            closeEntityManager(em);
        }
    }
}
