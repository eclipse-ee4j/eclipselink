/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.tests.wdf.jpa1.entitymanager;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestClear extends JPA1Base {

    @Test
    public void testClearActiveTransaction() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Department dep1 = new Department(1111, "one");
            Department dep2 = new Department(2222, "two");
            env.beginTransaction(em);
            em.persist(dep1);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            dep1 = em.find(Department.class, new Integer(dep1.getId()));
            em.persist(dep2);
            verify(em.contains(dep1), "entity not managed");
            verify(em.contains(dep2), "entity not managed");
            dep1.setName("changed"); // unflushed changes
            em.clear();
            verify(!em.contains(dep1), "entity managed");
            verify(!em.contains(dep2), "entity managed");
            env.commitTransactionAndClear(em);
            Department dep = em.find(Department.class, new Integer(dep1.getId()));
            verify(dep != null, "department with id " + dep1.getId() + " does not exist");
            verify("one".equals(dep.getName()), "department has wrong name: " + dep.getName());
            dep = em.find(Department.class, new Integer(dep2.getId()));
            verify(dep == null, "department with id " + dep2.getId() + " exists");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testClearNoTransaction() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        boolean extended = env.usesExtendedPC();
        try {
            Department dep1 = new Department(11, "one");
            Department dep2 = new Department(12, "two");
            env.beginTransaction(em);
            em.persist(dep1);
            env.commitTransactionAndClear(em);
            dep1 = em.find(Department.class, new Integer(dep1.getId()));
            if (extended) {
                em.persist(dep2);
                verify(em.contains(dep1), "entity not managed");
                verify(em.contains(dep2), "entity not managed");
            } else {
                verify(!em.contains(dep1), "entity managed");
            }
            em.clear();
            verify(!em.contains(dep1), "entity managed");
            verify(!em.contains(dep2), "entity managed");
        } finally {
            closeEntityManager(em);
        }
    }
}
