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
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     11/23/2009-2.0 Guy Pelletier 
 *       - 295790: JPA 2.0 adding @MapsId to one entity causes initialization errors in other entities
 *     05/31/2010-2.1 Guy Pelletier 
 *       - 314941: multiple joinColumns without referenced column names defined, no error
 *     01/25/2011-2.3 Guy Pelletier 
 *       - 333913: @OrderBy and <order-by/> without arguments should order by primary
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced.compositepk;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Competency;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Cubicle;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.JuniorScientist;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Office;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.OfficePK;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Scientist;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.ScientistPK;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.DepartmentPK;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.CompositePKTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.AdminPool;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Bookie;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.BrigadierGeneral;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Captain;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.CaptainId;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.CellNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.CellNumberPK;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Corporal;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.CorporalId;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.DepartmentAdminRole;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.DepartmentAdminRolePK;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Administrator;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.General;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Lackey;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.LackeyCrewMember;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.LackeyCrewMemberId;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Lieutenant;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.LieutenantGeneral;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.LieutenantId;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Major;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.MajorGeneral;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.MajorId;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.MasterCorporal;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.MasterCorporalClone;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.MasterCorporalId;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.OfficerCadet;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Private;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.PrivateId;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Sargeant;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.SecondLieutenant;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.nested.GolfClub;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.nested.GolfClubHead;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.nested.GolfClubOrder;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.nested.GolfClubShaft;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;

public class AdvancedCompositePKJunitTest extends JUnitTestCase {
    private static DepartmentPK m_departmentPK;
    private static ScientistPK m_scientist1PK, m_scientist2PK, m_scientist3PK, m_jScientistPK; 
    
    public AdvancedCompositePKJunitTest() {
        super();
    }
    
    public AdvancedCompositePKJunitTest(String name) {
        super(name);
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
        suite.addTest(new AdvancedCompositePKJunitTest("testDepartmentAdmin")); 
      
        // MappedById tests (see spec page 30 for more info)
        suite.addTest(new AdvancedCompositePKJunitTest("testMapsIdExample1"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMapsIdExample1b"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMapsIdExample2"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMapsIdExample3"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMapsIdExample4"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMapsIdExample5"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMapsIdExample5a"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMapsIdExample6"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMapsIdExample6MultiLevel"));
        
        suite.addTest(new AdvancedCompositePKJunitTest("testJoinColumnSharesPK"));
        
        suite.addTest(new AdvancedCompositePKJunitTest("testMapWithDerivedId"));
        suite.addTest(new AdvancedCompositePKJunitTest("testIdentitySequencingForDerivedId"));
        
        suite.addTest(new AdvancedCompositePKJunitTest("testSharedDerivedIdEmbeddableClass"));
        suite.addTest(new AdvancedCompositePKJunitTest("testNestedMapsId"));
        
        if (!isJPA10()) {
            // This test runs only on a JEE6 / JPA 2.0 capable server
            suite.addTest(new AdvancedCompositePKJunitTest("testGetIdentifier"));
        }
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new CompositePKTableCreator().replaceTables(JUnitTestCase.getServerSession());
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    /**
     * Verifies that order-by metadata is correctly processed/defaulted.
     */
    public void testOrderBySetting() {
        ServerSession session = JUnitTestCase.getServerSession();
        
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
    
    public void testDepartmentAdmin() {
        String location = "Ottawa";
        String depName = "New Product Research";
        String depRole = "R&D new technologies";
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Employee emp = new Employee();
            emp.setFirstName("George");
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
        Department department = createEntityManager().find(Department.class, m_departmentPK);
        
        assertTrue("Error on reading back the ordered department list.", department != null);
        assertTrue("The number of scientists were incorrect.", department.getScientists().size() > 0);
    }
    
    public void testReadJuniorScientist() {
        JuniorScientist jScientist;
        
        jScientist = createEntityManager().find(JuniorScientist.class, m_jScientistPK);
        assertTrue("Error on reading back the junior scientist.", jScientist != null);
    }

    //bug gf672 - JPQL Select query with IN/ANY in WHERE clause and subselect fails.
    public void testAnyAndAll() {
        EntityManager em = createEntityManager();
        
        beginTransaction(em);
        try {
            Query query1 = em.createQuery("SELECT s FROM Scientist s WHERE s = ANY (SELECT s2 FROM Scientist s2)");
            List<Scientist> results1 = query1.getResultList();

            Query query2 = em.createQuery("SELECT s FROM Scientist s WHERE s = ALL (SELECT s2 FROM Scientist s2)");
            List<Scientist> results2 = query2.getResultList();

            Query query3 = em.createQuery("SELECT s FROM Scientist s WHERE s.department = ALL (SELECT DISTINCT d FROM Department d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A')");
            List<Scientist> results3 = query3.getResultList();

            Query query4 = em.createQuery("SELECT s FROM Scientist s WHERE s.department = ANY (SELECT DISTINCT d FROM Department d JOIN d.scientists ds JOIN ds.cubicle c WHERE c.code = 'G')");
            List<Scientist> results4 = query4.getResultList();

            // control queries
            
            Query controlQuery1 = em.createQuery("SELECT s FROM Scientist s");
            List<Scientist> controlResults1 = controlQuery1.getResultList();
            
            List<Scientist> controlResults2;
            if(controlResults1.size() == 1) {
                controlResults2 = controlResults1;
            } else {
                controlResults2 = new ArrayList<Scientist>();
            }

            Query controlQuery3 = em.createQuery("SELECT s FROM Scientist s JOIN s.department d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A'");
            List<Scientist> controlResults3 = controlQuery3.getResultList();
            
            Query controlQuery4 = em.createQuery("SELECT s FROM Scientist s WHERE EXISTS (SELECT DISTINCT d FROM Department d JOIN d.scientists ds JOIN ds.cubicle c WHERE c.code = 'G' AND d = s.department)");
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
    
    public void testMapsIdExample1() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Sargeant sargeant = new Sargeant();
        MasterCorporal masterCorporal = new MasterCorporal();
        MasterCorporalId masterCorporalId = new MasterCorporalId();
        
        try {    
            sargeant.setName("Sarge");
            
            masterCorporalId.setName("Corpie");
            masterCorporal.setId(masterCorporalId);
            masterCorporal.setSargeant(sargeant);
            
            // Sargeant is cascade persist.
            em.persist(masterCorporal);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        clearCache();
        em = createEntityManager();
        
        Sargeant refreshedSargeant = em.find(Sargeant.class, sargeant.getSargeantId());       
        assertTrue("The sargeant read back did not match the original", getServerSession().compareObjects(sargeant, refreshedSargeant));

        MasterCorporal refreshedMasterCorporal = em.find(MasterCorporal.class, masterCorporalId);
        assertTrue("The master corporal read back did not match the original", getServerSession().compareObjects(masterCorporal, refreshedMasterCorporal));  
    }
    
    public void testMapsIdExample1b() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Sargeant sargeant = new Sargeant();
        MasterCorporal masterCorporal = new MasterCorporal();
        
        try {    
            sargeant.setName("SargeB");
            masterCorporal.setSargeant(sargeant);
            
            // We don't set the master corporal id, only the maps id mapping
            // sargeant. After persist, the master corporal id should be 
            // created for us with the derived id set.
            
            // Sargeant is cascade persist.
            em.persist(masterCorporal);
            
            // Now set the other part of the master corporal id (name) before
            // commit.
            masterCorporal.getId().setName("CorpieB");
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        clearCache();
        em = createEntityManager();
        
        Sargeant refreshedSargeant = em.find(Sargeant.class, sargeant.getSargeantId());       
        assertTrue("The sargeant read back did not match the original", getServerSession().compareObjects(sargeant, refreshedSargeant));

        MasterCorporal refreshedMasterCorporal = em.find(MasterCorporal.class, masterCorporal.getId());
        assertTrue("The master corporal read back did not match the original", getServerSession().compareObjects(masterCorporal, refreshedMasterCorporal));  
    }
    
    public void testMapsIdExample2() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Major major = new Major();
        MajorId majorId;
        Captain captain = new Captain();
        CaptainId captainId = new CaptainId();
        
        try {    
            major.setFirstName("Mr.");
            major.setLastName("Major");
            majorId = major.getPK();
            em.persist(major);
            
            captainId.setName("Captain Sparrow");
            captain.setId(captainId);
            captain.setMajor(major);
            em.persist(captain);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        clearCache();
        em = createEntityManager();
        
        Major refreshedMajor = em.find(Major.class, majorId);       
        assertTrue("The major read back did not match the original", getServerSession().compareObjects(major, refreshedMajor));

        Captain refreshedCaptain = em.find(Captain.class, captainId);
        assertTrue("The captain read back did not match the original", getServerSession().compareObjects(captain, refreshedCaptain));  
    }
    
    public void testMapsIdExample3() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Corporal corporal = new Corporal();
        CorporalId corporalId = new CorporalId();
        Private aPrivate = new Private();
        PrivateId privateId = new PrivateId();
        
        try {
            corporalId.setFirstName("Corporal");
            corporalId.setLastName("Kenny");
            corporal.setCorporalId(corporalId);
            em.persist(corporal);
            
            privateId.setName("Private Ryan");
            aPrivate.setId(privateId);
            aPrivate.setCorporal(corporal);
            em.persist(aPrivate);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        clearCache();
        em = createEntityManager();        
        
        Corporal refreshedCorporal = em.find(Corporal.class, corporalId);       
        assertTrue("The corporal read back did not match the original", getServerSession().compareObjects(corporal, refreshedCorporal));

        Private refreshedPrivate = em.find(Private.class, privateId);
        assertTrue("The private read back did not match the original", getServerSession().compareObjects(aPrivate, refreshedPrivate));  
    }
    
    public void testMapsIdExample4() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        General general = new General();
        LieutenantGeneral lieutenantGeneral = new LieutenantGeneral();
        
        try {
            em.persist(general);
            
            lieutenantGeneral.setGeneral(general);
            em.persist(lieutenantGeneral);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        clearCache();
        em = createEntityManager();        
        
        General refreshedGeneral = em.find(General.class, general.getGeneralId());
        assertTrue("The general read back did not match the original", getServerSession().compareObjects(general, refreshedGeneral));

        LieutenantGeneral refreshedLieutenantGeneral = em.find(LieutenantGeneral.class, lieutenantGeneral.getId());
        assertTrue("The lieutenant general read back did not match the original", getServerSession().compareObjects(lieutenantGeneral, refreshedLieutenantGeneral));  
    }

    public void testMapsIdExample5() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        MajorGeneral majorGeneral = new MajorGeneral();
        BrigadierGeneral brigadierGeneral = new BrigadierGeneral();
        
        try {
            majorGeneral.setFirstName("Major");
            majorGeneral.setLastName("Bilko");
            
            // Test the cascade persist on this mapping.
            brigadierGeneral.setMajorGeneral(majorGeneral);
            em.persist(brigadierGeneral);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        clearCache();
        em = createEntityManager();        
        
        MajorGeneral refreshedMajorGeneral = em.find(MajorGeneral.class, majorGeneral.getPK());
        assertTrue("The major general read back did not match the original", getServerSession().compareObjects(majorGeneral, refreshedMajorGeneral));

        BrigadierGeneral refreshedBrigadierGeneral = em.find(BrigadierGeneral.class, brigadierGeneral.getId());
        assertTrue("The brigadier general read back did not match the original", getServerSession().compareObjects(brigadierGeneral, refreshedBrigadierGeneral));  
    }
    
    public void testMapsIdExample5a() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Major major = new Major();
        major.setFirstName("Another");
        major.setLastName("Major");
        MajorId majorId = major.getPK();
        
        Lackey lackey = new Lackey();
        
        LackeyCrewMember lcm = new LackeyCrewMember();
        LackeyCrewMemberId lcmId = new LackeyCrewMemberId();
        lcm.setLackey(lackey);
        lcm.setRank(1);
        lcmId.setRank(1);
        lcmId.setMajorPK(majorId);
        
        try {    
            em.persist(major);
            lackey.setMajor(major);
            lackey.setName("Little");
            em.persist(lackey);
            
            em.persist(lcm);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        clearCache();
        em = createEntityManager();
        
        Major refreshedMajor = em.find(Major.class, majorId);       
        assertTrue("The major read back did not match the original", getServerSession().compareObjects(major, refreshedMajor));

        Lackey refreshedLackey = em.find(Lackey.class, majorId);
        assertTrue("The Lackey read back did not match the original", getServerSession().compareObjects(lackey, refreshedLackey)); 
        
        LackeyCrewMember refreshedLCM = em.find(LackeyCrewMember.class, lcmId);
        assertTrue("The LackeyCrewMember read back did not match the original", getServerSession().compareObjects(lcm, refreshedLCM));  
    }
    
    public void testMapsIdExample6() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Lieutenant lieutenant = new Lieutenant();
        LieutenantId lieutenantId = new LieutenantId();
        SecondLieutenant secondLieutenant = new SecondLieutenant();
        
        try {
            lieutenantId.setFirstName("Lieutenant");
            lieutenantId.setLastName("Dan");
            lieutenant.setId(lieutenantId);
            em.persist(lieutenant);
            
            em.persist(secondLieutenant);
            secondLieutenant.setLieutenant(lieutenant);
            em.flush();
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        clearCache();
        em = createEntityManager();        
        
        Lieutenant refreshedLieutenant = em.find(Lieutenant.class, lieutenant.getId());
        assertTrue("The lieutenant read back did not match the original", getServerSession().compareObjects(lieutenant, refreshedLieutenant));

        SecondLieutenant refreshedSecondLieutenant = em.find(SecondLieutenant.class, secondLieutenant.getId());
        assertTrue("The second lieutenant read back did not match the original", getServerSession().compareObjects(secondLieutenant, refreshedSecondLieutenant));  
    }
    
    public void testMapsIdExample6MultiLevel() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Lieutenant lieutenant = new Lieutenant();
        LieutenantId lieutenantId = new LieutenantId();
        SecondLieutenant secondLieutenant = new SecondLieutenant();
        OfficerCadet officerCadet = new OfficerCadet();
        
        try {
            lieutenantId.setFirstName("Lieutenant");
            lieutenantId.setLastName("Daniel");
            lieutenant.setId(lieutenantId);
            em.persist(lieutenant);
            
            em.persist(secondLieutenant);
            secondLieutenant.setLieutenant(lieutenant);
            
            officerCadet.setSecondLieutenant(secondLieutenant);
            em.persist(officerCadet);
            
            em.flush();
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        clearCache();
        em = createEntityManager();        
        
        Lieutenant refreshedLieutenant = em.find(Lieutenant.class, lieutenant.getId());
        assertTrue("The lieutenant read back did not match the original", getServerSession().compareObjects(lieutenant, refreshedLieutenant));

        SecondLieutenant refreshedSecondLieutenant = em.find(SecondLieutenant.class, secondLieutenant.getId());
        assertTrue("The second lieutenant read back did not match the original", getServerSession().compareObjects(secondLieutenant, refreshedSecondLieutenant));
        
        OfficerCadet refreshedOfficerCadet = em.find(OfficerCadet.class, officerCadet.getId());
        assertTrue("The officer cadet read back did not match the original", getServerSession().compareObjects(officerCadet, refreshedOfficerCadet));
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
            assertTrue("Got an incorrect id from persistenceUtil.getIdentifier()", pk.equals(util.getIdentifier(department)));
        } finally {
            rollbackTransaction(em);
        }
    }
    
    public void testJoinColumnSharesPK(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        org.eclipse.persistence.descriptors.ClassDescriptor descriptor = getServerSession().getDescriptor(Office.class);
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
            
            assertTrue("Office's department not properly persisted", office.getDepartment() != null);
            assertTrue("Department's offices not properly persisted", department.getOffices().size() > 0);
            
            
        } catch (Exception e){
            fail("Exception thrown while inserting an object with a read-only column in a foreign key." + e);
        } finally {
            rollbackTransaction(em);
        }
    }
    
    public void testMapWithDerivedId(){
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Employee emp = new Employee();
            emp.setFirstName("George");
            em.persist(emp);

            Administrator adminEmp = new Administrator();
            adminEmp.setContractCompany("George's consulting");
            adminEmp.setEmployee(emp);
            em.persist(adminEmp);

            Department support = new Department();
            support.setLocation("Ottawa");
            support.setName("Support");
            support.setRole("Support Customers");
            em.persist(support);

            DepartmentAdminRole depAdmin = new DepartmentAdminRole();
            depAdmin.setAdmin(adminEmp);
            depAdmin.setDepartment(support);

            em.persist(depAdmin);
            
            AdminPool pool = new AdminPool();
            pool.setId(1);
            pool.addAdmin(depAdmin);
            depAdmin.setPool(pool);
            em.persist(pool);
            em.flush();
            
            em.clear();
            clearCache();
            
            pool = em.find(AdminPool.class, pool.getId());
            
            assertTrue("The AdminPool was not found.", pool != null);
            assertTrue("The map did not contain the correct elements.", pool.getAdmins().get(depAdmin.buildDepartmentAdminRolePK()) != null);
            
        } catch (Exception e) {
            fail("Exception caught while testing maps with derived ids." + e);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    public void testIdentitySequencingForDerivedId() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Bookie bookie = new Bookie();
        
        try {
            bookie.setName("Bookie Joe");
            CellNumber cellNumber1 = new CellNumber();
            cellNumber1.setNumber("613-765-6452");
            cellNumber1.setDescription("NHL bets");
            bookie.addCellNumber(cellNumber1);
        
            CellNumber cellNumber2 = new CellNumber();
            cellNumber2.setNumber("613-765-6222");
            cellNumber2.setDescription("NB@ bets");
            bookie.addCellNumber(cellNumber2);
        
            // Bookie has cascade persist to cell number;
            em.persist(bookie);
        
            // Flush the changes.
            em.flush();
        
            // Make some changes.
            CellNumber cellNumber3 = new CellNumber();
            cellNumber3.setNumber("613-765-7422");
            cellNumber3.setDescription("PGA bets");
            bookie.addCellNumber(cellNumber3);
            em.persist(cellNumber3);
        
            // Flush the changes.
            em.flush();
        
            // Make some changes.
            bookie.setName("Bookie Jo");
            cellNumber2.setDescription("NBA bets");;
        
            // Flush the changes.
            em.flush();
        
            // Do some finds
            CellNumber findCellNumber1 = em.find(CellNumber.class, cellNumber1.buildPK());
            assertNotNull(findCellNumber1);
        
            CellNumberPK key = cellNumber2.buildPK();
            CellNumber findCellNumber2 = em.find(CellNumber.class, key);
            assertNotNull(findCellNumber2);
        
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    } 
    
    public void testSharedDerivedIdEmbeddableClass() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        MasterCorporalClone masterCorporal = new MasterCorporalClone();
        
        try {
            MasterCorporalId masterCorporalId = new MasterCorporalId();
            masterCorporalId.setName("MCC " + System.currentTimeMillis());
            masterCorporalId.setSargeantPK(System.currentTimeMillis());
            masterCorporal.setId(masterCorporalId);
            em.persist(masterCorporal);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        
        clearCache();
        em = createEntityManager();
        
        MasterCorporalClone refreshedMasterCorporal = em.find(MasterCorporalClone.class, masterCorporal.getId());
        assertTrue("The master corporal clone read back did not match the original", getServerSession().compareObjects(masterCorporal, refreshedMasterCorporal));
    }
    
    public void testNestedMapsId() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            GolfClubHead head = new GolfClubHead();
            em.persist(head);
            
            GolfClubShaft shaft = new GolfClubShaft();
            em.persist(shaft);
            
            GolfClub golfClub = new GolfClub();
            golfClub.setHead(head);
            golfClub.setShaft(shaft);
            
            GolfClubOrder golfClubOrder = new GolfClubOrder();
            golfClubOrder.setGolfClub(golfClub);
            golfClub.setOrder(golfClubOrder);
            
            em.persist(golfClub);
            em.persist(golfClubOrder);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }
}
