/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     01/25/2011-2.3 Guy Pelletier
//       - 333913: @OrderBy and <order-by/> without arguments should order by primary
package org.eclipse.persistence.testing.tests.jpa.xml.advanced.compositepk;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.CompositePKTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.Cubicle;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.Department;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.DepartmentPK;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.JuniorScientist;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.Scientist;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.ScientistPK;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.derivedid.Administrator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.derivedid.DepartmentAdminRole;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.derivedid.DepartmentAdminRolePK;

import java.util.ArrayList;
import java.util.List;


public class XmlAdvancedCompositePKTest extends JUnitTestCase {
    private static DepartmentPK m_departmentPK;
    private static ScientistPK m_scientist1PK, m_scientist2PK, m_scientist3PK, m_jScientistPK;

    public XmlAdvancedCompositePKTest() {
        super();
    }

    public XmlAdvancedCompositePKTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "default";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlAdvancedCompositePKTest - default");

        suite.addTest(new XmlAdvancedCompositePKTest("testSetup"));
        suite.addTest(new XmlAdvancedCompositePKTest("testOrderBySetting"));
        suite.addTest(new XmlAdvancedCompositePKTest("testCreateDepartment"));
        suite.addTest(new XmlAdvancedCompositePKTest("testCreateScientists"));
        suite.addTest(new XmlAdvancedCompositePKTest("testReadDepartment"));
        suite.addTest(new XmlAdvancedCompositePKTest("testReadJuniorScientist"));
        suite.addTest(new XmlAdvancedCompositePKTest("testAnyAndAll"));
        suite.addTest(new XmlAdvancedCompositePKTest("testDepartmentAdmin"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
        new CompositePKTableCreator().replaceTables(getPersistenceUnitServerSession());
        clearCache();
    }

    /**
     * Verifies that order-by metadata is correctly processed/defaulted.
     */
    public void testOrderBySetting() {
        ServerSession session = getPersistenceUnitServerSession();

        ClassDescriptor departmentDescriptor = session.getDescriptor(Department.class);
        assertNotNull("Department descriptor was not found.", departmentDescriptor);

        DatabaseMapping scientistMapping = departmentDescriptor.getMappingForAttributeName("scientists");
        assertNotNull("Scientist mapping from Department descriptor was not found.", scientistMapping);
        assertTrue("The scientist mapping from the Department descriptor did not have an order by setting.", ((OneToManyMapping) scientistMapping).hasOrderBy());
    }

    public void testCreateDepartment() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            // make sure the department is not left from the previous test run
            em.createQuery("DELETE FROM XMLDepartment d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A'").executeUpdate();
            commitTransaction(em);
        } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw e;
        }
        clearCache();
        closeEntityManager(em);
        em = createEntityManager();
        beginTransaction(em);
        try {

            Department department = new Department();
            department.setName("DEPT A");
            department.setRole("ROLE A");
            department.setLocation("LOCATION A");
            em.persist(department);

            commitTransaction(em);
            m_departmentPK = department.getPK();
        } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw e;
        }
    }

    public void testCreateScientists() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Department department = em.merge(em.find(Department.class, m_departmentPK));

            Cubicle cubicle1 = new Cubicle("G");
            em.persist(cubicle1);

            Scientist scientist1 = new Scientist();
            scientist1.setFirstName("Guy");
            scientist1.setLastName("Pelletier");
            scientist1.setCubicle(cubicle1);
            department.addScientist(scientist1);
            em.persist(scientist1);

            Cubicle cubicle2 = new Cubicle("T");
            em.persist(cubicle2);

            Scientist scientist2 = new Scientist();
            scientist2.setFirstName("Tom");
            scientist2.setLastName("Ware");
            scientist2.setCubicle(cubicle2);
            department.addScientist(scientist2);
            em.persist(scientist2);

            Cubicle cubicle3 = new Cubicle("G");
            em.persist(cubicle3);

            Scientist scientist3 = new Scientist();
            scientist3.setFirstName("Gordon");
            scientist3.setLastName("Yorke");
            scientist3.setCubicle(cubicle3);
            department.addScientist(scientist3);
            em.persist(scientist3);

            Cubicle cubicle4 = new Cubicle("J");
            em.persist(cubicle4);

            JuniorScientist jScientist = new JuniorScientist();
            jScientist.setFirstName("Junior");
            jScientist.setLastName("Sao");
            jScientist.setCubicle(cubicle4);
            department.addScientist(jScientist);
            em.persist(jScientist);

            commitTransaction(em);
            m_scientist1PK = scientist1.getPK();
            m_scientist2PK = scientist2.getPK();
            m_scientist3PK = scientist3.getPK();
            m_jScientistPK = jScientist.getPK();
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    public void testDepartmentAdmin() {
        String location = "Ottawa";
        String depName = "New Product Research";
        String depRole = "R&D new technologies";
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = new Employee("George", "Smith");
            em.persist(emp);

            Administrator adminEmp = new Administrator();
            adminEmp.setContractCompany("George's consulting");
            adminEmp.setEmployee(emp);
            em.persist(adminEmp);

            Department newProductResearch = new Department();
            newProductResearch.setLocation(location);
            newProductResearch.setName(depName);
            newProductResearch.setRole(depRole);
            em.persist(newProductResearch);

            DepartmentAdminRole depAdmin = new DepartmentAdminRole();
            depAdmin.setAdmin(adminEmp);
            depAdmin.setDepartment(newProductResearch);

            em.persist(depAdmin);
            commitTransaction(em);
            org.eclipse.persistence.internal.jpa.EntityManagerImpl emImpl = (org.eclipse.persistence.internal.jpa.EntityManagerImpl) em;
            DepartmentAdminRolePK depAdminPk= new DepartmentAdminRolePK(depName, depRole, location, adminEmp.getEmployee().getId());

            DepartmentAdminRole cacheObject = em.find(DepartmentAdminRole.class, depAdminPk);
            assertNotNull("Find did not return the DepartmentAdminRole", cacheObject);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testReadDepartment() {
        Department department = createEntityManager().find(Department.class, m_departmentPK);

        assertNotNull("Error on reading back the ordered department list.", department);
        assertTrue("The number of scientists were incorrect.", !department.getScientists().isEmpty());
    }

    public void testReadJuniorScientist() {
        JuniorScientist jScientist;

        jScientist = createEntityManager().find(JuniorScientist.class, m_jScientistPK);
        assertNotNull("Error on reading back the junior scientist.", jScientist);
    }

    //bug gf672 - JBQL Select query with IN/ANY in WHERE clause and subselect fails.
    public void testAnyAndAll() {
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            TypedQuery<Scientist> query1 = em.createQuery("SELECT s FROM XMLScientist s WHERE s = ANY (SELECT s2 FROM XMLScientist s2)", Scientist.class);
            List<Scientist> results1 = query1.getResultList();

            TypedQuery<Scientist> query2 = em.createQuery("SELECT s FROM XMLScientist s WHERE s = ALL (SELECT s2 FROM XMLScientist s2)", Scientist.class);
            List<Scientist> results2 = query2.getResultList();

            TypedQuery<Scientist> query3 = em.createQuery("SELECT s FROM XMLScientist s WHERE s.department = ALL (SELECT DISTINCT d FROM XMLDepartment d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A')", Scientist.class);
            List<Scientist> results3 = query3.getResultList();

            TypedQuery<Scientist> query4 = em.createQuery("SELECT s FROM XMLScientist s WHERE s.department = ANY (SELECT DISTINCT d FROM XMLDepartment d JOIN d.scientists ds JOIN ds.cubicle c WHERE c.code = 'G')", Scientist.class);
            List<Scientist> results4 = query4.getResultList();

            // control queries

            TypedQuery<Scientist> controlQuery1 = em.createQuery("SELECT s FROM XMLScientist s", Scientist.class);
            List<Scientist> controlResults1 = controlQuery1.getResultList();

            List<Scientist> controlResults2;
            if(controlResults1.size() == 1) {
                controlResults2 = controlResults1;
            } else {
                controlResults2 = new ArrayList<>();
            }

            TypedQuery<Scientist> controlQuery3 = em.createQuery("SELECT s FROM XMLScientist s JOIN s.department d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A'", Scientist.class);
            List<Scientist> controlResults3 = controlQuery3.getResultList();

            TypedQuery<Scientist> controlQuery4 = em.createQuery("SELECT s FROM XMLScientist s WHERE EXISTS (SELECT DISTINCT d FROM XMLDepartment d JOIN d.scientists ds JOIN ds.cubicle c WHERE c.code = 'G' AND d = s.department)", Scientist.class);
            List<Scientist> controlResults4 = controlQuery4.getResultList();

            //compare results - they should be the same
            compareResults(results1, controlResults1, "query1");
            compareResults(results2, controlResults2, "query2");
            compareResults(results3, controlResults3, "query3");
            compareResults(results4, controlResults4, "query4");
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    protected void compareResults(List<?> results, List<?> controlResults, String testName) {
        if(results.size() != controlResults.size()) {
            fail(testName + ": results.size() = " + results.size() + "; controlResults.size() = " + controlResults.size());
        }
        for (Object s : results) {
            if(!controlResults.contains(s)) {
                fail(testName + ": " + s + "contained in results but not in controlResults");
            }
        }
    }
}
