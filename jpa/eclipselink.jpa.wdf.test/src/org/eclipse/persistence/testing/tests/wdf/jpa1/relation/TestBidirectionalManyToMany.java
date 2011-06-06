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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.eclipse.persistence.testing.framework.wdf.AbstractBaseTest;
import org.eclipse.persistence.testing.framework.wdf.Bugzilla;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Bicycle;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Item_Byte;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.TravelProfile;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Vehicle;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestBidirectionalManyToMany extends JPA1Base {

    private static final int HANS_ID_VALUE = 1;
    private static final Integer HANS_ID = new Integer(HANS_ID_VALUE);
    private static final Set<Pair> HANS_SET = new HashSet<Pair>();
    private static final int FRED_ID_VALUE = 2;
    private static final Integer FRED_ID = new Integer(FRED_ID_VALUE);
    private static final Set<Pair> FRED_SET = new HashSet<Pair>();
    private static final Set<Pair> SEED_SET = new HashSet<Pair>();
    private static final Project PUHLEN = new Project("G\u00fcrteltiere puhlen");
    private static final Project PINSELN = new Project("B\u00e4uche pinseln");
    private static final Project FALTEN = new Project("Zitronen falten");
    private static final Project ZAEHLEN = new Project("Erbsen z\u00e4hlen");
    private static final Project LINKEN = new Project("Eclipse Linken");

    @Before
    public void initData() throws SQLException {
        /*
         * 5 Projects:
         */
        clearAllTables();
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = new Department(17, "diverses");
            em.persist(dep);
            em.persist(PUHLEN);
            em.persist(PINSELN);
            em.persist(FALTEN);
            em.persist(ZAEHLEN);
            em.persist(LINKEN);
            Employee hans = new Employee(HANS_ID_VALUE, "Hans", "Wurst", dep);
            Set<Project> hansProjects = new HashSet<Project>();
            hansProjects.add(PUHLEN);
            hansProjects.add(PINSELN);
            hansProjects.add(FALTEN);
            hans.setProjects(hansProjects);
            Employee fred = new Employee(FRED_ID_VALUE, "Fred von", "Jupiter", dep);
            Set<Project> fredProjects = new HashSet<Project>();
            fredProjects.add(FALTEN);
            fredProjects.add(ZAEHLEN);
            fred.setProjects(fredProjects);
            em.persist(hans);
            em.persist(fred);
            env.commitTransactionAndClear(em);
            HANS_SET.clear();
            for (final Project element : hansProjects) {
                HANS_SET.add(new Pair(HANS_ID_VALUE, element.getId().intValue()));
            }
            FRED_SET.clear();
            for (final Project element : fredProjects) {
                FRED_SET.add(new Pair(FRED_ID_VALUE, element.getId().intValue()));
            }
            SEED_SET.clear();
            SEED_SET.addAll(HANS_SET);
            SEED_SET.addAll(FRED_SET);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testUnchanged() throws SQLException {
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
            checkJoinTable(SEED_SET);
            // 2. touch the projects
            env.beginTransaction(em);
            emp = em.find(Employee.class, HANS_ID);
            emp.getProjects().size();
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
            checkJoinTable(SEED_SET);
            // 3. trivial update
            env.beginTransaction(em);
            emp = em.find(Employee.class, HANS_ID);
            Set<Project> projects = emp.getProjects();
            emp.setProjects(new HashSet<Project>(projects));
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(!emp.postUpdateWasCalled(), "postUpdate was called");
            checkJoinTable(SEED_SET);
        } finally {
            closeEntityManager(em);
        }
    }

    private void checkJoinTable(final Set<Pair> expected) throws SQLException {
        DataSource ds = getEnvironment().getDataSource();
        Connection conn = ds.getConnection();
        Set<Pair> actual = new HashSet<Pair>();
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT EMP_ID, PROJECT_ID FROM TMP_EMP_PROJECT");
            try {
                ResultSet rs = pstmt.executeQuery();
                try {
                    while (rs.next()) {
                        actual.add(new Pair(rs.getInt("EMP_ID"), rs.getInt("PROJECT_ID")));
                    }
                } finally {
                    rs.close();
                }
            } finally {
                pstmt.close();
            }
        } finally {
            conn.close();
        }
        if (expected.size() != actual.size()) {
            for (Iterator iter = expected.iterator(); iter.hasNext();) {
                @SuppressWarnings("unused")
                Pair element = (Pair) iter.next();
            }
            for (Iterator iter = actual.iterator(); iter.hasNext();) {
                @SuppressWarnings("unused")
                Pair element = (Pair) iter.next();
            }
            flop("actual set has wrong size " + actual.size() + " expected: " + expected.size());
        } else {
            verify(true, "");
        }
        verify(expected.containsAll(actual), "actual set contains some elements missing in the expected set");
        verify(actual.containsAll(expected), "expected set contains some elements missing in the actual set");
    }

    @Test
    public void testDeleteEmployee() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, HANS_ID);
            verify(emp != null, "employee not found");
            em.remove(emp);
            env.commitTransactionAndClear(em);
            checkJoinTable(FRED_SET);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDeleteNonSharedProject() throws SQLException {
        // remove a project that is not shared between employees:
        // 1) remove project from set of projects of employee
        // 2) remove employee from set of employees of project
        // 3) remove the project from the database
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            final int removeId = PUHLEN.getId().intValue();
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, HANS_ID);
            verify(emp != null, "employee not found");
            Set projects = emp.getProjects();
            verify(projects != null, "projects are null");
            verify(projects.size() == 3, "not exactly 3 projects but " + projects.size());
            Iterator iter = projects.iterator();
            while (iter.hasNext()) {
                Project project = (Project) iter.next();
                if (project.getId().intValue() == removeId) {
                    Set employeesOfProject = project.getEmployees();
                    employeesOfProject.remove(emp);
                    em.remove(project);
                    iter.remove();
                }
            }
            verify(projects.size() == 2, "no project removed");
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(emp.postUpdateWasCalled(), "post update was not called");
            Set<Pair> expected = new HashSet<Pair>(SEED_SET);
            expected.remove(new Pair(HANS_ID_VALUE, removeId));
            checkJoinTable(expected);
            env.beginTransaction(em);
            emp = em.find(Employee.class, HANS_ID);
            projects = emp.getProjects();
            verify(projects.size() == 2, "not exactly 2 projects but " + projects.size());
            Object object = em.find(Project.class, new Integer(removeId));
            verify(object == null, "project found");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDeleteSharedProject() throws SQLException {
        // remove a project that is shared between employees:
        // 1) remove project from set of projects of employee
        // 2) remove employee from set of employees of project
        // 3) don't remove the project from the database
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            final int REMOVE_ID = FALTEN.getId().intValue();
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, HANS_ID);
            verify(emp != null, "employee not found");
            Set projects = emp.getProjects();
            verify(projects != null, "projects are null");
            verify(projects.size() == 3, "not exactly 3 projects but " + projects.size());
            Iterator iter = projects.iterator();
            while (iter.hasNext()) {
                Project project = (Project) iter.next();
                if (project.getId().intValue() == REMOVE_ID) {
                    Set employeesOfProject = project.getEmployees();
                    employeesOfProject.remove(emp);
                    iter.remove();
                }
            }
            verify(projects.size() == 2, "no project removed");
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(emp.postUpdateWasCalled(), "post update was not called");
            Set<Pair> expected = new HashSet<Pair>(SEED_SET);
            expected.remove(new Pair(HANS_ID_VALUE, REMOVE_ID));
            checkJoinTable(expected);
            env.beginTransaction(em);
            emp = em.find(Employee.class, HANS_ID);
            projects = emp.getProjects();
            verify(projects.size() == 2, "not exactly 2 projects but " + projects.size());
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testAdd() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            final int newId;
            Employee emp = em.find(Employee.class, HANS_ID);
            verify(emp != null, "employee not found");
            Set<Project> projects = emp.getProjects();
            Project p6 = new Project("Nasen bohren");
            em.persist(p6);
            newId = p6.getId().intValue();
            projects.add(p6);
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(emp.postUpdateWasCalled(), "post update was not called");
            Set<Pair> expected = new HashSet<Pair>(SEED_SET);
            expected.add(new Pair(HANS_ID_VALUE, newId));
            checkJoinTable(expected);
            env.beginTransaction(em);
            emp = em.find(Employee.class, HANS_ID);
            projects = emp.getProjects();
            verify(projects.size() == 4, "not exactly 4 projects but " + projects.size());
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testExchange() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            final int newId;
            env.beginTransaction(em);
            Employee emp = em.find(Employee.class, HANS_ID);
            verify(emp != null, "employee not found");
            Set<Project> projects = emp.getProjects();
            Iterator iter = projects.iterator();
            Project project = (Project) iter.next();
            int removedId = project.getId().intValue();
            // there are no managed relationships -> we have to remove the projects on both sides
            em.remove(project);
            iter.remove();
            Project p7 = new Project("Ohren wacklen");
            em.persist(p7);
            newId = p7.getId().intValue();
            projects.add(p7);
            emp.clearPostUpdate();
            env.commitTransactionAndClear(em);
            verify(emp.postUpdateWasCalled(), "post update was not called");
            Set<Pair> expected = new HashSet<Pair>(SEED_SET);
            expected.remove(new Pair(HANS_ID_VALUE, removedId));
            expected.add(new Pair(HANS_ID_VALUE, newId));
            checkJoinTable(expected);
            env.beginTransaction(em);
            emp = em.find(Employee.class, HANS_ID);
            projects = emp.getProjects();
            verify(projects.size() == 3, "not exactly 3 projects but " + projects.size());
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    private void verifyEmployees(EntityManager em, int id, int size) {
        Project project = em.find(Project.class, new Integer(id));
        verify(project != null, "project not found");
        Set employees = project.getEmployees();
        verify(employees.size() == size, "wrong number of employees: " + employees.size() + " expected: " + size);
    }

    @Test
    public void testGetEmployees() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            verifyEmployees(em, LINKEN.getId().intValue(), 0);
            verifyEmployees(em, PUHLEN.getId().intValue(), 1);
            verifyEmployees(em, FALTEN.getId().intValue(), 2);
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCopyProjectsToNew() throws SQLException {
        // copy all projects from hans to a new employee with out actually touching them
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            final int newId = 19;
            env.beginTransaction(em);
            Employee hans = em.find(Employee.class, HANS_ID);
            Employee paul = new Employee(19, "Paul", "Knack", null);
            paul.setProjects(hans.getProjects());
            em.persist(paul);
            env.commitTransactionAndClear(em);
            Set<Pair> expected = new HashSet<Pair>(SEED_SET);
            for (Pair pair : HANS_SET) {
                expected.add(new Pair(newId, pair.getProjectId()));
            }
            checkJoinTable(expected);
            env.beginTransaction(em);
            paul = em.find(Employee.class, new Integer(newId));
            verify(paul.getProjects().size() == HANS_SET.size(), "Paul has wrong number of projects");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Bugzilla(bugid=300503)
    @Test
    public void testCopyProjectsToExisting() throws SQLException {
        // copy all projects from hans to a new employee with out actually touching them
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee hans = em.find(Employee.class, HANS_ID);
            Employee fred = em.find(Employee.class, FRED_ID);
            fred.setProjects(hans.getProjects());
            env.commitTransactionAndClear(em);
            Set<Pair> expected = new HashSet<Pair>(SEED_SET);
            expected.removeAll(FRED_SET);
            for (Pair pair : HANS_SET) {
                expected.add(new Pair(FRED_ID_VALUE, pair.getProjectId()));
            }
            checkJoinTable(expected);
            env.beginTransaction(em);
            fred = em.find(Employee.class, FRED_ID);
            verify(fred.getProjects().size() == HANS_SET.size(), "Paul has wrong number of projects");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCopyProjectsToExistingTouching() throws SQLException {
        // copy all projects from hans to a new employee with out actually touching them
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee hans = em.find(Employee.class, HANS_ID);
            Employee fred = em.find(Employee.class, FRED_ID);
            hans.getProjects().size();
            fred.setProjects(hans.getProjects());
            env.commitTransactionAndClear(em);
            Set<Pair> expected = new HashSet<Pair>(SEED_SET);
            expected.removeAll(FRED_SET);
            for (Pair pair : HANS_SET) {
                expected.add(new Pair(FRED_ID_VALUE, pair.getProjectId()));
            }
            checkJoinTable(expected);
            env.beginTransaction(em);
            fred = em.find(Employee.class, FRED_ID);
            verify(fred.getProjects().size() == HANS_SET.size(), "Paul has wrong number of projects");
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testGuidCollection() throws SQLException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        byte[] id1 = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        byte[] id2 = new byte[] { 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
        try {
            TravelProfile profile1 = new TravelProfile(id1, true, "neverComeBack");
            TravelProfile profile2 = new TravelProfile(id2, true, "flyAway");
            Set<TravelProfile> profileSet = new HashSet<TravelProfile>();
            profileSet.add(profile1);
            profileSet.add(profile2);

            Vehicle vehicle = new Vehicle();
            Set<Vehicle> vehicleSet = new HashSet<Vehicle>();
            vehicleSet.add(vehicle);

            profile1.setVehicles(vehicleSet);
            profile2.setVehicles(vehicleSet);
            vehicle.setProfiles(profileSet);

            env.beginTransaction(em);
            em.persist(profile1);
            em.persist(profile2);
            em.persist(vehicle);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testByteItem() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        byte[] id1 = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        try {
            env.beginTransaction(em);
            Item_Byte item = new Item_Byte(id1, "a", "b", "c");
            item.addAttr("key1", "value1");
            em.persist(item);
            env.commitTransactionAndClear(em);
            item = em.find(Item_Byte.class, id1);
            item.getAttributes();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @Bugzilla(bugid=338783, databaseNames={"org.eclipse.persistence.platform.database.oracle.Oracle10Platform", 
                                           "org.eclipse.persistence.platform.database.oracle.Oracle11Platform"})
    public void testCascadeMerge() throws IOException, ClassNotFoundException {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();

        try {
            env.beginTransaction(em);
            em.createQuery("delete from Employee").executeUpdate();
            em.createQuery("delete from Vehicle").executeUpdate();
            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            Bicycle bicycle = new Bicycle();
            em.persist(bicycle);

            Short bikeId = bicycle.getId();
            env.commitTransaction(em);

            bicycle = em.find(Bicycle.class, bikeId);
            em.clear();

            bicycle = AbstractBaseTest.serializeDeserialize(bicycle);
            // bicycle should be detached

            Employee emp = new Employee(9999, "Robbi", "Tobbi", null);
            emp.clearPostPersist();
            bicycle.setRiders(Collections.singleton(emp));
            env.beginTransaction(em);
            Bicycle mergedBike = em.merge(bicycle);
            env.commitTransactionAndClear(em);

            Employee mergedEmp = mergedBike.getRiders().iterator().next();

            verify(mergedEmp.postPersistWasCalled(), "merge did not cascade to employee");

            verify(em.find(Employee.class, 9999) != null, "employee not found");

        } finally {
            closeEntityManager(em);
        }
    }

    private static class Pair {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pair) {
                Pair other = (Pair) obj;
                return other.empId == empId && other.projectId == projectId;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return empId + 17 * projectId;
        }

        /**
         * @return Returns the empId.
         */
        @SuppressWarnings("unused")
        public int getEmpId() {
            return empId;
        }

        /**
         * @return Returns the projectId.
         */
        public int getProjectId() {
            return projectId;
        }

        final int empId;
        final int projectId;

        Pair(int e, int r) {
            empId = e;
            projectId = r;
        }
    }
}
