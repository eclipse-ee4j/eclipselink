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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Hobby;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.BasicTypesFieldAccess;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.eclipse.persistence.testing.tests.wdf.jpa1.query.Middle.MiddleInner.Inner;
import org.junit.Test;

public class TestSelectList extends JPA1Base {

    private void init() throws SQLException {
        clearAllTables();
        Employee hugo = new Employee(1, "Hugo", "Maier", null);
        Employee tina = new Employee(2, "Tina", "Schmidt", null);
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(hugo);
            em.persist(tina);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNewString() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select new java.lang.String(e.firstname) from Employee e where e.id = 1");
            Iterator<?> iter = query.getResultList().iterator();
            verify(iter.hasNext(), " no row found");
            Object object = iter.next();
            verify("Hugo".equals(object), "got wrong object: " + object);
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testNewInner() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em
                    .createQuery("select new org.eclipse.persistence.testing.tests.wdf.jpa1.query.Middle.MiddleInner.Inner(e.firstname) from Employee e where e.id = 1");
            Iterator<?> iter = query.getResultList().iterator();
            verify(iter.hasNext(), " no row found");
            Object object = iter.next();
            verify(object instanceof Inner, "got wrong class: " + object.getClass());
            verify("Hugo".equals(((Inner) object).getTxt()), "got wrong object: " + object);
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNewProject() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createQuery("select new " + Hobby.class.getName()
                    + "(e.firstname, e.lastname) from Employee e where e.id = 2");
            Iterator<?> iter = query.getResultList().iterator();
            verify(iter.hasNext(), " no row found");
            Object object = iter.next();
            verify(object instanceof Hobby, "got wrong class: " + object.getClass());
            Hobby hobby = (Hobby) object;
            verify("Schmidt".equals(hobby.getDescription()), "hobby has wrong description: " + hobby.getDescription());
            verify(!em.contains(hobby), "em contains hobby");
            verify(!iter.hasNext(), "too many rows");
            getEnvironment().rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testTwoSelectListItems() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select e.firstname, e.lastname from Employee e where e.id = 1");
            Iterator<?> iter = query.getResultList().iterator();
            verify(iter.hasNext(), " no row found");
            Object object = iter.next();
            verify(object instanceof Object[], "result is no array");
            Object[] array = (Object[]) object;
            verify(array.length == 2, "wrong length: " + array.length);
            verify("Hugo".equals(array[0]), "got wrong first name: " + array[0]);
            verify(array[0] instanceof String, "wrong class: " + array[0].getClass());
            verify("Maier".equals(array[1]), "got wrong last name: " + array[1]);
            verify(array[1] instanceof String, "wrong class: " + array[1].getClass());
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMixedSelectListItems() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select e.firstname, e from Employee e where e.id = 1");
            Iterator<?> iter = query.getResultList().iterator();
            verify(iter.hasNext(), " no row found");
            Object object = iter.next();
            verify(object instanceof Object[], "result is no array");
            Object[] array = (Object[]) object;
            verify(array.length == 2, "wrong length: " + array.length);
            verify(array[0] instanceof String, "wrong class: " + array[0].getClass());
            verify("Hugo".equals(array[0]), "got wrong first name: " + array[0]);
            verify(array[1] instanceof Employee, "wrong class: " + array[1].getClass());
            Employee employee = (Employee) array[1];
            verify(employee.getId() == 1, "got wrong employee: " + employee.getId());
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testMixedSelectListItemsReverse() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select e, e.firstname from Employee e where e.id = 1");
            Iterator<?> iter = query.getResultList().iterator();
            verify(iter.hasNext(), " no row found");
            Object object = iter.next();
            verify(object instanceof Object[], "result is no array");
            Object[] array = (Object[]) object;
            verify(array.length == 2, "wrong length: " + array.length);
            verify(array[0] instanceof Employee, "wrong class: " + array[0].getClass());
            Employee employee = (Employee) array[0];
            verify(employee.getId() == 1, "got wrong employee: " + employee.getId());
            verify(array[1] instanceof String, "wrong class: " + array[1].getClass());
            verify("Hugo".equals(array[1]), "got wrong first name: " + array[1]);
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testAverage() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select avg(e.id) from Employee e");
            Iterator<?> iter = query.getResultList().iterator();
            verify(iter.hasNext(), " no row found");
            Object object = iter.next();
            verify(object instanceof Number, "result is not numeric but: " + object.getClass().getName());
            verify(object instanceof Double, "result is not Double but: " + object.getClass().getName());
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testConstructorAverage() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select new java.lang.Double(avg(e.id)) from Employee e");
            Iterator<?> iter = query.getResultList().iterator();
            verify(iter.hasNext(), " no row found");
            Object object = iter.next();
            verify(object instanceof Number, "result is not numeric but: " + object.getClass().getName());
            verify(object instanceof Double, "result is not Double but: " + object.getClass().getName());
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    // @TestProperties(unsupportedDatabaseVendors=DatabaseVendor.OPEN_SQL)
    @ToBeInvestigated
    public void testConstructorArgumentBoxing() throws SQLException {
        List<BasicTypesFieldAccess> entities = new ArrayList<BasicTypesFieldAccess>();

        init();
        EntityManager em = getEnvironment().getEntityManager();
        getEnvironment().beginTransaction(em);
        try {
            int id = 7489;

            for (int i = 0; i < 10; i++) {
                entities.add(new BasicTypesFieldAccess(id++));
            }

            for (BasicTypesFieldAccess btfa : entities) {
                btfa.setPrimitiveDouble(Double.parseDouble("0.5"));
                btfa.setString2Varchar("maeh");
                btfa.setPrimitiveLong((long) 456);
                btfa.setBigDecimal(new BigDecimal(5));
                btfa.setBigInteger(BigInteger.valueOf(10));

                em.persist(btfa);
            }
        } finally {
            em.flush();
            getEnvironment().commitTransaction(em);
        }

        try {
            Query q = em
                    .createQuery("SELECT new com.sap.engine.services.orpersistence.jver.query.TestSelectList.TestClass2"
                            + "(btfa.string2Varchar, count(btfa.string2Varchar), avg(btfa.primitiveLong)) FROM BasicTypesFieldAccess btfa GROUP BY btfa.string2Varchar");
            q.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    public static class TestClass2 {
        private final String field1;
        private final long field2;
        private final double field3;

        public String getField1() {
            return field1;
        }

        public long getField2() {
            return field2;
        }

        public double getField3() {
            return field3;
        }

        public TestClass2(String field1, Long field2, Double field3) {
            super();
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }

    }
}
