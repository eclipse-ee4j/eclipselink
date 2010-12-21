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

package org.eclipse.persistence.testing.tests.wdf.jpa1.query;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.BrokerageAccount;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CheckingAccount;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.CreditCardAccount;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.SavingsAccount;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestJoinedInheritance extends JPA1Base {
    private final Department dep10 = new Department(10, "ten");
    private final Department dep20 = new Department(20, "twenty");

    private void init() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // begin transaction
            env.beginTransaction(em);
            // persist predefined departments
            em.persist(dep10);
            em.persist(dep20);
            // create 4 Employees: 1 and 2 with dep 10, 3 and 4 with dep 20
            Employee[] e = new Employee[4];
            e[0] = new Employee(1, "Albert", "Acker", dep10);
            e[1] = new Employee(2, "Berta", "Baier", dep10);
            e[2] = new Employee(3, "Christa", "Clemens", dep20);
            e[3] = new Employee(4, "Dieter", "Deutsch", dep20);
            // create and persist 4 SavingAccounts used as sample accounts;
            SavingsAccount[] sa = new SavingsAccount[4];
            for (int i = 0; i < sa.length; i++) {
                sa[i] = new SavingsAccount();
                sa[i].setNumber(100 + (i + 1));
                sa[i].setOwner("none");
                sa[i].setBalance(10 + i);
                sa[i].setBankId("12345678");
                sa[i].setBankName("Volksbank");
                sa[i].setInterestRate(20 + 1);
                em.persist(sa[i]);
            }
            // each employee owns one savings account as sample account
            for (int i = 0; i < sa.length; i++) {
                e[i].setSampleAccount(sa[i]);
            }
            // create and persist 2 BrokerageAccounts
            BrokerageAccount[] ba = new BrokerageAccount[2];
            for (int i = 0; i < ba.length; i++) {
                ba[i] = new BrokerageAccount();
                ba[i].setNumber(200 + (i + 1));
                ba[i].setOwner("none");
                ba[i].setBalance(10 + i);
                ba[i].setRiskLevel("super-risky");
                em.persist(ba[i]);
            }
            // each employee from department 10 owns one brokerage account
            for (int i = 0; i < ba.length; i++) {
                e[i].setBrokerageAccount(ba[i]);
            }
            // create and persist 4 CreditCardAccounts
            CreditCardAccount[] cca = new CreditCardAccount[4];
            for (int i = 0; i < cca.length; i++) {
                cca[i] = new CreditCardAccount();
                cca[i].setNumber(300 + (i + 1));
                cca[i].setOwner("none");
                cca[i].setBalance(10 + i);
                cca[i].setCardNumber(1000000 + i);
                cca[i].setCardType("VISA");
                cca[i].setValidTo(Date.valueOf("2010-01-01"));
                em.persist(cca[i]);
            }
            // each employee from department 20 owns two credit card accounts
            e[2].addCreditCardAccount(cca[0]);
            e[2].addCreditCardAccount(cca[1]);
            e[3].addCreditCardAccount(cca[2]);
            e[3].addCreditCardAccount(cca[3]);
            // create and persist one checking account owned by employee 1
            CheckingAccount ca = new CheckingAccount();
            ca.setNumber(401);
            ca.setOwner("none");
            ca.setBalance(99);
            ca.setBankId("98765432");
            ca.setBankName("Sparkasse");
            ca.setCreditLimit(-5000);
            ca.setClient(e[0]);
            em.persist(ca);
            // persist all employees
            for (int i = 0; i < e.length; i++) {
                em.persist(e[i]);
            }
            // the following data is now available
            // 
            // e dep sa cac bac cca
            // ---------------------------
            // 1 10 sa1 ca1 ba1 ---
            // 2 10 sa2 --- ba2 ---
            // 3 20 sa3 --- --- cca1, cca2
            // 4 20 sa4 --- --- cca3, cca4
            // commit
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSelectEmployee() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query = em.createQuery("select e from Employee e where e.id = 1");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount " + result.size() + ", 1 is expected");
            Iterator iter = result.iterator();
            Object obj = iter.next();
            verify(obj instanceof Employee, "unexpected object, Employee is expected");
            Employee emp = (Employee) obj;
            verify(emp.getFirstName().equals("Albert"), "wrong first name " + emp.getFirstName() + ", Albert is expected");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSelectSampleAccounts() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query = em
                    .createQuery("select e.id, e.sampleAccount.number from Employee e where e.id > 0 order by e.id ASC");
            List result = query.getResultList();
            verify(result.size() == 4, "wrong resultcount " + result.size() + ", 4 is expected");
            Iterator iter = result.iterator();
            int i = 1;
            while (iter.hasNext()) {
                Object obj = iter.next();
                verify(obj instanceof Object[], "result is no object array");
                Object[] array = (Object[]) obj;
                verify(array.length == 2, "wrong length " + array.length + ", 2 is expected");
                verify(array[1] instanceof Long, "second select item is no Long (wrong class " + array[1].getClass() + ")");
                Long accountId = (Long) array[1];
                long expectedId = 100 + i;
                verify(accountId.equals(expectedId), "wrong id of sampleAccount " + accountId + ", " + expectedId
                        + " is expected");
                i++;
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSelectCheckingAccount() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query = em.createQuery("select e.checkingAccount.owner from Employee e where e.id = 1");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount " + result.size() + ", 1 is expected");
            Iterator iter = result.iterator();
            Object obj = iter.next();
            verify(obj instanceof String, "select item is no String (wrong class " + obj.getClass() + ")");
            String owner = (String) obj;
            verify(owner.equals("none"), "wrong owner " + owner + ", >>none<< is expected");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSelectBrokerageAccounts() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query = em
                    .createQuery("select e.id, e.brokerageAccount.number from Employee e where e.id > 0 order by e.id ASC");
            List result = query.getResultList();
            verify(result.size() == 2, "wrong resultcount " + result.size() + ", 2 is expected");
            Iterator iter = result.iterator();
            int i = 1;
            while (iter.hasNext()) {
                Object obj = iter.next();
                verify(obj instanceof Object[], "result is no object array");
                Object[] array = (Object[]) obj;
                verify(array.length == 2, "wrong length " + array.length + ", 2 is expected");
                verify(array[1] instanceof Long, "second select item is no Long (wrong class " + array[1].getClass() + ")");
                Long accountId = (Long) array[1];
                long expectedId = 200 + i;
                verify(accountId.equals(expectedId), "wrong id of brokerageAccount " + accountId + ", " + expectedId
                        + " is expected");
                i++;
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSelectCreditCardAccounts() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Query query = em
                    .createQuery("select cc.number from Employee e join e.creditCardAccounts cc where e.id = 3 order by cc.number");
            List result = query.getResultList();
            verify(result.size() == 2, "wrong resultcount " + result.size() + ", 2 is expected");
            Iterator iter = result.iterator();
            int i = 1;
            while (iter.hasNext()) {
                Object obj = iter.next();
                verify(obj instanceof Long, "select item is no Long (wrong class " + obj.getClass() + ")");
                Long accountId = (Long) obj;
                long expectedId = 300 + i;
                verify(accountId.equals(expectedId), "wrong id of creditCardAccount " + accountId + ", " + expectedId
                        + " is expected");
                i++;
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSelectEmployeeJoinCheckingAccount() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            // "e" could be used instead of "c.client"
            Query query = em
                    .createQuery("select e.id from Employee e join e.checkingAccount c where c.balance = 99 and c.client.brokerageAccount is not null");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount " + result.size() + ", 1 is expected");
            Iterator iter = result.iterator();
            Object obj = iter.next();
            verify(obj instanceof Integer, "select item is no Integer (wrong class " + obj.getClass() + ")");
            int id = ((Integer) obj).intValue();
            verify(id == 1, "wrong employee id " + id + ", 1 is expected");
            query = em
                    .createQuery("select distinct e.id from Employee e join e.checkingAccount c where c.balance = 99 and c.client.creditCardAccounts is not empty");
            result = query.getResultList();
            verify(result.size() == 0, "wrong resultcount " + result.size() + ", 0 is expected");
        } finally {
            closeEntityManager(em);
        }
    }
}
