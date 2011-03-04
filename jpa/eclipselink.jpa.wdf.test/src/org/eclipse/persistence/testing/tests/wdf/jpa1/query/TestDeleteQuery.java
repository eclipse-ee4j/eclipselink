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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestDeleteQuery extends JPA1Base {

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
            Employee employee = new Employee(1, "Hugo", "Maier", dep10);
            em.persist(dep10);
            em.persist(dep20);
            em.persist(employee);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyCountOnDatabase(int expected, String table) throws SQLException {
        Connection conn = getEnvironment().getDataSource().getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement("select COUNT(*) from " + table);
            try {
                ResultSet rs = stmt.executeQuery();
                try {
                    verify(rs.next(), "no count ?!");
                    int count = rs.getInt(1);
                    verify(count == expected, "unexpected count: " + count + " on table >>" + table + "<<, expected: "
                            + expected);
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            conn.close();
        }
    }

    @Test
    public void testDeleteOutsideTx() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("delete from Department d");
            try {
                query.executeUpdate();
                flop("missing TransactionRequiredException");
            } catch (TransactionRequiredException ex) {
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDeleteWithSecondaryTable() {

        Project project1 = new Project();
        project1.setName("foo");
        project1.setPlannedDays(1);
        project1.setUsedDays(53);

        Project project2 = new Project();
        project2.setName("bar");
        project2.setPlannedDays(100);
        project2.setUsedDays(410);

        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            em.persist(project1);
            em.persist(project2);
            em.flush();
            getEnvironment().commitTransaction(em);
            verifyCountOnDatabase(2, "TMP_PROJECT");
            verifyCountOnDatabase(2, "TMP_PROJECT_DETAILS");

            getEnvironment().beginTransaction(em);
            Query deleteQuery = em.createQuery("DELETE FROM Project p WHERE p.id IS NOT NULL");
            deleteQuery.executeUpdate();
            em.flush();
            getEnvironment().commitTransaction(em);

            verifyCountOnDatabase(0, "TMP_PROJECT");
            verifyCountOnDatabase(0, "TMP_PROJECT_DETAILS");
        } catch (SQLException sqlEx) {
            throw new RuntimeException(sqlEx);
        } finally {
            closeEntityManager(em);
        }

    }

    @Test
    public void testDeleteAllDepartments() throws SQLException {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            init();
            getEnvironment().beginTransaction(em);
            // OS/390 doesn't get the update count right (returns -1), if there is no where clause.
            // Workaround is to supply a dummy where clause.
            Query emplyoeeDelete = em.createQuery("delete from Employee e");
            emplyoeeDelete.executeUpdate(); // del emp before dep caused by FK
            Query query = em.createQuery("delete from Department d where d.id is not null");
            int count = query.executeUpdate();
			if (!"JDBC".equals(getEnvironment().getPropertyValue(em, PersistenceUnitProperties.BATCH_WRITING))) {
				verify(count == 2, "wrong update count: " + count);
			}
            getEnvironment().commitTransaction(em);
            verifyCountOnDatabase(0, "TMP_DEP");
            init();
            getEnvironment().beginTransaction(em);
            emplyoeeDelete = em.createQuery("delete from Employee e");
            emplyoeeDelete.executeUpdate();
            query = em.createQuery("delete from Department d where d.id = 10");
            count = query.executeUpdate();
			if (!"JDBC".equals(getEnvironment().getPropertyValue(em, PersistenceUnitProperties.BATCH_WRITING))) {
				verify(count == 1, "wrong update count: " + count);
			}
            getEnvironment().commitTransaction(em);
            verifyCountOnDatabase(1, "TMP_DEP");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDeleteEmployeeWithGivenDepartment() throws SQLException {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            init();
            Department department = em.find(Department.class, Integer.valueOf(10));
            verifyDeleteEmployeeWithGivenDepartment(em, department);
            em.clear();
            init();
            verifyDeleteEmployeeWithGivenDepartment(em, dep10);
            em.clear();
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyDeleteEmployeeWithGivenDepartment(EntityManager em, Department dep) {
        getEnvironment().beginTransaction(em);
        Query query = em.createQuery("delete from Employee e where e.department = ?1");
        query.setParameter(1, dep);
        int count = query.executeUpdate();
        verify(count == 1, "wrong update count: " + count);
        getEnvironment().commitTransaction(em);
        getEnvironment().evict(em, Employee.class);
        verify(null == em.find(Employee.class, Integer.valueOf(1)), "employee found");
    }

    @Test
    public void testDeleteAllDepartmentsNative() throws SQLException {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            init();
            getEnvironment().beginTransaction(em);
            // OS/390 doesn't get the update count right (returns -1), if there is no where clause.
            // Workaround is to supply a dummy where clause.
            Query emplyoeeDelete = em.createQuery("delete from Employee e");
            emplyoeeDelete.executeUpdate(); // del emp before dep caused by FK
            Query query = em.createNativeQuery("delete from TMP_DEP where ID is not null");
            int count = query.executeUpdate();
            
			if (!"JDBC".equals(getEnvironment().getPropertyValue(em, PersistenceUnitProperties.BATCH_WRITING))) {
				verify(count == 2, "wrong update count: " + count);
			}
            getEnvironment().commitTransaction(em);
            verifyCountOnDatabase(0, "TMP_DEP");
            init();
            getEnvironment().beginTransaction(em);
            emplyoeeDelete = em.createQuery("delete from Employee e");
            emplyoeeDelete.executeUpdate();
            query = em.createNativeQuery("delete from TMP_DEP where TMP_DEP.ID = 10");
            count = query.executeUpdate();
			if (!"JDBC".equals(getEnvironment().getPropertyValue(em, PersistenceUnitProperties.BATCH_WRITING))) {
				verify(count == 1, "wrong update count: " + count);
			}
            getEnvironment().commitTransaction(em);
            verifyCountOnDatabase(1, "TMP_DEP");
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testExecuteUpdateSelect() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createQuery("select d from Department d");
            try {
                query.executeUpdate();
                flop("missing IllegalStateException");
            } catch (IllegalStateException ex) {
            } finally {
                getEnvironment().rollbackTransaction(em);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testGetResultListDelete() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createQuery("delete from Department d");
            try {
                query.getResultList();
                flop("missing IllegalStateException");
            } catch (IllegalStateException ex) {
            } finally {
                getEnvironment().rollbackTransaction(em);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testGetSingleResultDelete() {
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createQuery("delete from Department d");
            try {
                query.getResultList();
                flop("missing IllegalStateException");
            } catch (IllegalStateException ex) {
            } finally {
                getEnvironment().rollbackTransaction(em);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testUpdateDepartment() throws SQLException {
        clearAllTables();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            getEnvironment().beginTransaction(em);
            Department department = new Department(1, "hugo");
            em.persist(department);
            getEnvironment().commitTransactionAndClear(em);
            getEnvironment().beginTransaction(em);
            Query query = em.createQuery("update Department d set d.name = ?1 where d.id = ?2");
            query.setParameter(1, "emil");
            query.setParameter(2, Integer.valueOf(1));
            query.executeUpdate();
            getEnvironment().commitTransactionAndClear(em);
            Department found = em.find(Department.class, Integer.valueOf(1));
            verify("emil".equals(found.getName()), "wrong name: " + found.getName());
        } finally {
            closeEntityManager(em);
        }
    }
}
