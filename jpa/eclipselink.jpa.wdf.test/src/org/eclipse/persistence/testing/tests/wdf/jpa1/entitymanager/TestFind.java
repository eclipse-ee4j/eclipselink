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
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CubiclePrimaryKeyClass;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.island.Island;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestFind extends JPA1Base {

    private final Department _dep = new Department(1, "eins");
    private final Employee _emp = new Employee(7, "first", "last", _dep);
    private final Cubicle _cub = new Cubicle(new Integer(1), new Integer(2), "yellow", _emp);

    @Override
    public void setup() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(_dep);
            em.persist(_emp);
            em.persist(_cub);
            em.flush();
            env.commitTransactionAndClear(em);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPositivTx() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, new Integer(7));
            verify(em.contains(emp), "Object not loaded");
            verify(emp.getId() == 7, "wrong id");
            verify(emp.getDepartment().getName().equals("eins"), "wrong department");
            emp = em.find(Employee.class, new Integer(7));
            verify(emp.getId() == 7, "wrong id");
            Department dep = em.find(Department.class, new Integer(1));
            verify(em.contains(dep), "Object not loaded");
            verify(dep.getId() == 1, "wrong id");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testPositivNonTx() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            Employee emp = em.find(Employee.class, new Integer(7));
            verify(emp.getId() == 7, "wrong id");
            verify(emp.getDepartment().getName().equals("eins"), "wrong department");
            emp = em.find(Employee.class, new Integer(7));
            verify(emp.getId() == 7, "wrong id");
            Department dep = em.find(Department.class, new Integer(1));
            verify(dep.getId() == 1, "wrong id");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNegativ() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            Object result = em.find(Employee.class, new Integer(17 + 4));
            verify(result == null, "found something");
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * IllegalArgumentException, if the first argument does not denote an entity type or the second argument is not a valid type
     * for that entity's primary key.
     */
    @Test
    public void testIllegalArguments() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            try {
                em.find(String.class, new Integer(17 + 4));
                flop("no IllegalArgumentException thrown");
            } catch (IllegalArgumentException ex) {
                verify(true, "");
            }
            try {
                em.find(Employee.class, "illegal key");
                flop("no IllegalArgumentException thrown");
            } catch (IllegalArgumentException ex) {
                verify(true, "");
            }
            try {
                em.find(Employee.class, null);
                flop("no IllegalArgumentException thrown");
            } catch (IllegalArgumentException ex) {
                verify(true, "");
            }
            try {
                em.find(null, "illegal key");
                flop("no IllegalArgumentException thrown");
            } catch (IllegalArgumentException ex) {
                verify(true, "");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFindWithCompositeKey() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            Integer one = new Integer(1);
            Integer two = new Integer(2);
            CubiclePrimaryKeyClass cubKey = new CubiclePrimaryKeyClass(one, two);
            Cubicle cub = em.find(Cubicle.class, cubKey);
            verify(cub.getFloor().equals(one) && cub.getPlace().equals(two), "wrong cubicle");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFindRemoved() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Island island = new Island();
            em.persist(island);
            env.commitTransactionAndClear(em);
            Integer islandId = Integer.valueOf(island.getId());
            env.beginTransaction(em);
            island = em.find(Island.class, islandId);
            em.remove(island);
            verify(em.find(Island.class, islandId) == null, "entity in state FOR_DELETE but found by find");
            env.rollbackTransactionAndClear(em);
            env.beginTransaction(em);
            island = em.find(Island.class, islandId);
            em.remove(island);
            em.flush();
            verify(em.find(Island.class, islandId) == null, "entity in state DELETE_EXECUTED but found by find");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }
}
