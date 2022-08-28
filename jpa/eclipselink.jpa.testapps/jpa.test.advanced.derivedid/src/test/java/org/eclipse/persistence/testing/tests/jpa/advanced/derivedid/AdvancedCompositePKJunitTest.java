/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2022 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.advanced.derivedid;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.CompositePKTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.AdminPool;
import org.eclipse.persistence.testing.models.jpa.advanced.derivedid.Administrator;
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

public class AdvancedCompositePKJunitTest extends JUnitTestCase {

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

        suite.addTest(new AdvancedCompositePKJunitTest("testMapWithDerivedId"));
        suite.addTest(new AdvancedCompositePKJunitTest("testIdentitySequencingForDerivedId"));

        suite.addTest(new AdvancedCompositePKJunitTest("testSharedDerivedIdEmbeddableClass"));
        suite.addTest(new AdvancedCompositePKJunitTest("testNestedMapsId"));

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
        assertTrue("The sargeant read back did not match the original", getPersistenceUnitServerSession().compareObjects(sargeant, refreshedSargeant));

        MasterCorporal refreshedMasterCorporal = em.find(MasterCorporal.class, masterCorporalId);
        assertTrue("The master corporal read back did not match the original", getPersistenceUnitServerSession().compareObjects(masterCorporal, refreshedMasterCorporal));
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
        assertTrue("The sargeant read back did not match the original", getPersistenceUnitServerSession().compareObjects(sargeant, refreshedSargeant));

        MasterCorporal refreshedMasterCorporal = em.find(MasterCorporal.class, masterCorporal.getId());
        assertTrue("The master corporal read back did not match the original", getPersistenceUnitServerSession().compareObjects(masterCorporal, refreshedMasterCorporal));
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
        assertTrue("The major read back did not match the original", getPersistenceUnitServerSession().compareObjects(major, refreshedMajor));

        Captain refreshedCaptain = em.find(Captain.class, captainId);
        assertTrue("The captain read back did not match the original", getPersistenceUnitServerSession().compareObjects(captain, refreshedCaptain));
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
        assertTrue("The corporal read back did not match the original", getPersistenceUnitServerSession().compareObjects(corporal, refreshedCorporal));

        Private refreshedPrivate = em.find(Private.class, privateId);
        assertTrue("The private read back did not match the original", getPersistenceUnitServerSession().compareObjects(aPrivate, refreshedPrivate));
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
        assertTrue("The general read back did not match the original", getPersistenceUnitServerSession().compareObjects(general, refreshedGeneral));

        LieutenantGeneral refreshedLieutenantGeneral = em.find(LieutenantGeneral.class, lieutenantGeneral.getId());
        assertTrue("The lieutenant general read back did not match the original", getPersistenceUnitServerSession().compareObjects(lieutenantGeneral, refreshedLieutenantGeneral));
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
        assertTrue("The major general read back did not match the original", getPersistenceUnitServerSession().compareObjects(majorGeneral, refreshedMajorGeneral));

        BrigadierGeneral refreshedBrigadierGeneral = em.find(BrigadierGeneral.class, brigadierGeneral.getId());
        assertTrue("The brigadier general read back did not match the original", getPersistenceUnitServerSession().compareObjects(brigadierGeneral, refreshedBrigadierGeneral));
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
        assertTrue("The major read back did not match the original", getPersistenceUnitServerSession().compareObjects(major, refreshedMajor));

        Lackey refreshedLackey = em.find(Lackey.class, majorId);
        assertTrue("The Lackey read back did not match the original", getPersistenceUnitServerSession().compareObjects(lackey, refreshedLackey));

        LackeyCrewMember refreshedLCM = em.find(LackeyCrewMember.class, lcmId);
        assertTrue("The LackeyCrewMember read back did not match the original", getPersistenceUnitServerSession().compareObjects(lcm, refreshedLCM));
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
        assertTrue("The lieutenant read back did not match the original", getPersistenceUnitServerSession().compareObjects(lieutenant, refreshedLieutenant));

        SecondLieutenant refreshedSecondLieutenant = em.find(SecondLieutenant.class, secondLieutenant.getId());
        assertTrue("The second lieutenant read back did not match the original", getPersistenceUnitServerSession().compareObjects(secondLieutenant, refreshedSecondLieutenant));
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
        assertTrue("The lieutenant read back did not match the original", getPersistenceUnitServerSession().compareObjects(lieutenant, refreshedLieutenant));

        SecondLieutenant refreshedSecondLieutenant = em.find(SecondLieutenant.class, secondLieutenant.getId());
        assertTrue("The second lieutenant read back did not match the original", getPersistenceUnitServerSession().compareObjects(secondLieutenant, refreshedSecondLieutenant));

        OfficerCadet refreshedOfficerCadet = em.find(OfficerCadet.class, officerCadet.getId());
        assertTrue("The officer cadet read back did not match the original", getPersistenceUnitServerSession().compareObjects(officerCadet, refreshedOfficerCadet));
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

            assertNotNull("The AdminPool was not found.", pool);
            assertNotNull("The map did not contain the correct elements.", pool.getAdmins().get(depAdmin.buildDepartmentAdminRolePK()));

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
            cellNumber2.setDescription("NBA bets");

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
        assertTrue("The master corporal clone read back did not match the original", getPersistenceUnitServerSession().compareObjects(masterCorporal, refreshedMasterCorporal));
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
