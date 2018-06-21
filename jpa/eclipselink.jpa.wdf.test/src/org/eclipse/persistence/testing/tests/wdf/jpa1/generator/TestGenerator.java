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

package org.eclipse.persistence.testing.tests.wdf.jpa1.generator;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestGenerator extends JPA1Base {

    @Test
    public void testPersist() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Project p1 = new Project();
            em.persist(p1);
            verify(p1.getId() != null, "id is null");
            verify(p1.getId().intValue() != 0, "id == 0");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPersistManyProjects() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = new Employee(1, "Paul", "Fritz", null);
            Set<Employee> fritzSet = new HashSet<Employee>();
            fritzSet.add(emp);
            Set<Project> projectSet = new HashSet<Project>();
            em.persist(emp);
            for (int i = 0; i < 25; i++) {
                Project project = new Project("project " + i);
                em.persist(project);
                projectSet.add(project);
            }
            emp.setProjects(projectSet);
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
            Project p1 = new Project();
            p1 = em.merge(p1);
            verify(p1.getId() != null, "id is null");
            verify(p1.getId().intValue() != 0, "id == 0");
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
            Project p1 = new Project();
            em.persist(p1);
            env.commitTransactionAndClear(em);
            verify(!em.contains(p1), "project is not detached");
            env.beginTransaction(em);
            p1 = em.merge(p1);
            verify(p1.getId() != null, "id is null");
            verify(p1.getId().intValue() != 0, "id == 0");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }
}
