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
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestUpdate extends JPA1Base {

    @Test
    public void testUpdateOfComposoiteKey() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            CubiclePrimaryKeyClass cubKey = new CubiclePrimaryKeyClass(new Integer(40), new Integer(41));
            Cubicle cub = new Cubicle(cubKey, "green", null /* employee */
            );
            env.beginTransaction(em);
            em.persist(cub);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            cub = em.find(Cubicle.class, cubKey);
            cub.setColor("blue");
            env.commitTransactionAndClear(em);
            cub = em.find(Cubicle.class, cubKey);
            verify(cub != null, "cubicle null");
            verify(cub.getColor().equals("blue"), "wrong color");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testUpdateRelationWithCompositeKey() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Employee emp = new Employee(17, "first", "last", null /* department */
            );
            CubiclePrimaryKeyClass key1 = new CubiclePrimaryKeyClass(new Integer(98), new Integer(99));
            CubiclePrimaryKeyClass key2 = new CubiclePrimaryKeyClass(new Integer(5), new Integer(6));
            Cubicle cub1 = new Cubicle(key1, "orange", emp);
            env.beginTransaction(em);
            emp.setCubicle(cub1);
            em.persist(emp);
            em.persist(cub1);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            emp = em.find(Employee.class, new Integer(17));
            Cubicle cub2 = new Cubicle(key2, "dusky pink", emp);
            emp.setCubicle(cub2);
            em.persist(cub2);
            env.commitTransactionAndClear(em);
            emp = em.find(Employee.class, new Integer(17));
            verify(emp != null, "employee lost");
            verify(emp.getCubicle() != null, "cubicle lost");
            CubiclePrimaryKeyClass key = emp.getCubicle().getId();
            verify(key.equals(key2), "wrong cubicle");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testUpdatePrimaryKey() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            Department dep = new Department(1, "JST");
            env.beginTransaction(em);
            em.persist(dep);
            env.commitTransaction(em);
            em.clear();
            env.beginTransaction(em);
            dep = em.find(Department.class, Integer.valueOf(dep.getId()));
            dep.setId(99);
            try {
                env.commitTransaction(em);
                flop("exception not thrown as expected");
            } catch (RuntimeException e) {
                if (!checkForPersistenceException(e)) {
                    throw e;
                }
            }
        } finally {
            closeEntityManager(em);
        }
    }
}
