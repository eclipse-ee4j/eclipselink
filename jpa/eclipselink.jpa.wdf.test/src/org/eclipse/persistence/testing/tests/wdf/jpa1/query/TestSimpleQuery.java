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

import static javax.persistence.FlushModeType.AUTO;
import static javax.persistence.FlushModeType.COMMIT;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.models.wdf.jpa1.types.BasicTypesFieldAccess;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestSimpleQuery extends JPA1Base {

    private final Set<Department> ALL_DEPARTMENTS = new HashSet<Department>();
    private final Department dep10 = new Department(10, "ten");
    private final Department dep20 = new Department(20, "twenty");

    private void init() throws SQLException {
        clearAllTables();
        ALL_DEPARTMENTS.add(dep10);
        ALL_DEPARTMENTS.add(dep20);
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(dep10);
            em.persist(dep20);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSelectAllDepartments() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        boolean extended = getEnvironment().usesExtendedPC();
        try {
            Query query = em.createQuery("select d from Department d");
            List result = query.getResultList();
            verify(result.size() == 2, "wrong resultcount");
            Iterator iter = result.iterator();
            Set<Department> actualSet = new HashSet<Department>();
            while (iter.hasNext()) {
                Department entity = (Department) iter.next();
                if (extended) {
                    verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
                } else {
                    verify(!em.contains(entity), "entity manager contains result read outside transaction");
                }
                actualSet.add(entity);
            }
            verify(ALL_DEPARTMENTS.equals(actualSet), "the sets are somehow different");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSelectDepartmentByName() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        boolean extended = getEnvironment().usesExtendedPC();
        try {
            Query query = em.createQuery("select d from Department d where d.name = ?1");
            query.setParameter(1, "twenty");
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "row not found");
            Department entity = (Department) iter.next();
            if (extended) {
                verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
            } else {
                verify(!em.contains(entity), "entity manager contains result read outside transaction");
            }
            verify(entity.equals(dep20), " got wrong department");
            verify(!iter.hasNext(), "too many rows");
            query = em.createQuery("select distinct d from Department d where d.name = ?1");
            query.setParameter(1, "twenty");
            result = query.getResultList();
            iter = result.iterator();
            verify(iter.hasNext(), "row not found");
            entity = (Department) iter.next();
            if (extended) {
                verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
            } else {
                verify(!em.contains(entity), "entity manager contains result read outside transaction");
            }
            verify(entity.equals(dep20), " got wrong department");
            verify(!iter.hasNext(), "too many rows");
            query = em.createQuery("select d from Department d where d.name like 'twenty'");
            result = query.getResultList();
            iter = result.iterator();
            verify(iter.hasNext(), "row not found");
            entity = (Department) iter.next();
            if (extended) {
                verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
            } else {
                verify(!em.contains(entity), "entity manager contains result read outside transaction");
            }
            verify(entity.equals(dep20), " got wrong department");
            verify(!iter.hasNext(), "too many rows");
            query = em.createQuery("select d from Department d where d.name like 'twen%'");
            result = query.getResultList();
            iter = result.iterator();
            verify(iter.hasNext(), "row not found");
            entity = (Department) iter.next();
            if (extended) {
                verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
            } else {
                verify(!em.contains(entity), "entity manager contains result read outside transaction");
            }
            verify(entity.equals(dep20), " got wrong department");
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSelectInTransaction() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Query query = em.createQuery("select d from Department d");
            List result = query.getResultList();
            verify(result.size() == 2, "wrong resultcount");
            Iterator iter = result.iterator();
            while (iter.hasNext()) {
                Object entity = iter.next();
                verify(em.contains(entity), "entity manager doesn't contain result read inside transaction");
            }
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testIntIsNull() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Project project = new Project(null);
            em.persist(project);
            final int projectId = project.getId().intValue();
            env.commitTransactionAndClear(em);
            Query query = em.createQuery("select p from Project p where p.name IS NULL");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
            Iterator iter = result.iterator();
            while (iter.hasNext()) {
                project = (Project) iter.next();
                verify(project.getId().intValue() == projectId, "wrong project");
            }
            query = em.createQuery("select distinct p from Project p where p.name IS NULL");
            result = query.getResultList();
            verify(result.size() == 1, "wrong resultcount");
            iter = result.iterator();
            while (iter.hasNext()) {
                project = (Project) iter.next();
                verify(project.getId().intValue() == projectId, "wrong project");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyComparisonPrepdicateResult(EntityManager em, String txt, int... ids) {
        Query query = em.createQuery(txt);
        List result = query.getResultList();
        Set<Integer> actual = new HashSet<Integer>();
        for (Object object : result) {
            BasicTypesFieldAccess fa = (BasicTypesFieldAccess) object;
            actual.add(new Integer(fa.getId()));
        }
        Set<Integer> expected = new HashSet<Integer>();
        for (int i : ids) {
            expected.add(new Integer(i));
        }
        verify(expected.equals(actual), "expecetd and actual sets are different for query >>" + txt + "<<");
    }

    @Test
    public void testComparisonPrepdicate() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            BasicTypesFieldAccess fa = new BasicTypesFieldAccess(17);
            fa.setPrimitiveInt(17);
            em.persist(fa);
            fa = new BasicTypesFieldAccess(18);
            fa.setPrimitiveInt(18);
            em.persist(fa);
            fa = new BasicTypesFieldAccess(19);
            fa.setPrimitiveInt(19);
            em.persist(fa);
            fa = new BasicTypesFieldAccess(20);
            fa.setPrimitiveInt(20);
            em.persist(fa);
            env.commitTransactionAndClear(em);
            verifyComparisonPrepdicateResult(em, "Select Object(a) from BasicTypesFieldAccess a where a.primitiveInt = 17", 17);
            verifyComparisonPrepdicateResult(em, "Select Object(a) from BasicTypesFieldAccess a where a.primitiveInt > 17", 18,
                    19, 20);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNamedQuery() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        boolean extended = getEnvironment().usesExtendedPC();
        try {
            Query query = em.createNamedQuery("getDepartmentByName"); // select d from Department d where d.name = ?1
            query.setParameter(1, "twenty");
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "row not found");
            Department entity = (Department) iter.next();
            if (extended) {
                verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
            } else {
                verify(!em.contains(entity), "entity manager contains result read outside transaction");
            }
            verify(entity.equals(dep20), " got wrong department");
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testUndefinedNamedQuery() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            try {
                Query query = em.createNamedQuery("Hutzliputz");
                query.getResultList();
                flop("undefined query created");
            } catch (IllegalArgumentException e) {
            }
            try {
                Query query = em.createNamedQuery(null);
                query.getResultList();
                flop("undefined query created");
            } catch (IllegalArgumentException e) {
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNamedParameter() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        boolean extended = getEnvironment().usesExtendedPC();
        try {
            Query query = em.createQuery("select d from Department d where d.name = :name");
            query.setParameter("name", "twenty");
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "row not found");
            Department entity = (Department) iter.next();
            if (extended) {
                verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
            } else {
                verify(!em.contains(entity), "entity manager contains result read outside transaction");
            }
            verify(entity.equals(dep20), " got wrong department");
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testWrongParameter() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            try {
                Query query = em.createQuery("select d from Department d where d.name = :name");
                query.setParameter(1, "twenty");
                query.getResultList();
                flop("missing exception");
            } catch (RuntimeException e) {
            }
        } finally {
            closeEntityManager(em);
        }
    }
    
    @Test
    public void testCachedQueryWithClear() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createNamedQuery("getDepartmentCached");
            query.setParameter(1, "twenty");
            List<Department> result = query.getResultList();
            verify(result.size() == 1, "wrong result size");
            em.clear();
            result = query.getResultList();
            verify(result.size() == 1, "wrong result size");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=301063)
    public void testCachedQueryWithoutClear() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createNamedQuery("getDepartmentCached");
            query.setParameter(1, "twenty");
            List<Department> result = query.getResultList();
            verify(result.size() == 1, "wrong result size");
            result = query.getResultList();
            verify(result.size() == 1, "wrong result size");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testUnCachedQuery() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createNamedQuery("getDepartmentUnCached");
            query.setParameter(1, "twenty");
            List<Department> result = query.getResultList();
            verify(result.size() == 1, "wrong result size");
            result = query.getResultList();
            verify(result.size() == 1, "wrong result size");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testQueryStringNotValid() throws SQLException {
        // TODO enable test as soon as functionality is implemented
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            boolean caught = false;
            Query query;
            try {
                query = em.createQuery("this is not a valid query");
                try {
                    query.getResultList();
                } catch (PersistenceException ex) {
                    caught = true;
                }
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            verify(caught,
                    "illegal query threw neither IllegalArgumentExceptionon create nor PersistenceException on getResultList");
            caught = false;
            try {
                query = em.createQuery((String) null);
                try {
                    query.getResultList();
                } catch (PersistenceException ex) {
                    caught = true;
                }
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            verify(caught,
                    "null query threw neither IllegalArgumentExceptionon create nor PersistenceException on getResultList");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testExecuteQueryMultipleTimesWithSameParameter() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        boolean extended = getEnvironment().usesExtendedPC();
        try {
            Query query = em.createQuery("select d from Department d where d.name = :name");
            query.setParameter("name", "twenty");
            List result = query.getResultList();
            result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "row not found");
            Department entity = (Department) iter.next();
            if (extended) {
                verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
            } else {
                verify(!em.contains(entity), "entity manager contains result read outside transaction");
            }
            verify(entity.equals(dep20), " got wrong department");
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testExecuteQueryMultipleTimesWithDifferentParameter() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        boolean extended = getEnvironment().usesExtendedPC();
        try {
            Query query = em.createQuery("select d from Department d where d.name = ?1 and d.id = ?2");
            query.setParameter(1, "twenty");
            query.setParameter(2, Integer.valueOf(20));
            List result = query.getResultList();
            Iterator iter = result.iterator();
            verify(iter.hasNext(), "row not found");
            Department entity = (Department) iter.next();
            if (extended) {
                verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
            } else {
                verify(!em.contains(entity), "entity manager contains result read outside transaction");
            }
            verify(entity.equals(dep20), " got wrong department");
            verify(!iter.hasNext(), "too many rows");
            query.setParameter(1, "ten");
            query.setParameter(2, Integer.valueOf(10));
            result = query.getResultList();
            iter = result.iterator();
            verify(iter.hasNext(), "row not found");
            entity = (Department) iter.next();
            if (extended) {
                verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
            } else {
                verify(!em.contains(entity), "entity manager contains result read outside transaction");
            }
            verify(entity.equals(dep10), " got wrong department");
            verify(!iter.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testSingleResultOk() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        boolean extended = getEnvironment().usesExtendedPC();
        try {
            Query query = em.createQuery("select d from Department d where d.name = :name");
            query.setParameter("name", "twenty");
            Object entity = query.getSingleResult();
            if (extended) {
                verify(em.contains(entity), "entity manager doesn't contain result read outside transaction");
            } else {
                verify(!em.contains(entity), "entity manager contains result read outside transaction");
            }
            verify(entity.equals(dep20), " got wrong department");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNoResult() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select d from Department d where d.name = :name");
            query.setParameter("name", "hutzliputz");
            try {
                query.getSingleResult();
                flop("missing NoResultException");
            } catch (NoResultException e) {
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNonUniqueResult() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select d from Department d");
            try {
                query.getSingleResult();
                flop("missing NonUniqueResultException");
            } catch (NonUniqueResultException e) {
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testQueryWithFlush() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            em.persist(dep10);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            Department dep = em.find(Department.class, new Integer(10));
            dep.setName("newName");
            em.flush();
            Query query = em.createQuery("select d from Department d where d.name = :name");
            query.setParameter("name", "newName");
            Department entity = (Department) query.getSingleResult();
            verify(entity.getName().equals("newName"), " wrong department name");
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyRowCount(Query query, int maxRows, int startPos, int expected) {
        query.setMaxResults(maxRows);
        query.setFirstResult(startPos);
        int count = query.getResultList().size();
        verify(count == expected, "wrong row count: " + count);
    }

    @Test
    public void testMaxResult() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            for (int i = 0; i < 10; i++) {
                em.persist(new Project("P" + i));
            }
            env.commitTransaction(em);
            em.clear();
            Query query = em.createQuery("select p from Project p");
            verifyRowCount(query, 5, 0, 5);
            verifyRowCount(query, 10, 0, 10);
            verifyRowCount(query, 15, 0, 10);
            verifyRowCount(query, 5, 5, 5);
            verifyRowCount(query, 10, 5, 5);
            verifyRowCount(query, 15, 5, 5);
            verifyRowCount(query, 5, 7, 3);
            verifyRowCount(query, 10, 7, 3);
            verifyRowCount(query, 15, 7, 3);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=301274, databases=OraclePlatform.class )
    public void testMaxResultUnlimited() throws SQLException {
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            for (int i = 0; i < 10; i++) {
                em.persist(new Project("P" + i));
            }
            env.commitTransaction(em);
            em.clear();
            Query query = em.createQuery("select p from Project p");
            verifyRowCount(query, Integer.MAX_VALUE, 0, 10);
            verifyRowCount(query, Integer.MAX_VALUE, 5, 5);
            verifyRowCount(query, Integer.MAX_VALUE, 7, 3);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testFlushMode() throws SQLException {
        testFlushMode(AUTO, null);
        testFlushMode(AUTO, AUTO);
        testFlushMode(AUTO, COMMIT);
        testFlushMode(COMMIT, null);
        testFlushMode(COMMIT, AUTO);
        testFlushMode(COMMIT, COMMIT);
    }

    private void testFlushMode(FlushModeType flushModeEM, FlushModeType flushModeQuery) throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Query query = em.createQuery("select d from Department d where d.name = 'updated'");
            if (flushModeEM == COMMIT) {
                em.setFlushMode(COMMIT);
            }
            verify(em.getFlushMode() == flushModeEM, "wrong flushMode");
            if (flushModeQuery != null) {
                query.setFlushMode(flushModeQuery);
            }
            boolean flushExpected = isFlushExpected(flushModeEM, flushModeQuery);
            Department dep = em.find(Department.class, Integer.valueOf(10));
            dep.setName("updated");
            List result = query.getResultList();
            if (flushExpected) {
                verify(result.size() == 1, "wrong number of rows: " + result.size());
            } else {
                verify(result.size() == 0, "wrong number of rows: " + result.size());
            }
            env.commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private boolean isFlushExpected(FlushModeType flushModeEM, FlushModeType flushModeQuery) {
        if (flushModeQuery == null) {
            if (flushModeEM == COMMIT) {
                return false;
            } else {
                return true;
            }
        } else if (flushModeQuery == COMMIT) {
            return false;
        } else if (flushModeQuery == AUTO) {
            return true;
        }
        throw new IllegalArgumentException("Illegal flushMode(s): flushModeEM = " + flushModeEM + ", flushModeQuery = "
                + flushModeQuery);
    }

    @Test
    public void testTransactionBoundaries() throws SQLException {
        init();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        boolean extended = env.usesExtendedPC();
        boolean failureExpected = false;
        if (!extended) {
            failureExpected = true;
        }
        try {
            // create query outside transaction
            Query query = em.createQuery("select d from Department d where d.name = :name");
            query.setParameter("name", "twenty");
            List result = query.getResultList();
            verify(result.size() == 1, "wrong number of rows: " + result.size());
            verify(result.get(0).equals(dep20), "got wrong department");
            // execute again
            result = query.getResultList();
            verify(result.size() == 1, "wrong number of rows: " + result.size());
            verify(result.get(0).equals(dep20), "got wrong department");
            // execute inside transaction
            env.beginTransaction(em);
            try {
                result = query.getResultList();
                verify(result.size() == 1, "wrong number of rows: " + result.size());
                verify(result.get(0).equals(dep20), "got wrong department");
            } catch (PersistenceException e) {
                if (!failureExpected) {
                    throw e;
                }
            }
            env.rollbackTransaction(em);
            // create query inside transaction
            env.beginTransaction(em);
            query = em.createQuery("select d from Department d where d.name = :name");
            query.setParameter("name", "twenty");
            result = query.getResultList();
            verify(result.size() == 1, "wrong number of rows: " + result.size());
            verify(result.get(0).equals(dep20), "got wrong department");
            env.commitTransaction(em);
            // execute outside transaction
            try {
                result = query.getResultList();
                verify(result.size() == 1, "wrong number of rows: " + result.size());
                verify(result.get(0).equals(dep20), "got wrong department");
            } catch (IllegalStateException e) {
                if (!failureExpected) {
                    throw e;
                }
            }
            // execute inside new transaction
            env.beginTransaction(em);
            try {
                result = query.getResultList();
                verify(result.size() == 1, "wrong number of rows: " + result.size());
                verify(result.get(0).equals(dep20), "got wrong department");
            } catch (IllegalStateException e) {
                if (!failureExpected) {
                    throw e;
                }
            }
            env.rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testBooleanParameter() {
        EntityManager em = getEnvironment().getEntityManager();
        JPAEnvironment env = getEnvironment();
        try {
            env.beginTransaction(em);
            BasicTypesFieldAccess fieldAccess = new BasicTypesFieldAccess();
            fieldAccess.setId(1);
            fieldAccess.setPrimitiveBoolean(true);
            em.persist(fieldAccess);
            fieldAccess = new BasicTypesFieldAccess();
            fieldAccess.setId(2);
            fieldAccess.setPrimitiveBoolean(false);
            em.persist(fieldAccess);
            env.commitTransactionAndClear(em);
            Query query = em.createQuery("select b from BasicTypesFieldAccess b where b.primitiveBoolean = ?1");
            query.setParameter(1, Boolean.TRUE);
            List list = query.getResultList();
            Iterator iterator = list.iterator();
            verify(iterator.hasNext(), "no rows");
            BasicTypesFieldAccess value = (BasicTypesFieldAccess) iterator.next();
            verify(value.getId() == 1, "wrong id: " + value.getId());
            verify(!iterator.hasNext(), "too many rows");
        } finally {
            closeEntityManager(em);
        }
    }
}
