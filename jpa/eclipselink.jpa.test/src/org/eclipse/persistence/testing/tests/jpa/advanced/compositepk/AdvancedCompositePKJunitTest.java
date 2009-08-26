/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced.compositepk;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.Query;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Cubicle;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.JuniorScientist;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Scientist;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.ScientistPK;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.DepartmentPK;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.CompositePKTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.BrigadierGeneral;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Captain;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.CaptainId;
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
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.MasterCorporalId;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Private;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.PrivateId;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Sargeant;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.SecondLieutenant;
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
        suite.addTest(new AdvancedCompositePKJunitTest("testCreateDepartment"));
        suite.addTest(new AdvancedCompositePKJunitTest("testCreateScientists"));
        suite.addTest(new AdvancedCompositePKJunitTest("testReadDepartment"));
        suite.addTest(new AdvancedCompositePKJunitTest("testReadJuniorScientist"));
        suite.addTest(new AdvancedCompositePKJunitTest("testAnyAndAll"));
        suite.addTest(new AdvancedCompositePKJunitTest("testDepartmentAdmin")); 
      
        // MappedById tests (see spec page 30 for more info)
        suite.addTest(new AdvancedCompositePKJunitTest("testMappedByIdExample1"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMappedByIdExample2"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMappedByIdExample3"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMappedByIdExample4"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMappedByIdExample5"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMappedByIdExample5a"));
        suite.addTest(new AdvancedCompositePKJunitTest("testMappedByIdExample6"));
        
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
            //org.eclipse.persistence.internal.jpa.EntityManagerImpl emImpl = (org.eclipse.persistence.internal.jpa.EntityManagerImpl) em;
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
    
    public void testMappedByIdExample1() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Sargeant sargeant = new Sargeant();
        MasterCorporal masterCorporal = new MasterCorporal();
        MasterCorporalId masterCorporalId = new MasterCorporalId();
        
        try {    
            sargeant.setName("Sarge");
            em.persist(sargeant);
            
            masterCorporalId.setName("Corpie");
            masterCorporal.setId(masterCorporalId);
            masterCorporal.setSargeant(sargeant);
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
    
    public void testMappedByIdExample2() {
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
    
    public void testMappedByIdExample3() {
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
    
    public void testMappedByIdExample4() {
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

    public void testMappedByIdExample5() {
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
    
    public void testMappedByIdExample5a() {
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
    
    public void testMappedByIdExample6() {
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
            
            secondLieutenant.setLieutenant(lieutenant);
            em.persist(secondLieutenant);
            
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
}
