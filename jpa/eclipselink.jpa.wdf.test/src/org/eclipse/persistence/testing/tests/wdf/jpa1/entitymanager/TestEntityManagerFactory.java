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

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.Assert;

import org.eclipse.persistence.testing.framework.wdf.AbstractBaseTest;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

// we cannot close the JTA EntityManagerFactory -> makes further tests impossible
public class TestEntityManagerFactory extends JPA1Base {

    @Test
    /**
     * just for the case that all other methods are skipped 
     */
    public void dummyTestMethod() {
        return;
    }

    @Test()
    @Skip(server=true)
    public void testEntityManagerFactoryClose() throws NamingException {
        final boolean closingEmfThrowsException = AbstractBaseTest.isInsideEngine();
        final JPAEnvironment env = getEnvironment();
        final EntityManagerFactory emf = env.createNewEntityManagerFactory();
        final EntityManager em = emf.createEntityManager();
        try {
            emf.close();
        } catch (IllegalStateException e) {
            if (closingEmfThrowsException) {
                return;
            } else {
                throw e;
            }
        }
        verify(!emf.isOpen(), "EntityManagerFactory not closed");
        verify(!em.isOpen(), "EntityManager not closed");
        try {
            em.contains(new Department(1, "dummy"));
            flop("operation on a closed EntityManager did not throw IllegalStateException");
            em.close();
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
            Assert.assertTrue(true);
        }
    }

    @Test
    @ToBeInvestigated
    public void testEntityManagerFactoryCloseActiveTransaction() throws NamingException {
        final boolean closingEmfThrowsException = AbstractBaseTest.isInsideEngine();
        final JPAEnvironment env = getEnvironment();
        final EntityManagerFactory emf = env.createNewEntityManagerFactory();
        final EntityManager em = emf.createEntityManager();
        Department dep1 = new Department(11, "Muggles");
        Department dep2 = new Department(12, "Wizards");
        env.beginTransaction(em);
        em.persist(dep1);
        env.commitTransactionAndClear(em);
        env.beginTransaction(em);
        dep1 = em.find(Department.class, new Integer(dep1.getId()));
        em.persist(dep2);
        try {
            emf.close(); // persistence context should remain active
        } catch (IllegalStateException e) {
            if (closingEmfThrowsException) {
                closeEntityManager(em);
                return;
            } else {
                throw e;
            }
        }
        try {
            em.contains(dep1); // should not work any longer
            flop("operation on a closed entity manager did not throw IllegalStateException");
            em.close();
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
            Assert.assertTrue(true);
        }
        env.rollbackTransaction(em);
        env.beginTransaction(em);
        verify(!emf.isOpen(), "EntityManagerFactory not closed");
        verify(!em.isOpen(), "EntityManager not closed");
        try {
            em.contains(new Department(1, "dummy"));
            flop("operation on a closed EntityManager did not throw IllegalStateException");
            em.close();
        } catch (IllegalStateException e) {
            // $JL-EXC$ expected behavior
            Assert.assertTrue(true);
        }
    }
}
