/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2024 IBM Corporation. All rights reserved.
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
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
//     10/21/2009-2.0 Guy Pelletier
//       - 290567: mappedbyid support incomplete
//     11/23/2009-2.0 Guy Pelletier
//       - 295790: JPA 2.0 adding @MapsId to one entity causes initialization errors in other entities
//     05/31/2010-2.1 Guy Pelletier
//       - 314941: multiple joinColumns without referenced column names defined, no error
//     01/25/2011-2.3 Guy Pelletier
//       - 333913: @OrderBy and <order-by/> without arguments should order by primary
//     09/11/2017-2.1 Will Dazey
//       - 520387: multiple owning descriptors for an embeddable are not set
package org.eclipse.persistence.testing.tests.jpa.advanced.compositepk;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Author;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Book;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Competency;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.CompositePKTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Cubicle;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.DepartmentPK;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.JuniorScientist;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Office;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.OfficePK;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Scientist;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.ScientistPK;

import java.util.ArrayList;
import java.util.List;

public class AdvancedCompositePKJunitTest extends JUnitTestCase {
    private static DepartmentPK m_departmentPK;
    private static ScientistPK m_scientist1PK, m_scientist2PK, m_scientist3PK, m_jScientistPK;

    public AdvancedCompositePKJunitTest() {
        super();
    }

    public AdvancedCompositePKJunitTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "case_sensitivity_pu";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedCompositePKJunitTest");

        suite.addTest(new AdvancedCompositePKJunitTest("testSetup"));
        suite.addTest(new AdvancedCompositePKJunitTest("testOrderBySetting"));
        suite.addTest(new AdvancedCompositePKJunitTest("testCreateDepartment"));
        suite.addTest(new AdvancedCompositePKJunitTest("testCreateScientists"));
        suite.addTest(new AdvancedCompositePKJunitTest("testReadDepartment"));
        suite.addTest(new AdvancedCompositePKJunitTest("testReadJuniorScientist"));
        suite.addTest(new AdvancedCompositePKJunitTest("testAnyAndAll"));

        suite.addTest(new AdvancedCompositePKJunitTest("testJoinColumnSharesPK"));

        suite.addTest(new AdvancedCompositePKJunitTest("testCopyAggregateCollection"));

        suite.addTest(new AdvancedCompositePKJunitTest("testGetIdentifier"));
        suite.addTest(new AdvancedCompositePKJunitTest("testFailedGetIdenitifier"));
        suite.addTest(new AdvancedCompositePKJunitTest("testGetIdenitifierOnNonEntity"));
        suite.addTest(new AdvancedCompositePKJunitTest("testNestedEmbeddableSequenceGeneration"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new CompositePKTableCreator().replaceTables(getPersistenceUnitServerSession());
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
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
            em.createQuery("DELETE FROM Department d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A'").executeUpdate();
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

            Competency competency = new Competency();
            competency.description = "Manage groups";
            competency.rating = 9;
            department.addCompetency(competency);

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

    //bug gf672 - JPQL Select query with IN/ANY in WHERE clause and subselect fails.
    public void testAnyAndAll() {
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            TypedQuery<Scientist> query1 = em.createQuery("SELECT s FROM Scientist s WHERE s = ANY (SELECT s2 FROM Scientist s2)", Scientist.class);
            List<Scientist> results1 = query1.getResultList();

            TypedQuery<Scientist> query2 = em.createQuery("SELECT s FROM Scientist s WHERE s = ALL (SELECT s2 FROM Scientist s2)", Scientist.class);
            List<Scientist> results2 = query2.getResultList();

            TypedQuery<Scientist> query3 = em.createQuery("SELECT s FROM Scientist s WHERE s.department = ALL (SELECT DISTINCT d FROM Department d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A')", Scientist.class);
            List<Scientist> results3 = query3.getResultList();

            TypedQuery<Scientist> query4 = em.createQuery("SELECT s FROM Scientist s WHERE s.department = ANY (SELECT DISTINCT d FROM Department d JOIN d.scientists ds JOIN ds.cubicle c WHERE c.code = 'G')", Scientist.class);
            List<Scientist> results4 = query4.getResultList();

            // control queries

            TypedQuery<Scientist> controlQuery1 = em.createQuery("SELECT s FROM Scientist s", Scientist.class);
            List<Scientist> controlResults1 = controlQuery1.getResultList();

            List<Scientist> controlResults2;
            if(controlResults1.size() == 1) {
                controlResults2 = controlResults1;
            } else {
                controlResults2 = new ArrayList<>();
            }

            TypedQuery<Scientist> controlQuery3 = em.createQuery("SELECT s FROM Scientist s JOIN s.department d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A'", Scientist.class);
            List<Scientist> controlResults3 = controlQuery3.getResultList();

            TypedQuery<Scientist> controlQuery4 = em.createQuery("SELECT s FROM Scientist s WHERE EXISTS (SELECT DISTINCT d FROM Department d JOIN d.scientists ds JOIN ds.cubicle c WHERE c.code = 'G' AND d = s.department)", Scientist.class);
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

    public void testGetIdentifier(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            DepartmentPK pk = new DepartmentPK("DEPT B", "ROLE B", "LOCATION B");
            Department department = new Department();
            department.setName("DEPT B");
            department.setRole("ROLE B");
            department.setLocation("LOCATION B");
            em.persist(department);
            em.flush();

            PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
            assertEquals("Got an incorrect id from persistenceUtil.getIdentifier()", pk, util.getIdentifier(department));
        } finally {
            rollbackTransaction(em);
        }
    }

    public void testJoinColumnSharesPK(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        org.eclipse.persistence.descriptors.ClassDescriptor descriptor = getPersistenceUnitServerSession().getDescriptor(Office.class);
        try{
            Department department = new Department();
            department.setName("DEPT B");
            department.setRole("ROLE B");
            department.setLocation("LOCATION B");
            em.persist(department);

            Office office = new Office();
            office.setId(1);
            office.setLocation("LOCATION B");
            office.setDepartment(department);
            em.persist(office);
            department.getOffices().add(office);
            em.flush();

            clearCache();

            office = em.find(Office.class, new OfficePK(1, "LOCATION B"));
            department = em.find(Department.class, new DepartmentPK("DEPT B", "ROLE B", "LOCATION B"));

            assertNotNull("Office's department not properly persisted", office.getDepartment());
            assertTrue("Department's offices not properly persisted", !department.getOffices().isEmpty());


        } catch (Exception e){
            fail("Exception thrown while inserting an object with a read-only column in a foreign key." + e);
        } finally {
            rollbackTransaction(em);
        }
    }

    // Bug 406957 - Copy fails on AggregateCollectionMapping and on map with @MapKeyColumn
    public void testCopyAggregateCollection() {
        Department department = new Department();
        department.setName("DEPT AggregateCollection");
        department.setRole("ROLE AggregateCollection");
        department.setLocation("LOCATION AggregateCollection");

        Competency competency = new Competency();
        competency.description = "Manage groups";
        competency.rating = 9;
        department.addCompetency(competency);

        EntityManager em = createEntityManager();
        CopyGroup privatelyOwned = new CopyGroup();
        Department departmentCopy = (Department)JpaHelper.getEntityManager(em).copy(department, privatelyOwned);
        if (departmentCopy.getCompetencies().size() != department.getCompetencies().size()) {
            fail("departmentCopy.getCompetencies().size() = " + departmentCopy.getCompetencies().size() + "; "+department.getCompetencies().size()+" was expected");
        }
        if (departmentCopy.getCompetencies() == department.getCompetencies()) {
            fail("departmentCopy.getCompetencies() == department.getCompetencies()");
        }
        Competency copmetencyCopy = departmentCopy.getCompetencies().iterator().next();
        if (!competency.description.equals(copmetencyCopy.description)) {
            fail("competency.descripton = " + competency.description +"; but copmetencyCopy.description = " + copmetencyCopy.description);
        }
    }

    // bug 409579
    public void testFailedGetIdenitifier(){
        EntityManagerFactory factory = getEntityManagerFactory();
        Cubicle cube = new Cubicle();
        cube.setId(1);
        cube.setCode("a");
        try{
            factory.getPersistenceUnitUtil().getIdentifier(cube);
        } catch (PersistenceException e){
            return;
        }
        fail("Exception not thrown for call to getIdentifier when empty constructor not available.");
    }

    // bug 421557
    public void testGetIdenitifierOnNonEntity(){
        EntityManagerFactory factory = getEntityManagerFactory();
        Object nonEntity = new Object();
        try{
            factory.getPersistenceUnitUtil().getIdentifier(nonEntity);
        } catch (IllegalArgumentException e){
            return;
        }
        fail("IllegalArgumentException not thrown for call to getIdentifier with a non-entity class.");
    }

    // bug 520387
    public void testNestedEmbeddableSequenceGeneration(){
        if(!supportsSequenceObjects()) {
            return;
        }

        Book b = new Book("Book1");
        Author a = new Author("Author1");

        EntityManager em = createEntityManager();
        beginTransaction(em);
        b = em.merge(b);
        a = em.merge(a);

        assertTrue("The PK value for "+ b.getClass() +" (" + b.getId().getNumberId().getValue() + ") is not sequence generated", (b.getId().getNumberId().getValue() >= 1000L));
        assertTrue("The PK value for "+ a.getClass() +" (" + a.getId().getNumberId().getValue() + ") is not sequence generated", (a.getId().getNumberId().getValue() >= 1000L));

        rollbackTransaction(em);
    }
}
