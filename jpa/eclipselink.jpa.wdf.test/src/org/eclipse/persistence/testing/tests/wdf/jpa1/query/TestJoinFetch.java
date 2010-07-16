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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Hobby;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.Node;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestJoinFetch extends JPA1Base {

    // private final DatabaseStatisticsRequestManager databaseStatistics =
    // OpenSQLServices.getDatabaseStatisticsRequestManager();

    private final static String description = "description_";

    private static final Map<Integer, Employee> employeeStore = new HashMap<Integer, Employee>();

    private void init() throws SQLException {
        clearAllTables();
        employeeStore.clear();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            for (int depNo = 1; depNo <= 3; depNo++) {
                Department department = new Department(depNo, "dep_" + depNo);
                em.persist(department);

                for (int empNo = 1; empNo <= depNo + 2; empNo++) {
                    int id = 10 * depNo + empNo;
                    Employee employee = new Employee(id, "first_" + id, "last_" + id, department);
                    List<Hobby> hobbies = new ArrayList<Hobby>();
                    Set<Project> projects = new HashSet<Project>();
                    for (int hobbyCount = 0; hobbyCount < empNo; hobbyCount++) {
                        Project project = new Project();
                        project.setName(description + hobbyCount);
                        Set<Employee> employees = new HashSet<Employee>(1);
                        employees.add(employee);

                        project.setEmployees(employees);
                        projects.add(project);
                        Hobby hobby = new Hobby();
                        em.persist(hobby);

                        hobby.setDescription(description + hobby.getId());
                        hobbies.add(hobby);
                    }
                    em.persist(employee);
                    for (Project proj : projects) {
                        em.persist(proj);
                    }
                    employee.setProjects(projects);
                    employee.setHobbies(hobbies);
                    em.merge(employee);
                    for (Project proj : projects) {
                        em.merge(proj);
                    }
                    employeeStore.put(employee.getId(), employee);

                }
            }

            Node parent = new Node(1, null);
            Node child1 = new Node(2, parent);
            Node child2 = new Node(3, parent);
            em.persist(parent);
            em.persist(child1);
            em.persist(child2);

            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testJoinFetchDepartment() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select e from Employee e join fetch e.department");
            List<?> result = query.getResultList();

            verify(result.size() == 12, "wrong size");
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testJoinFetchProjects() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query q = em.createQuery("SELECT DISTINCT e FROM Employee e JOIN FETCH e.projects");
            List<Employee> result = q.getResultList();
            verify(result.size() == 12, "wrong size of result list. expected 12, got " + result.size());

            for (Employee emp : result) {
                verify(emp.getProjects() != null, "the field projects in employee " + emp.getId()
                        + " is null. It shold at least be populated with an empty list.");
                // the project should at least have a back reference to emp

                for (Project proj : emp.getProjects()) {
                    boolean found = false;
                    for (Object backEmp : proj.getEmployees()) {
                        if (((Employee) backEmp).getId() == emp.getId()) {
                            found = true;
                        }
                    }
                    verify(found, "could not find back reference from project " + proj.getId() + " to employee + "
                            + emp.getId());
                }
            }
        } finally {
            closeEntityManager(em);
        }
    }

    private void countStatements(String jpql, int rows, int statements) throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            // final DatabaseStatisticsRecord record = databaseStatistics.start("testCountStatements");
            Query query = em.createQuery(jpql);
            @SuppressWarnings("unused")
            List<?> result = query.getResultList();
            // databaseStatistics.stop(record);
            // if(record != null && record.getExecuteQueryCount() > 0) {
            // assertTrue("The number of monitored SQL statements ("+ record.getExecuteQueryCount() +")" +
            // " does not match the number of expected statemetns ("+ statements +").",
            // record.getExecuteQueryCount() == statements);
            // }
            //            
            // verify(result.size() == rows, "wrong size; expected " + rows + " - got " + result.size());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testOneNode() throws SQLException {
        countStatements("select n from Node n join fetch n.parent where n.id = 2", 1, 1);
    }

    @Test
    public void testTwoNodes() throws SQLException {
        countStatements("select n from Node n join fetch n.parent where n.id <> 1", 2, 1);
    }

    @Test
    public void testJoinFetchHobbies() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select e from Employee e join fetch e.hobbies");
            List<?> result = query.getResultList();

            verify(result.size() == 31, "wrong size; expected 31 - got " + result.size());
        } finally {
            closeEntityManager(em);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    // @TestProperties(unsupportedEnvironments={JTAEnvironment.class})
    @ToBeInvestigated
    public void testDistinctJoinFetchHobbies() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();

        try {
            getEnvironment().beginTransaction(em);
            Query query = em.createQuery("select distinct e from Employee e join fetch e.hobbies ORDER BY e.id");
            List<Employee> result = query.getResultList();
            getEnvironment().commitTransactionAndClear(em);

            verify(result.size() == 12, "wrong size; expected 12 - got " + result.size());
            // check for fetch-joined hobbies in query-result employees
            for (Employee emp : result) {
                verify(emp.getHobbies().size() > 0,
                        "fetch-joined hobbies were not attached to the entity after query execution.");
            }

            for (Object emp : result) {
                Employee emp1 = (Employee) emp;
                Employee compareEmp = employeeStore.get(emp1.getId());

                Comparator<Hobby> comp = new Comparator<Hobby>() {
                    @Override
                    public int compare(Hobby o1, Hobby o2) {
                        return String.CASE_INSENSITIVE_ORDER.compare(o1.getDescription(), o2.getDescription());
                    }
                };

                Collections.sort(compareEmp.getHobbies(), comp);
                Collections.reverse(compareEmp.getHobbies());

                for (int i = 0; i < emp1.getHobbies().size(); i++) {
                    if (!compareEmp.getHobbies().get(i).getId().equals(emp1.getHobbies().get(i).getId())) {
                        verify(false, "did not find expected hobby " + emp1.getHobbies().get(i).getId()
                                + " attached to returned entity employee with ID " + emp1.getId() + " instead employee "
                                + compareEmp.getId() + " with hobby " + compareEmp.getHobbies().get(i).getId() + " was found.");

                    }
                }
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDistinctJoinFetchHobbiesWithMultipleSelectItems() throws SQLException {
        init();
        EntityManager em = getEnvironment().getEntityManager();
        try {
            Query query = em.createQuery("select distinct e, e from Employee e join fetch e.hobbies");
            List<?> result = query.getResultList();
            verify(result.size() == 12, "wrong size; expected 12 - got " + result.size());
        } finally {
            closeEntityManager(em);
        }
    }

}
