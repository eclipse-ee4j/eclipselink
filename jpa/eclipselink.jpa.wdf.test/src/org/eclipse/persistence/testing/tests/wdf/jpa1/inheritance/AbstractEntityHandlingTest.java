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

package org.eclipse.persistence.testing.tests.wdf.jpa1.inheritance;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.persistence.testing.models.wdf.jpa1.employee.abstr.AbstractEmployee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.abstr.ContractEmployee;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;

public class AbstractEntityHandlingTest extends JPA1Base {

    @Before
    public void createEmployee() {
        ContractEmployee emp = new ContractEmployee();
        emp.setName("Hans Wurst");
        emp.setDailyRate(100);
        emp.setId(123645);
        emp.setStartDate(new Date(System.currentTimeMillis() - 1000000000));
        emp.setTerm(1);

        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        getEnvironment().beginTransaction(em);
        try {
            em.persist(emp);
            getEnvironment().commitTransactionAndClear(em);
        } catch (Exception ex) {
            if (getEnvironment().isTransactionActive(em)) {
                getEnvironment().rollbackTransaction(em);
            }
            throw new RuntimeException(ex);
        } finally {
            closeEntityManager(em);
        }
    }

    @After
    public void cleanUp() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        getEnvironment().beginTransaction(em);
        try {
            ContractEmployee emp = em.find(ContractEmployee.class, 123645);
            em.remove(emp);
            getEnvironment().commitTransactionAndClear(em);
        } catch (Exception ex) {
            if (getEnvironment().isTransactionActive(em)) {
                getEnvironment().rollbackTransaction(em);
            }
            throw new RuntimeException(ex);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void findEmployeeById() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            em.find(AbstractEmployee.class, 123645);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void findAllContractEmployees() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query1 = em.createQuery("SELECT OBJECT(ce) FROM ContractEmployee ce WHERE ce.dailyRate = ?1");
            query1.setParameter(1, 123645);
            query1.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void findAllAbstractEmployees() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query1 = em.createQuery("SELECT OBJECT(ae) FROM AbstractEmployee ae WHERE ae.name = ?1");
            query1.setParameter(1, "Hugo");
            query1.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }
}
