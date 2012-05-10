/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     01/25/2011-2.3 Guy Pelletier 
 *       - 333913: @OrderBy and <order-by/> without arguments should order by primary
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.advanced.compositepk;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.Query;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.*;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.derivedid.*;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.tests.jpa.TestingProperties;

 
public class AdvancedCompositePKJunitTest extends JUnitTestCase {
    String m_persistenceUnit = "default";
    private static DepartmentPK m_departmentPK;
    private static ScientistPK m_scientist1PK, m_scientist2PK, m_scientist3PK, m_jScientistPK; 
    
    public AdvancedCompositePKJunitTest() {
        super();
    }
    
    public AdvancedCompositePKJunitTest(String name) {
        super(name);
    }
    
    public AdvancedCompositePKJunitTest(String name, String persistenceUnit) {
        super(name);
        m_persistenceUnit = persistenceUnit;
    }
    
    public static Test suite() {
        String ormTesting = TestingProperties.getProperty(TestingProperties.ORM_TESTING, TestingProperties.JPA_ORM_TESTING);
        final String persistenceUnit = ormTesting.equals(TestingProperties.JPA_ORM_TESTING)? "default" : "extended-advanced";
        TestSuite suite = new TestSuite("AdvancedCompositePKJunitTest - " + persistenceUnit);
        
        suite.addTest(new AdvancedCompositePKJunitTest("testSetup", persistenceUnit));
        suite.addTest(new AdvancedCompositePKJunitTest("testOrderBySetting", persistenceUnit));
        suite.addTest(new AdvancedCompositePKJunitTest("testCreateDepartment", persistenceUnit));
        suite.addTest(new AdvancedCompositePKJunitTest("testCreateScientists", persistenceUnit));
        suite.addTest(new AdvancedCompositePKJunitTest("testReadDepartment", persistenceUnit));
        suite.addTest(new AdvancedCompositePKJunitTest("testReadJuniorScientist", persistenceUnit));
        suite.addTest(new AdvancedCompositePKJunitTest("testAnyAndAll", persistenceUnit));
        suite.addTest(new AdvancedCompositePKJunitTest("testDepartmentAdmin", persistenceUnit)); 
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession(m_persistenceUnit));
        new CompositePKTableCreator().replaceTables(JUnitTestCase.getServerSession(m_persistenceUnit));
        clearCache(m_persistenceUnit);
    }
    
    /**
     * Verifies that order-by metadata is correctly processed/defaulted.
     */
    public void testOrderBySetting() {
        ServerSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        
        ClassDescriptor departmentDescriptor = session.getDescriptor(Department.class);
        assertNotNull("Department descriptor was not found.", departmentDescriptor);
        
        DatabaseMapping scientistMapping = departmentDescriptor.getMappingForAttributeName("scientists");
        assertNotNull("Scientist mapping from Department descriptor was not found.", scientistMapping);
        assertTrue("The scientist mapping from the Department descriptor did not have an order by setting.", ((OneToManyMapping) scientistMapping).hasOrderBy());
    }
    
    public void testCreateDepartment() {
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        clearCache(m_persistenceUnit);
        closeEntityManager(em);
        em = createEntityManager(m_persistenceUnit);
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
        EntityManager em = createEntityManager(m_persistenceUnit);
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
            this.assertTrue("Find did not return the DepartmentAdminRole", cacheObject!=null);
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
        Department department = createEntityManager(m_persistenceUnit).find(Department.class, m_departmentPK);
        
        assertTrue("Error on reading back the ordered department list.", department != null);
        assertTrue("The number of scientists were incorrect.", department.getScientists().size() > 0);
    }
    
    public void testReadJuniorScientist() {
        JuniorScientist jScientist;
        
        jScientist = createEntityManager(m_persistenceUnit).find(JuniorScientist.class, m_jScientistPK);
        assertTrue("Error on reading back the junior scientist.", jScientist != null);
    }

    //bug gf672 - JBQL Select query with IN/ANY in WHERE clause and subselect fails.
    public void testAnyAndAll() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        
        beginTransaction(em);
        try {
            Query query1 = em.createQuery("SELECT s FROM XMLScientist s WHERE s = ANY (SELECT s2 FROM XMLScientist s2)");
            List<Scientist> results1 = query1.getResultList();

            Query query2 = em.createQuery("SELECT s FROM XMLScientist s WHERE s = ALL (SELECT s2 FROM XMLScientist s2)");
            List<Scientist> results2 = query2.getResultList();

            Query query3 = em.createQuery("SELECT s FROM XMLScientist s WHERE s.department = ALL (SELECT DISTINCT d FROM XMLDepartment d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A')");
            List<Scientist> results3 = query3.getResultList();

            Query query4 = em.createQuery("SELECT s FROM XMLScientist s WHERE s.department = ANY (SELECT DISTINCT d FROM XMLDepartment d JOIN d.scientists ds JOIN ds.cubicle c WHERE c.code = 'G')");
            List<Scientist> results4 = query4.getResultList();

            // control queries
            
            Query controlQuery1 = em.createQuery("SELECT s FROM XMLScientist s");
            List<Scientist> controlResults1 = controlQuery1.getResultList();
            
            List<Scientist> controlResults2;
            if(controlResults1.size() == 1) {
                controlResults2 = controlResults1;
            } else {
                controlResults2 = new ArrayList<Scientist>();
            }

            Query controlQuery3 = em.createQuery("SELECT s FROM XMLScientist s JOIN s.department d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A'");
            List<Scientist> controlResults3 = controlQuery3.getResultList();
            
            Query controlQuery4 = em.createQuery("SELECT s FROM XMLScientist s WHERE EXISTS (SELECT DISTINCT d FROM XMLDepartment d JOIN d.scientists ds JOIN ds.cubicle c WHERE c.code = 'G' AND d = s.department)");
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
    
    protected void compareResults(List results, List controlResults, String testName) {
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
