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

package org.eclipse.persistence.testing.tests.wdf.jpa1.foreignkeys;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Car;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Cop;
import org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Criminal;
import org.eclipse.persistence.testing.models.wdf.jpa1.jpql.Informer;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.CascadingNode;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.CascadingNodeDescription;
import org.eclipse.persistence.testing.models.wdf.jpa1.node.Node;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestForeignKeys extends JPA1Base {

    private static void execute(final DataSource dataSource, final String stmtText, final boolean ignore) throws SQLException {
        final Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(true);
            final Statement statement = connection.createStatement();
            try {
                statement.executeUpdate(stmtText);
            } catch (SQLException se) {
                if (!ignore) {
                    throw se;
                }
            } finally {
                statement.close();
            }
        } finally {
            connection.close();
        }
    }

    @Before
    @After
    public void clean() throws SQLException {
        deleteRows(getEnvironment().getDataSource());
    }

    private static void deleteRows(DataSource dataSource) throws SQLException {
        execute(dataSource, "delete from TMP_VEHICLE", true);
        execute(dataSource, "delete from TMP_PROJECT_DETAILS", true);
        execute(dataSource, "delete from TMP_EMP_PROJECT", true);
        execute(dataSource, "delete from TMP_EMP", true);
        execute(dataSource, "delete from TMP_DEP", true);
        execute(dataSource, "delete from TMP_PROJECT", true);
        execute(dataSource, "delete from TMP_NODE", true);
        execute(dataSource, "delete from TMP_CASC_NODE_DESC", true);
        execute(dataSource, "delete from TMP_CASC_NODE", true);

        execute(dataSource, "delete from TMP_COP_TMP_CRIMINAL", true);
        execute(dataSource, "delete from TMP_CRIMINAL_TMP_CRIMINAL", true);
        execute(dataSource, "delete from TMP_COP_TMP_INFORMER", true);
        execute(dataSource, "delete from TMP_INFORMER_TMP_COP", true);
        execute(dataSource, "delete from TMP_INFORMER", true);
        execute(dataSource, "delete from TMP_CRIMINAL", true);
        execute(dataSource, "delete from TMP_COP", true);
    }

    @Test
    public void testFKBasic() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.getTransaction().begin();
            Department dep = new Department(1, "Kornfeld");
            Employee emp = new Employee(105, "Solarfried", "Sonnenblume", dep);
            em.persist(dep);
            em.persist(emp);
            em.flush();
            em.remove(emp);
            em.remove(dep);
            em.getTransaction().commit();
        } finally {
            closeEntityManager(em);
        }
    }

    // foreignKeys on jointable(employee_project) and secondary
    // table(project_details)
    @Test
    public void testRelationNM() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.getTransaction().begin();
            Employee emp = new Employee(1, "first", "last", null);
            Project pro1 = new Project("foreignKeys");
            pro1.setPlannedDays(17); // second tab
            Set<Project> projects = new HashSet<Project>();
            projects.add(pro1);
            emp.setProjects(projects);

            em.persist(pro1);
            em.persist(emp);
            em.flush();
            Project pro2 = new Project("blah");
            projects.add(pro2);
            em.persist(pro2);
            em.getTransaction().commit();

            em.getTransaction().begin();
            emp = em.find(Employee.class, 1);
            Assert.assertEquals("wrong number of Projects", emp.getProjects().size(), 2);
            projects = emp.getProjects();
            pro1 = projects.iterator().next();
            pro2 = projects.iterator().next();
            projects.remove(pro1);
            projects.remove(pro2);
            em.remove(emp);
            em.remove(pro1);
            em.remove(pro2);
            em.getTransaction().commit();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    // delete not sorted by PK
    public void testSelfishKey() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.getTransaction().begin();
            Node parent = new Node(1, null);
            Node child1 = new Node(2, parent);
            Node child2 = new Node(3, parent);
            em.persist(child1);
            em.persist(child2);
            em.persist(parent);
            em.getTransaction().commit();

            em.getTransaction().begin();
            parent = em.find(Node.class, 1);
            child1 = em.find(Node.class, 2);
            child2 = em.find(Node.class, 3);
            em.remove(parent);
            em.remove(child1);
            em.remove(child2);
            em.getTransaction().commit();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testFKException() {
        /*
         * DatabaseVendor vendor = ConnectionHelper.getDatabaseVendor(dataSource); if (vendor == DatabaseVendor.ORACLE) {
         * return; // FK check at commit time will be fine } if (vendor == DatabaseVendor.DB2_UDB_AS400) { return; // insert
         * batch atomic }
         */
        final EntityManager em = getEnvironment().getEntityManager();
        boolean caught = false;
        try {
            em.getTransaction().begin();
            Node eins = new Node(10, null);
            Node zwei = new Node(20, eins);
            Node drei = new Node(30, zwei);
            eins.setParent(drei);
            em.persist(eins);
            em.persist(zwei);
            em.persist(drei);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            if (e.getCause().getCause() instanceof SQLException) {
                caught = true;
            }
        } finally {
            Assert.assertTrue("missing SQLException", caught);
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    // delete not sorted by PK
    public void testCriminal() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.getTransaction().begin();

            Criminal criminal1 = new Criminal();
            criminal1.setId(1);
            Set<Criminal> criminalSet = new HashSet<Criminal>();
            criminalSet.add(criminal1);

            Cop cop1 = new Cop();
            cop1.setId(101);
            Cop cop2 = new Cop();
            cop2.setId(202);
            Set<Cop> copSet = new HashSet<Cop>();
            copSet.add(cop1);
            copSet.add(cop2);

            cop1.setPartner(cop2);
            cop1.setAttachedCriminals(criminalSet);

            criminal1.setAttachedCop(cop1);

            em.persist(cop1);
            em.persist(cop2);
            em.persist(criminal1);

            em.flush();

            em.remove(cop1);
            em.remove(cop2);
            em.remove(criminal1);

            em.getTransaction().commit();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testCycle() {

        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.getTransaction().begin();

            Criminal criminal1 = new Criminal();
            criminal1.setId(1);
            Criminal criminal2 = new Criminal();
            criminal2.setId(2);
            Set<Criminal> criminalSet = new HashSet<Criminal>();
            criminalSet.add(criminal1);
            criminalSet.add(criminal2);

            Informer informer1 = new Informer();
            informer1.setId(10);
            Informer informer2 = new Informer();
            informer2.setId(20);
            Set<Informer> informerSet = new HashSet<Informer>();
            informerSet.add(informer1);
            informerSet.add(informer2);

            Cop cop1 = new Cop();
            cop1.setId(100);
            Cop cop2 = new Cop();
            cop2.setId(200);
            Set<Cop> copSet = new HashSet<Cop>();
            copSet.add(cop1);
            copSet.add(cop2);

            cop1.setPartner(cop2);
            cop1.setAttachedCriminals(criminalSet);
            cop1.setInformers(informerSet);
            criminal1.setAttachedCop(cop1);
            criminal2.setAttachedCop(cop1);
            informer1.setInformingCops(copSet);
            informer2.setInformingCops(copSet);

            em.persist(cop1);
            em.persist(cop2);
            em.persist(criminal1);
            em.persist(criminal2);
            em.persist(informer1);
            em.persist(informer2);

            em.flush();

            em.remove(cop1);
            em.remove(cop2);
            em.remove(criminal1);
            em.remove(criminal2);
            em.remove(informer1);
            em.remove(informer2);

            em.getTransaction().commit();
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCascading() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            CascadingNode cNode = new CascadingNode(1, null);
            CascadingNodeDescription desc = new CascadingNodeDescription(7, cNode, "blah blah");
            cNode.setDescription(desc);

            em.getTransaction().begin();
            em.persist(cNode);
            // cascading desc
            em.getTransaction().commit();

            desc = em.find(CascadingNodeDescription.class, 7);
            Assert.assertNotNull("missing description", desc);

            em.getTransaction().begin();
            em.remove(cNode);
            // cascading desc
            em.getTransaction().commit();

            desc = em.find(CascadingNodeDescription.class, 7);
            Assert.assertEquals("to much description", desc, null);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testInsertInRelation() {
        final EntityManager em = getEnvironment().getEntityManager();
        boolean caught = false;
        try {
            em.getTransaction().begin();
            Department dep = new Department(2, "Kornfeld");
            Employee emp = new Employee(106, "Solarfried", "Sonnenblume", dep);
            em.persist(emp);
            em.persist(dep);
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            caught = true;
        } finally {
            Assert.assertEquals("wrong Exception", caught, false);
            closeEntityManager(em);
        }
    }

    @Test
    public void testRemoveInRelation() {
        final EntityManager em = getEnvironment().getEntityManager();
        boolean caught = false;
        try {
            em.getTransaction().begin();
            Department dep = new Department(3, "Kornfeld");
            Employee emp = new Employee(107, "Solarfried", "Sonnenblume", dep);
            em.persist(dep);
            em.persist(emp);
            em.flush();
            em.remove(dep);
            em.remove(emp);
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            caught = true;
        } finally {
            Assert.assertEquals("wrong Exception", caught, false);
            closeEntityManager(em);
        }
    }

    @Test
    public void testInsertWithJointable() {
        final EntityManager em = getEnvironment().getEntityManager();
        boolean caught = false;
        try {
            Project pro = new Project("WrongInsertWithJointable");
            pro.setPlannedDays(18);
            Employee emp = new Employee(106, "Solarfried", "Sonnenblume", null);
            Set<Project> projects = new HashSet<Project>();
            projects.add(pro);
            emp.setProjects(projects);

            em.getTransaction().begin();
            em.persist(emp);
            em.persist(pro);
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            caught = true;
        } finally {
            Assert.assertEquals("wrong Exception", caught, false);
            closeEntityManager(em);
        }
    }

    @Test
    public void testRemoveWithJointable() {
        final EntityManager em = getEnvironment().getEntityManager();
        boolean caught = false;
        try {
            Project pro = new Project("testWrongRemoveWithJointable");
            pro.setPlannedDays(19);
            Employee emp = new Employee(107, "Solarfried", "Sonnenblume", null);
            Set<Project> projects = new HashSet<Project>();
            projects.add(pro);
            emp.setProjects(projects);

            em.getTransaction().begin();
            em.persist(pro);
            em.persist(emp);
            em.flush();
            em.remove(pro);
            em.remove(emp);
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            caught = true;
        } finally {
            Assert.assertEquals("wrong Exception", caught, false);
            closeEntityManager(em);
        }
    }

    @Test
    public void testInheritance() {
        final EntityManager em = getEnvironment().getEntityManager();
        try {
            em.getTransaction().begin();
            Employee emp1 = new Employee(321, "Willi", "Wusel", null);
            Employee emp2 = new Employee(322, "Manni", "Manta", null);
            Car car1 = new Car();
            Car car2 = new Car();

            car1.setDriver(emp1);
            emp2.setAutomobile(car1);
            car2.setDriver(emp2);

            em.persist(car1);
            em.persist(car2);
            em.persist(emp1);
            em.persist(emp2);

            em.flush();

            em.remove(car1);
            em.remove(car2);
            em.remove(emp1);
            em.remove(emp2);

            em.getTransaction().commit();
        } finally {
            closeEntityManager(em);
        }
    }
}
