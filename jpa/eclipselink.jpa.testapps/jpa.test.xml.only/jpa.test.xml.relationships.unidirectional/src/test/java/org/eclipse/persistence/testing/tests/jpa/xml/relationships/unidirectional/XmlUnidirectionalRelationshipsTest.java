/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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


package org.eclipse.persistence.testing.tests.jpa.xml.relationships.unidirectional;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.unidirectional.Address;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.unidirectional.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.unidirectional.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.unidirectional.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.unidirectional.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.unidirectional.Project;

import java.util.Collection;
import java.util.Iterator;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class XmlUnidirectionalRelationshipsTest extends JUnitTestCase {

    protected static Integer otoEmployeeId;
    protected static Integer otoProjectId;
    protected static Integer otmEmployeeId;
    protected static Integer otmPhone1Id;
    protected static Integer otmPhone2Id;
    protected static Integer mtoEmployee1Id;
    protected static Integer mtoEmployee2Id;
    protected static Integer mtoAddressId;
    protected static Integer mtmEmployeeId;
    protected static Integer mtmProject1Id;
    protected static Integer mtmProject2Id;

    public XmlUnidirectionalRelationshipsTest() {
        super();
    }

    public XmlUnidirectionalRelationshipsTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Unidirectional Relationships Model");
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testSetup"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalOneToOneCreate"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalOneToOneRead"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalOneToOneDeleteNonowning"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalOneToOneDeleteOwning"));

        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalOneToManyCreate"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalOneToManyRead"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalOneToManyDeleteNonowning"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalOneToManyDeleteOwning"));

        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalManyToOneCreate"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalManyToOneRead"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalManyToOneDeleteNonowning"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalManyToOneDeleteOwning"));

        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalManyToManyCreate"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalManyToManyRead"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalManyToManyDeleteNonowning"));
        suite.addTest(new XmlUnidirectionalRelationshipsTest("testUnidirectionalManyToManyDeleteOwning"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = JUnitTestCase.getServerSession();
        new AdvancedTableCreator().replaceTables(session);
        clearCache();
    }

    public void testUnidirectionalOneToOneCreate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = ModelExamples.employeeExample1();
            Project project = ModelExamples.projectExample1();
            project.setTeamLeader(employee);

            //cascade-persist is not set
            em.persist(employee);
            em.persist(project);
            otoEmployeeId = employee.getId();
            otoProjectId = project.getId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    public void testUnidirectionalOneToOneRead() {
        EntityManager em = createEntityManager();
        Project project = em.find(Project.class, otoProjectId);
        assertSame("Error reading Project", project.getId(), otoProjectId);
        assertSame("Error reading TeamLeader of Project", project.getTeamLeader().getId(), otoEmployeeId);
    }

    public void testUnidirectionalOneToOneDeleteNonowning() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = em.find(Employee.class, otoEmployeeId);
            Project project = em.find(Project.class, otoProjectId);
            assertNotNull(employee);
            assertNotNull(project);
            project.setTeamLeader(null);
            em.remove(employee);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Employee", em.find(Employee.class, otoEmployeeId));
    }

    public void testUnidirectionalOneToOneDeleteOwning() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Project project = em.find(Project.class, otoProjectId);
            assertNotNull(project);
            em.remove(project);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Project", em.find(Project.class, otoProjectId));
    }

    public void testUnidirectionalOneToManyCreate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = ModelExamples.employeeExample1();
            PhoneNumber phone1 = ModelExamples.phoneExample1();
            PhoneNumber phone2 = ModelExamples.phoneExample2();
            employee.addPhoneNumber(phone1);
            employee.addPhoneNumber(phone2);

            //cascade-persist
            em.persist(employee);
            otmEmployeeId = employee.getId();
            otmPhone1Id = phone1.getId();
            otmPhone2Id = phone2.getId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    public void testUnidirectionalOneToManyRead() {
        EntityManager em = createEntityManager();
        Employee employee = em.find(Employee.class, otmEmployeeId);
        assertSame("Error reading Employee", employee.getId(), otmEmployeeId);
        Collection<PhoneNumber> phones = employee.getPhoneNumbers();
        assertEquals("Employee didn't have correct number of phone numbers", 2, phones.size());
        boolean phonesMatched = true;
        for(Iterator<PhoneNumber> ite = phones.iterator(); ite.hasNext();){
          PhoneNumber phone = ite.next();
          if(phone.getId()!=otmPhone1Id && phone.getId()!=otmPhone2Id)
          {
            phonesMatched = false;
          }
        }
        assertTrue("Employee didn't have correct phones", phonesMatched);
    }

    public void testUnidirectionalOneToManyDeleteNonowning() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = em.find(Employee.class, otmEmployeeId);
            PhoneNumber phone = em.find(PhoneNumber.class, otmPhone1Id);
            employee.removePhoneNumber(phone);
            em.remove(phone);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting PhoneNumber", em.find(PhoneNumber.class, otmPhone1Id));
    }

    public void testUnidirectionalOneToManyDeleteOwning() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = em.find(Employee.class, otmEmployeeId);

            //if cascade-all is set, comment this line out
            employee.setPhoneNumbers(null);
            em.remove(employee);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Employee", em.find(Employee.class, otmEmployeeId));
        //if cascade-all is set, uncomment this line
        //assertTrue("The phone should have been deleted.", em.find(PhoneNumber.class, otmPhone2Id) == null);
    }

    public void testUnidirectionalManyToOneCreate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee1 = ModelExamples.employeeExample1();
            Employee employee2 = ModelExamples.employeeExample2();
            Address address = ModelExamples.addressExample1();
            employee1.setAddress(address);
            employee2.setAddress(address);

            //cascade-persist
            em.persist(employee1);
            em.persist(employee2);
            mtoEmployee1Id = employee1.getId();
            mtoEmployee2Id = employee2.getId();
            mtoAddressId = address.getId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    public void testUnidirectionalManyToOneRead() {
        EntityManager em = createEntityManager();
        Employee employee = em.find(Employee.class, mtoEmployee1Id);
        assertSame("Error reading Employee", employee.getId(), mtoEmployee1Id);
        assertSame("Error reading Address", employee.getAddress().getId(), mtoAddressId);
    }

    public void testUnidirectionalManyToOneDeleteNonowning() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee1 = em.find(Employee.class, mtoEmployee1Id);
            Employee employee2 = em.find(Employee.class, mtoEmployee2Id);
            Address address = em.find(Address.class, mtoAddressId);
            employee1.setAddress(null);
            employee2.setAddress(null);
            em.remove(address);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Address", em.find(Address.class, mtoAddressId));
    }

    public void testUnidirectionalManyToOneDeleteOwning() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee1 = em.find(Employee.class, mtoEmployee1Id);
            Employee employee2 = em.find(Employee.class, mtoEmployee2Id);
            em.remove(employee1);
            em.remove(employee2);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Employee", em.find(Employee.class, mtoEmployee1Id));
        assertNull("Error deleting Employee", em.find(Employee.class, mtoEmployee2Id));
    }

    public void testUnidirectionalManyToManyCreate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = ModelExamples.employeeExample1();
            Project project1=ModelExamples.projectExample1();
            Project project2=ModelExamples.projectExample2();
            employee.addProject(project1);
            employee.addProject(project2);

            //cascade-persist
            em.persist(employee);
            mtmEmployeeId = employee.getId();
            mtmProject1Id = project1.getId();
            mtmProject2Id = project2.getId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    public void testUnidirectionalManyToManyRead() {
        EntityManager em = createEntityManager();
        Employee employee = em.find(Employee.class, mtmEmployeeId);
        assertSame("Error reading Employee", employee.getId(), mtmEmployeeId);
        Collection<Project> projects = employee.getProjects();
        assertEquals("Employee didn't have correct number of projects", 2, projects.size());
        boolean projectsMatched = true;
        for(Iterator<Project> ite = projects.iterator(); ite.hasNext();){
          Project project = ite.next();
          if(project.getId()!=mtmProject1Id && project.getId()!=mtmProject2Id)
          {
            projectsMatched = false;
          }
        }
        assertTrue("Employee didn't have correct projects", projectsMatched);
    }

    public void testUnidirectionalManyToManyDeleteNonowning() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = em.find(Employee.class, mtmEmployeeId);
            Project project1 = em.find(Project.class, mtmProject1Id);
            employee.removeProject(project1);
            em.remove(project1);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Project", em.find(Project.class, mtmProject1Id));
    }

    public void testUnidirectionalManyToManyDeleteOwning() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee employee = em.find(Employee.class, mtmEmployeeId);
            employee.setProjects(null);
            em.remove(employee);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Employee", em.find(Employee.class, mtmEmployeeId));
        assertNotNull("The project shouldn't have been deleted.", em.find(Project.class, mtmProject2Id));
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(XmlUnidirectionalRelationshipsTest.suite());
    }
}
