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

package org.eclipse.persistence.testing.tests.wdf.jpa1.relation;

import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Cubicle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CubiclePrimaryKeyClass;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Before;
import org.junit.Test;

public class TestBidirectionalOneToOne extends JPA1Base {

    private static final int HANS_ID_VALUE = 1;
    private static final int FRED_ID_VALUE = 2;
    private static final Integer HANS_ID = new Integer(HANS_ID_VALUE);
    private static final Integer FRED_ID = new Integer(FRED_ID_VALUE);
    private static final CubiclePrimaryKeyClass GREEN_CUBICLE_ID = new CubiclePrimaryKeyClass(new Integer(10), new Integer(20));
    private static final CubiclePrimaryKeyClass BLUE_CUBICLE_ID = new CubiclePrimaryKeyClass(new Integer(33), new Integer(44));

    @Before
    public void seedDataModel() throws SQLException {
        /*
         * 5 Projects:
         */
        clearAllTables(); // clear all tables;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = new Department(17, "diverses");
            Employee hans = new Employee(HANS_ID_VALUE, "Hans", "Wurst", dep);
            Employee fred = new Employee(FRED_ID_VALUE, "Fred", "vom Jupiter", dep);
            Cubicle green = new Cubicle(GREEN_CUBICLE_ID, "green", hans);
            hans.setCubicle(green);
            Cubicle blue = new Cubicle(BLUE_CUBICLE_ID, "blue", null);
            em.persist(dep);
            em.persist(hans);
            em.persist(fred);
            em.persist(green);
            em.persist(blue);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testUnchanged() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // 1. do nothing
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, HANS_ID);
            // do nothing
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
            // 2. touch the cubicle
            env.beginTransaction(em);
            emp = em.find(Employee.class, HANS_ID);
            emp.getCubicle().getColor();
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
            // 3. trivial update
            env.beginTransaction(em);
            emp = em.find(Employee.class, HANS_ID);
            Cubicle cubicle = emp.getCubicle();
            emp.setCubicle(cubicle);
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMoveCubicle() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee hans = em.find(Employee.class, HANS_ID);
            Employee fred = em.find(Employee.class, FRED_ID);
            verify(hans != null, "employee not found");
            verify(fred != null, "employee not found");
            Cubicle fredsCubicle = fred.getCubicle();
            Cubicle hansCubicle = hans.getCubicle();
            fred.setCubicle(hansCubicle);
            hans.setCubicle(null);
            hansCubicle.setEmployee(fred);
            verify(fredsCubicle == null, "Freds cubicle is not null");
            env.commitTransactionAndClear(em);
            hans = em.find(Employee.class, HANS_ID);
            verify(hans != null, "Hans is null");
            verify(hans.getCubicle() == null, "Hans' cubicle is not null");
            fred = em.find(Employee.class, FRED_ID);
            verify(fred != null, "Fred is null");
            fredsCubicle = fred.getCubicle();
            verify(fredsCubicle != null, "Freds cubicle is null");
            verify(fredsCubicle.getId().equals(GREEN_CUBICLE_ID), "Freds cubicle has wrong id");
            Employee fredsCubiclesEmployee = fredsCubicle.getEmployee();
            verify(fredsCubiclesEmployee != null, "The employee of Freds cubicle is null");
            verify(fredsCubiclesEmployee.getId() == FRED_ID_VALUE, "The id of the employee of Freds cubicle is wrong");
            Cubicle blueCubicle = em.find(Cubicle.class, BLUE_CUBICLE_ID);
            verify(blueCubicle != null, "blue cubicle not found");
            verify(blueCubicle.getEmployee() == null, "The employee of the blue cubicle is not null");
        } finally {
            closeEntityManager(em);
        }
    }
}
