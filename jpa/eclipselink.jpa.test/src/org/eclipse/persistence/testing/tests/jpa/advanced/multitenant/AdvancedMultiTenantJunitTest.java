/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     03/23/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     04/01/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 2)
//     04/21/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 5)
//     05/24/2011-2.3 Guy Pelletier
//       - 345962: Join fetch query when using tenant discriminator column fails.
//     06/1/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 9)
//     06/30/2011-2.3.1 Guy Pelletier
//       - 341940: Add disable/enable allowing native queries
//     09/09/2011-2.3.1 Guy Pelletier
//       - 356197: Add new VPD type to MultitenantType
//     11/10/2011-2.4 Guy Pelletier
//       - 357474: Address primaryKey option from tenant discriminator column
//     11/15/2011-2.3.2 Guy Pelletier
//       - 363820: Issue with clone method from VPDMultitenantPolicy
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
//     22/05/2012-2.4 Guy Pelletier
//       - 380008: Multitenant persistence units with a dedicated emf should force tenant property specification up front.
//     01/06/2011-2.3 Guy Pelletier
//       - 371453: JPA Multi-Tenancy in Bidirectional OneToOne Relation throws ArrayIndexOutOfBoundsException
//     08/11/2012-2.5 Guy Pelletier
//       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
//     20/11/2012-2.5 Guy Pelletier
//       - 394524: Invalid query key [...] in expression
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NamedQuery;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import junit.framework.*;

import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.AdvancedMultiTenantTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Boss;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Candidate;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Capo;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Card;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Contract;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.MafiaFamily;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Mafioso;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Mason;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Party;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Reward;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Riding;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Soldier;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SubCapo;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SubTask;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Supporter;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SupporterInfo;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.SupporterInfoSub;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Task;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Trowel;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Underboss;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Card;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.Envelope;

public class AdvancedMultiTenantJunitTest extends JUnitTestCase {
    public String MULTI_TENANT_VPD_PU;
    public String MULTI_TENANT_PU;
    public String MULTI_TENANT_PU_123;

    public String MULTI_TENANT_TABLE_PER_TENANT_PU;
    public String MULTI_TENANT_TABLE_PER_TENANT_C_PU;

    public static long candidateAId;
    public static long supporter1Id;
    public static long supporter2Id;
    public static int ridingId;
    public static int partyId;
    public static int masonId;

    public static int family707;
    public static int family007;
    public static int family123;
    public static int capo123Id;
    public static int soldier123Id;
    public static List<Integer> family707Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family707Contracts = new ArrayList<Integer>();
    public static List<Integer> family007Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family007Contracts = new ArrayList<Integer>();
    public static List<Integer> family123Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family123Contracts = new ArrayList<Integer>();

    public AdvancedMultiTenantJunitTest() {
        super();
    }

    public AdvancedMultiTenantJunitTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenantJunitTest");

        suite.addTest(new AdvancedMultiTenantJunitTest("testSetup"));

        suite.addTest(new AdvancedMultiTenantJunitTest("testCreateMafiaFamily707"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testCreateMafiaFamily007"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testCreateEMF123WithoutAllTenantPropertiesSet"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testCreateMafiaFamily123"));

        suite.addTest(new AdvancedMultiTenantJunitTest("testValidateMafiaFamily707"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testValidateMafiaFamily007"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testValidateMafiaFamily707and007WithSameEM"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testValidateMafiaFamily123"));

        suite.addTest(new AdvancedMultiTenantJunitTest("testComplexMultitenantQueries"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testVPDEMPerTenant"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testMultitenantOneToOneReadObjectRead"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testMultitenantPrimaryKeyWithIdClass"));

        suite.addTest(new AdvancedMultiTenantJunitTest("testTablePerTenantA"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testTablePerTenantAQueries"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testTablePerTenantB"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testTablePerTenantBQueries"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testTablePerTenantC"));
        suite.addTest(new AdvancedMultiTenantJunitTest("testTablePerTenantCQueries"));

        return suite;
    }

    public String getMULTI_TENANT_PU() { return "multi-tenant-shared-emf"; }

    public String getMULTI_TENANT_PU_123() { return "multi-tenant-123"; }

    public String getMULTI_TENANT_VPD_PU() { return "multi-tenant-vpd"; }

    public String getMULTI_TENANT_TABLE_PER_TENANT_PU() { return "multi-tenant-table-per-tenant"; }

    public String getMULTI_TENANT_TABLE_PER_TENANT_C_PU(){ return "multi-tenant-table-per-tenant-C"; }

    public EntityManager createSharedEMFEntityManager(){
        return createEntityManager(getMULTI_TENANT_PU());
    }

    public EntityManager create123EntityManager(){
        return createEntityManager(getMULTI_TENANT_PU_123());
    }

    public EntityManager createTenant123EntityManager(){
        Map<String, String> properties = new HashMap<String, String>();
        EntityManager em = null;
        //properties passed in createEntityManager() won't work on server since server side entity manager is injected, so we have "eclipselink.tenant-id" in server persistence.xml
        if (! isOnServer()) {
            properties.putAll(JUnitTestCaseHelper.getDatabaseProperties(getMULTI_TENANT_PU_123()));
            properties.put(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, "123");
            em = createEntityManager(getMULTI_TENANT_PU_123(), properties);
        }else{
            em = create123EntityManager();
        }
        return em;
    }

    public EntityManager createVPDEntityManager(){
        return createEntityManager(getMULTI_TENANT_VPD_PU());
    }

    public EntityManager createTableTenantEntityManager(){
        return createEntityManager(getMULTI_TENANT_TABLE_PER_TENANT_PU());
    }

    public EntityManager createTableTenantCEntityManager(){
        return createEntityManager(getMULTI_TENANT_TABLE_PER_TENANT_C_PU());
    }
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedMultiTenantTableCreator().replaceTables(((org.eclipse.persistence.jpa.JpaEntityManager) createSharedEMFEntityManager()).getServerSession());
    }

    public void testTablePerTenantA() {
        EntityManager em = createTableTenantEntityManager();

        try {
            beginTransaction(em);

            // Valid to set the table per tenant qualifier now.
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "A");

            Candidate candidateA = new Candidate();
            candidateA.setName("CA");

            candidateA.addHonor("Raised most money");
            candidateA.addHonor("Highest win margin");

            candidateA.setSalary(9999999);

            Supporter supporter1 = new Supporter();
            supporter1.setName("Supporter1a");
            SupporterInfo supporter1Info = new SupporterInfo();
            supporter1Info.setDescription("Supporter1aDesc");
            SupporterInfoSub supporter1InfoSub = new SupporterInfoSub();
            supporter1InfoSub.setSubDescription("Supporter1aSubDesc");
            supporter1Info.setSubInfo(supporter1InfoSub);
            supporter1.setInfo(supporter1Info);
            candidateA.addSupporter(supporter1);

            Supporter supporter2 = new Supporter();
            supporter2.setName("Supporter2a");
            SupporterInfo supporter2Info = new SupporterInfo();
            supporter2Info.setDescription("Supporter2aDesc");
            SupporterInfoSub supporter2InfoSub = new SupporterInfoSub();
            supporter2InfoSub.setSubDescription("Supporter2aSubDesc");
            supporter2Info.setSubInfo(supporter2InfoSub);
            supporter2.setInfo(supporter2Info);
            candidateA.addSupporter(supporter2);

            Supporter supporter3 = new Supporter();
            supporter3.setName("Supporter3a");
            SupporterInfo supporter3Info = new SupporterInfo();
            supporter3Info.setDescription("Supporter3aDesc");
            SupporterInfoSub supporter3InfoSub = new SupporterInfoSub();
            supporter3InfoSub.setSubDescription("Supporter3aSubDesc");
            supporter3Info.setSubInfo(supporter3InfoSub);
            supporter3.setInfo(supporter3Info);
            candidateA.addSupporter(supporter3);

            Party party = new Party();
            party.setName("Conservatives");
            party.addCandidate(candidateA);

            Riding riding = new Riding();
            riding.setName("Ottawa");
            candidateA.setRiding(riding);

            // Persist our objects.
            em.persist(party);
            em.persist(candidateA);
            em.persist(supporter1);
            em.persist(supporter2);
            em.persist(supporter3);
            em.persist(riding);

            Mason mason = new Mason();
            mason.setName("FromTenantA");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2009, 1, 1)), "Best pointer");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2010, 5, 9)), "Least screw-ups");

            Trowel trowel = new Trowel();
            trowel.setType("Pointing");
            mason.setTrowel(trowel);
            trowel.setMason(mason);

            em.persist(mason);
            em.persist(trowel);

            // Grab any id's for verification.
            candidateAId = candidateA.getId();
            ridingId = riding.getId();
            partyId = party.getId();
            supporter1Id = supporter1.getId();
            supporter2Id = supporter2.getId();
            masonId = mason.getId();

            commitTransaction(em);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
        }
    }

    public void testTablePerTenantAQueries() {
        EntityManager em = createTableTenantEntityManager();

        try {
            beginTransaction(em);

            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "A");

            List resultsFromNamedQuery = em.createNamedQuery("Supporter.findAll").getResultList();
            List resultsFromDynamicQuery = em.createQuery("SELECT s FROM Supporter s ORDER BY s.id DESC").getResultList();

            assertTrue("Incorrect number of supporters returned from named query.", resultsFromNamedQuery.size() == 3);
            assertTrue("Incorrect number of supporters returned from dynamic query.", resultsFromDynamicQuery.size() == 3);

            // Test some more complex joining queries.
            List<Supporter> results = em.createNamedQuery("Supporter.findBySupporterInfo").setParameter("desc", "Supporter1aDesc").getResultList();
            assertFalse("No results returned.", results == null);
            assertTrue("Single result not returned.", results.size() == 1);
            assertTrue("Didn't return supporter1a", results.get(0).getName().equals("Supporter1a"));
            results = em.createNamedQuery("Supporter.findBySupporterInfoSub").setParameter("subDesc", "Supporter3aSubDesc").getResultList();
            assertFalse("No results returned.", results == null);
            assertTrue("Single result not returned.", results.size() == 1);
            assertTrue("Didn't return supporter3a", results.get(0).getName().equals("Supporter3a"));

            commitTransaction(em);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
        }
    }

    public void testTablePerTenantB() {
        EntityManager em = createTableTenantEntityManager();

        try {
            beginTransaction(em);

            // Valid to set the table per tenant qualifier now.
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "B");

            // Should not find these ...
            assertNull("CandidateA was found from tenant B.", em.find(Candidate.class, candidateAId));
            assertNull("Supporter1 was found from tenant B.", em.find(Supporter.class, supporter1Id));
            assertNull("Supporter2 was found from tenant B.", em.find(Supporter.class, supporter2Id));
            assertNull("Mason was found from tenant B.", em.find(Mason.class, masonId));

            // Should find these ...
            Riding riding = em.find(Riding.class, ridingId);
            assertNotNull("Riding was not found from tenant B", riding);
            Party party = em.find(Party.class, partyId);
            assertNotNull("Party was not found from tenant B", party);
            // TODO: can't do this (would have to initialize the mapping from party again to the new descriptor.
            // future implementation maybe.
            //assertTrue("Party had candidates", party.getCandidates().isEmpty());

            Candidate candidateB = new Candidate();
            candidateB.setName("CB");
            candidateB.setSalary(100);
            em.persist(candidateB);
            commitTransaction(em);

            em.clear();
            clearCache(getMULTI_TENANT_TABLE_PER_TENANT_PU());

            beginTransaction(em);

            // Valid to set the table per tenant qualifier now.
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "B");

            Candidate candidateBRefreshed = em.find(Candidate.class, candidateB.getId());

            assertNull("Candidate B has a Party when he shouldn't have.", candidateBRefreshed.getParty());
            assertNull("Candidate B has a Riding when he shouldn't have.", candidateBRefreshed.getRiding());
            assertTrue("Candidate B had supporters when he shouldn't have.", candidateBRefreshed.getSupporters().isEmpty());
            assertTrue("Candidate B had honors when he shouldn't have.", candidateBRefreshed.getHonors().isEmpty());
            assertTrue("Candidate B had the incorrect salary.", candidateBRefreshed.getSalary() == 100);

            commitTransaction(em);

        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {

            closeEntityManager(em);
        }
    }

    public void testTablePerTenantBQueries() {
        EntityManager em = createTableTenantEntityManager();

        try {
            beginTransaction(em);

            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "B");

            List resultsFromNamedQuery = em.createNamedQuery("Supporter.findAll").getResultList();
            List resultsFromDynamicQuery = em.createQuery("SELECT s FROM Supporter s ORDER BY s.id DESC").getResultList();

            assertTrue("Incorrect number of supporters returned from named query.", resultsFromNamedQuery.isEmpty());
            assertTrue("Incorrect number of supporters returned from dynamic query.", resultsFromDynamicQuery.isEmpty());

            commitTransaction(em);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
        }
    }

    public void testTablePerTenantC() {
        // Tenant is set in the persistence.xml file.
        EntityManager em = createTableTenantCEntityManager();

        try {
            beginTransaction(em);

            Candidate candidate = new Candidate();
            candidate.setName("C");

            candidate.addHonor("Raised most money");
            candidate.addHonor("Highest win margin");

            candidate.setSalary(9999999);

            Supporter supporter1 = new Supporter();
            supporter1.setName("Supporter1c");
            candidate.addSupporter(supporter1);

            Supporter supporter2 = new Supporter();
            supporter2.setName("Supporter2c");
            candidate.addSupporter(supporter2);

            Party party = new Party();
            party.setName("Conservatives");
            party.addCandidate(candidate);

            Riding riding = new Riding();
            riding.setName("Ottawa");
            candidate.setRiding(riding);

            // Persist our objects.
            em.persist(party);
            em.persist(candidate);
            em.persist(supporter2);
            em.persist(supporter1);
            em.persist(riding);

            Mason mason = new Mason();
            mason.setName("FromTenantC");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2009, 1, 1)), "Best pointer");
            mason.addAward(Helper.timestampFromDate(Helper.dateFromYearMonthDate(2010, 5, 9)), "Least screw-ups");

            Trowel trowel = new Trowel();
            trowel.setType("Pointing");
            mason.setTrowel(trowel);
            trowel.setMason(mason);

            em.persist(mason);
            em.persist(trowel);

            // Grab any id's for verification.
            candidateAId = candidate.getId();
            ridingId = riding.getId();
            partyId = party.getId();
            supporter1Id = supporter1.getId();
            supporter2Id = supporter2.getId();
            masonId = mason.getId();

            commitTransaction(em);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
        }
    }

    public void testTablePerTenantCQueries() {
        EntityManager em = createTableTenantCEntityManager();

        try {
            beginTransaction(em);

            List resultsFromNamedQuery = em.createNamedQuery("Supporter.findAll").getResultList();
            List resultsFromDynamicQuery = em.createQuery("SELECT s FROM Supporter s ORDER BY s.id DESC").getResultList();

            assertTrue("Incorrect number of supporters returned from named query.", resultsFromNamedQuery.size() == 2);
            assertTrue("Incorrect number of supporters returned from dynamic query.", resultsFromDynamicQuery.size() == 2);

            commitTransaction(em);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
        }
    }

    public void testCreateMafiaFamily707() {
        EntityManager em = createSharedEMFEntityManager();

        try {
            beginTransaction(em);
            //on server side, you have to set the em properties after transaction begins
            em.setProperty("tenant.id", "707");
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");

            MafiaFamily family = new MafiaFamily();
            family.setName("Gonzo");
            family.setRevenue(10000000.00);
            family.addTag("firstTag");
            family.addTag("secondTag");
            family.addTag("thirdTag");

            Boss boss = new Boss();
            boss.setFirstName("707");
            boss.setLastName("Boss");
            boss.setGender(Mafioso.Gender.Male);

            Underboss underboss = new Underboss();
            underboss.setFirstName("Under");
            underboss.setLastName("Boss");
            underboss.setGender(Mafioso.Gender.Male);

            Capo capo1 = new Capo();
            capo1.setFirstName("Capo");
            capo1.setLastName("Giggaloo");
            capo1.setGender(Mafioso.Gender.Female);

            Capo capo2 = new Capo();
            capo2.setFirstName("Capo");
            capo2.setLastName("CrazyGlue");
            capo2.setGender(Mafioso.Gender.Male);

            Soldier soldier1 = new Soldier();
            soldier1.setFirstName("Soldier");
            soldier1.setLastName("One");
            soldier1.setGender(Mafioso.Gender.Female);

            Soldier soldier2 = new Soldier();
            soldier2.setFirstName("Soldier");
            soldier2.setLastName("Two");
            soldier2.setGender(Mafioso.Gender.Male);

            Soldier soldier3 = new Soldier();
            soldier3.setFirstName("Soldier");
            soldier3.setLastName("Three");
            soldier3.setGender(Mafioso.Gender.Male);

            Soldier soldier4 = new Soldier();
            soldier4.setFirstName("Soldier");
            soldier4.setLastName("Four");
            soldier4.setGender(Mafioso.Gender.Male);

            Soldier soldier5 = new Soldier();
            soldier5.setFirstName("Soldier");
            soldier5.setLastName("Four");
            soldier5.setGender(Mafioso.Gender.Female);

            Contract contract1 = new Contract();
            contract1.setDescription("Whack 007 family boss");

            Contract contract2 = new Contract();
            contract2.setDescription("Acquire fully-automatic guns");

            Contract contract3 = new Contract();
            contract3.setDescription("Steal some money");

            // Populate the relationships.
            contract1.addSoldier(soldier1);
            contract1.addSoldier(soldier5);

            contract2.addSoldier(soldier1);
            contract2.addSoldier(soldier3);
            contract2.addSoldier(soldier5);

            contract3.addSoldier(soldier2);
            contract3.addSoldier(soldier3);
            contract3.addSoldier(soldier4);
            contract3.addSoldier(soldier5);

            boss.setUnderboss(underboss);

            capo1.setUnderboss(underboss);
            capo2.setUnderboss(underboss);

            capo1.addSoldier(soldier1);
            capo1.addSoldier(soldier2);

            capo2.addSoldier(soldier3);
            capo2.addSoldier(soldier4);
            capo2.addSoldier(soldier5);

            underboss.addCapo(capo1);
            underboss.addCapo(capo2);

            family.addMafioso(boss);
            family.addMafioso(underboss);

            family.addMafioso(capo1);
            family.addMafioso(capo2);

            family.addMafioso(soldier1);
            family.addMafioso(soldier2);
            family.addMafioso(soldier3);
            family.addMafioso(soldier4);
            family.addMafioso(soldier5);

            // Will cascade through the whole family.
            em.persist(family);

            // Store the ids to verify
            family707 = family.getId();
            family707Mafiosos.add(boss.getId());
            family707Mafiosos.add(underboss.getId());
            family707Mafiosos.add(capo1.getId());
            family707Mafiosos.add(capo2.getId());
            family707Mafiosos.add(soldier1.getId());
            family707Mafiosos.add(soldier2.getId());
            family707Mafiosos.add(soldier3.getId());
            family707Mafiosos.add(soldier4.getId());
            family707Mafiosos.add(soldier5.getId());
            family707Contracts.add(contract1.getId());
            family707Contracts.add(contract2.getId());
            family707Contracts.add(contract3.getId());
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testCreateMafiaFamily007() {
        EntityManager em = createSharedEMFEntityManager();
        try {
            beginTransaction(em);
            //on server side, you have to set the em properties after transaction begins
            em.setProperty("tenant.id", "007");
            em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");

            MafiaFamily family = new MafiaFamily();
            family.setName("Bond");
            family.setRevenue(987654321.03);
            family.addTag("tag1");
            family.addTag("tag2");
            family.addTag("tag3");
            family.addTag("tag4");
            family.addTag("tag5");

            Boss boss = new Boss();
            boss.setFirstName("007");
            boss.setLastName("Boss");
            boss.setGender(Mafioso.Gender.Female);

            Underboss underboss = new Underboss();
            underboss.setFirstName("Second");
            underboss.setLastName("InCommand");
            underboss.setGender(Mafioso.Gender.Female);

            Capo capo1 = new Capo();
            capo1.setFirstName("Capo");
            capo1.setLastName("Lubey");
            capo1.setGender(Mafioso.Gender.Male);

            Capo capo2 = new Capo();
            capo2.setFirstName("Capo");
            capo2.setLastName("Greasy");
            capo2.setGender(Mafioso.Gender.Female);

            Soldier soldier1 = new Soldier();
            soldier1.setFirstName("First");
            soldier1.setLastName("Grunt");
            soldier1.setGender(Mafioso.Gender.Male);

            Soldier soldier2 = new Soldier();
            soldier2.setFirstName("Second");
            soldier2.setLastName("Grunt");
            soldier2.setGender(Mafioso.Gender.Female);

            Soldier soldier3 = new Soldier();
            soldier3.setFirstName("Third");
            soldier3.setLastName("Grunt");
            soldier3.setGender(Mafioso.Gender.Female);

            Soldier soldier4 = new Soldier();
            soldier4.setFirstName("Fourth");
            soldier4.setLastName("Grunt");
            soldier4.setGender(Mafioso.Gender.Female);

            Soldier soldier5 = new Soldier();
            soldier5.setFirstName("Fifth");
            soldier5.setLastName("Grunt");
            soldier5.setGender(Mafioso.Gender.Male);

            Soldier soldier6 = new Soldier();
            soldier6.setFirstName("Sixth");
            soldier6.setLastName("Grunt");
            soldier6.setGender(Mafioso.Gender.Male);

            Soldier soldier7 = new Soldier();
            soldier7.setFirstName("Seventh");
            soldier7.setLastName("Grunt");
            soldier7.setGender(Mafioso.Gender.Male);

            Contract contract1 = new Contract();
            contract1.setDescription("Whack 707 family boss");

            Contract contract2 = new Contract();
            contract2.setDescription("Acquire semi-automatic guns");

            Contract contract3 = new Contract();
            contract3.setDescription("Set up new financing deals");

            // Populate the relationships.
            contract1.addSoldier(soldier1);
            contract1.addSoldier(soldier5);

            contract2.addSoldier(soldier1);
            contract2.addSoldier(soldier3);
            contract2.addSoldier(soldier7);

            contract3.addSoldier(soldier2);
            contract3.addSoldier(soldier3);
            contract3.addSoldier(soldier4);
            contract3.addSoldier(soldier5);

            boss.setUnderboss(underboss);

            capo1.setUnderboss(underboss);
            capo2.setUnderboss(underboss);

            capo1.addSoldier(soldier1);
            capo1.addSoldier(soldier2);

            capo2.addSoldier(soldier3);
            capo2.addSoldier(soldier4);
            capo2.addSoldier(soldier5);
            capo2.addSoldier(soldier6);
            capo2.addSoldier(soldier7);

            underboss.addCapo(capo1);
            underboss.addCapo(capo2);

            family.addMafioso(boss);
            family.addMafioso(underboss);

            family.addMafioso(capo1);
            family.addMafioso(capo2);

            family.addMafioso(soldier1);
            family.addMafioso(soldier2);
            family.addMafioso(soldier3);
            family.addMafioso(soldier4);
            family.addMafioso(soldier5);
            family.addMafioso(soldier6);
            family.addMafioso(soldier7);

            // Will cascade through the whole family.
            em.persist(family);
            family007 = family.getId();
            family007Mafiosos.add(boss.getId());
            family007Mafiosos.add(underboss.getId());
            family007Mafiosos.add(capo1.getId());
            family007Mafiosos.add(capo2.getId());
            family007Mafiosos.add(soldier1.getId());
            family007Mafiosos.add(soldier2.getId());
            family007Mafiosos.add(soldier3.getId());
            family007Mafiosos.add(soldier4.getId());
            family007Mafiosos.add(soldier5.getId());
            family007Mafiosos.add(soldier6.getId());
            family007Mafiosos.add(soldier7.getId());
            family007Contracts.add(contract1.getId());
            family007Contracts.add(contract2.getId());
            family007Contracts.add(contract3.getId());
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * This test is expected to fail.
     */
    public void testCreateEMF123WithoutAllTenantPropertiesSet() {
        EntityManager em = null;
        boolean exceptionCaught = false;
        try {
            em = create123EntityManager();
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } finally {
            if (em != null) {
                closeEntityManager(em);
            }

            closeEntityManagerFactory(getMULTI_TENANT_PU_123());
        }

        assertTrue("No exception received on a non shared emf without all tenant properties provided", exceptionCaught);
    }

    public void testCreateMafiaFamily123() {
        EntityManager em = createTenant123EntityManager();
        try {
            beginTransaction(em);

            MafiaFamily family = new MafiaFamily();
            family.setName("Galore");
            family.setRevenue(4321.03);
            family.addTag("newtag1");

            Boss boss = new Boss();
            boss.setFirstName("Kitty");
            boss.setLastName("Galore");
            boss.setGender(Mafioso.Gender.Female);
            boss.addReward("Reward 1");
            boss.addReward("Reward 2");
            boss.setAddress(new Address("1st Street", "Ottawa", "Ontario", "Canada", "B1T 2Y1"));

            Underboss underboss = new Underboss();
            underboss.setFirstName("Number2");
            underboss.setLastName("Galore");
            underboss.setGender(Mafioso.Gender.Male);
            underboss.addReward("Reward 3");
            underboss.addReward("Reward 4");
            underboss.setAddress(new Address("2nd Street", "Ottawa", "Ontario", "Canada", "B1T 2Y2"));

            Capo capo1 = new Capo();
            capo1.setFirstName("Capo");
            capo1.setLastName("Galore");
            capo1.setGender(Mafioso.Gender.Male);
            capo1.setAddress(new Address("3rd Street", "Ottawa", "Ontario", "Canada", "B1T 2Y3"));

            Capo capo2 = new Capo();
            capo2.setFirstName("Drill");
            capo2.setLastName("Bit");
            capo2.setGender(Mafioso.Gender.Male);
            capo2.setAddress(new Address("4th Street", "Ottawa", "Ontario", "Canada", "B1T 2Y4"));

            Soldier soldier1 = new Soldier();
            soldier1.setFirstName("Grunt");
            soldier1.setLastName("Galore");
            soldier1.setGender(Mafioso.Gender.Male);
            soldier1.addReward("Reward 5");
            soldier1.addReward("Reward 6");
            soldier1.addReward("Reward 7");
            soldier1.addReward("Reward 8");
            soldier1.setAddress(new Address("5th Street", "Ottawa", "Ontario", "Canada", "B1T 2Y5"));

            Soldier soldier2 = new Soldier();
            soldier2.setFirstName("Hammer");
            soldier2.setLastName("Head");
            soldier2.setGender(Mafioso.Gender.Male);
            soldier2.addReward("Reward 9");
            soldier2.addReward("Reward 10");
            soldier2.setAddress(new Address("6th Street", "Ottawa", "Ontario", "Canada", "B1T 2Y6"));

            Contract contract1 = new Contract();
            contract1.setDescription("Whack all other family bosses");

            Contract contract2 = new Contract();
            contract2.setDescription("Pillage, pillage, pillage!");

            // Populate the relationships.
            contract1.addSoldier(soldier1);
            contract2.addSoldier(soldier1);

            boss.setUnderboss(underboss);

            capo1.setUnderboss(underboss);
            capo1.addSoldier(soldier1);

            capo2.addSoldier(soldier2);

            underboss.addCapo(capo1);
            underboss.addCapo(capo2);

            family.addMafioso(boss);
            family.addMafioso(underboss);

            family.addMafioso(capo1);
            family.addMafioso(capo2);

            family.addMafioso(soldier1);
            family.addMafioso(soldier2);

            // Will cascade through the whole family.
            em.persist(family);
            capo123Id = capo1.getId();
            soldier123Id = soldier1.getId();
            family123 = family.getId();
            family123Mafiosos.add(boss.getId());
            family123Mafiosos.add(underboss.getId());
            family123Mafiosos.add(capo1.getId());
            family123Mafiosos.add(soldier1.getId());
            family123Contracts.add(contract1.getId());
            family123Contracts.add(contract2.getId());
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testValidateMafiaFamily707() {
        EntityManager em = createSharedEMFEntityManager();

        try {
            validateMafiaFamily707(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testValidateMafiaFamily007() {
        EntityManager em = createSharedEMFEntityManager();

        try {
            validateMafiaFamily007(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testValidateMafiaFamily707and007WithSameEM() {
        EntityManager em = createSharedEMFEntityManager();

        try {
            validateMafiaFamily707(em);
            validateMafiaFamily007(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testValidateMafiaFamily123() {
        EntityManager em = createTenant123EntityManager();

        try {
            clearCache(getMULTI_TENANT_PU_123());
            em.clear();

            MafiaFamily family =  em.find(MafiaFamily.class, family123);
            assertNotNull("The Mafia Family with id: " + family123 + ", was not found", family);
            assertTrue("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [1]", family.getTags().size() == 1);
            assertNull("The Mafia Family with id: " + family707 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family707));
            assertNull("The Mafia Family with id: " + family007 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family007));
            assertFalse("No mafiosos part of 123 family", family.getMafiosos().isEmpty());

            // See if we can find any members of the other family.
            for (Integer id : family707Mafiosos) {
                assertNull("Found family 707 mafioso.", em.find(Mafioso.class, id));
            }

            // Try a native sql query. Should get an exception since the
            // eclipselink.jdbc.allow-native-sql-queries property is set to
            // false for this PU.
            boolean exceptionCaught = false;
            List mafiaFamilies = null;
            try {
                mafiaFamilies = em.createNativeQuery("select * from JPA_MAFIA_FAMILY").getResultList();
            } catch (Exception e) {
                exceptionCaught = true;
            }

            assertTrue("No exception was caught from issuing a native query.", exceptionCaught);

            exceptionCaught = false;
            try {
                mafiaFamilies = em.createNamedQuery("findSQLMafiaFamilies").getResultList();
            } catch (Exception e) {
                exceptionCaught = true;
            }

            assertTrue("No exception was caught from issuing a named native query.", exceptionCaught);


            // Query directly for the boss from the other family.
            Boss otherBoss = em.find(Boss.class, family707Mafiosos.get(0));
            assertNull("Found family 707 boss.", otherBoss);

            // See if we can find any contracts of the other family.
            for (Integer id : family707Contracts) {
                assertNull("Found family 707 contract. ", em.find(Contract.class, id));
            }

            // Try a select named query
            List families = em.createNamedQuery("findAllMafiaFamilies").getResultList();
            assertTrue("Incorrect number of families were returned [" + families.size() + "], expected [1]",  families.size() == 1);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    protected void validateMafiaFamily007(EntityManager em) {
        clearCache(getMULTI_TENANT_PU());
        em.clear();
        beginTransaction(em);

        em.setProperty("tenant.id", "007");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");

        MafiaFamily family =  em.find(MafiaFamily.class, family007);
        assertNotNull("The Mafia Family with id: " + family007 + ", was not found", family);
        assertTrue("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [5]", family.getTags().size() == 5);
        assertNull("The Mafia Family with id: " + family707 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family707));
        assertNull("The Mafia Family with id: " + family123 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family123));
        assertFalse("No mafiosos part of 007 family", family.getMafiosos().isEmpty());

        // See if we can find any members of the other family.
        for (Integer id : family707Mafiosos) {
            assertNull("Found family 707 mafioso.", em.find(Mafioso.class, id));
        }

        // Query directly for the boss from the other family.
        Boss otherBoss = em.find(Boss.class, family707Mafiosos.get(0));
        assertNull("Found family 707 boss.", otherBoss);

        // See if we can find any contracts of the other family.
        for (Integer id : family707Contracts) {
            assertNull("Found family 707 contract.", em.find(Contract.class, id));
        }

        // Read and validate our contracts
        List<Contract> contracts = em.createNamedQuery("FindAllContracts").getResultList();
        assertTrue("Incorrect number of contracts were returned [" + contracts.size() + "], expected[3]", contracts.size() == 3);

        for (Contract contract : contracts) {
            assertFalse("Contract description was voided.", contract.getDescription().equals("voided"));
        }

        // Try a select named query
        List families = em.createNamedQuery("findAllMafiaFamilies").getResultList();
        assertTrue("Incorrect number of families were returned [" + families.size() + "], expected [1]",  families.size() == 1);

        // Find our boss and make sure his name has not been compromised from the 707 family.
        Boss boss = em.find(Boss.class, family007Mafiosos.get(0));
        assertFalse("The Boss name has been compromised", boss.getFirstName().equals("Compromised"));
        commitTransaction(em);
    }

    protected void validateMafiaFamily707(EntityManager em) {
        clearCache(getMULTI_TENANT_PU());
        em.clear();

        beginTransaction(em);

        em.setProperty("tenant.id", "707");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");
        MafiaFamily family = em.find(MafiaFamily.class, family707);
        assertNotNull("The Mafia Family with id: " + family707 + ", was not found", family);
        assertTrue("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [3]", family.getTags().size() == 3);
        assertNull("The Mafia Family with id: " + family007 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family007));
        assertNull("The Mafia Family with id: " + family123 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family123));
        assertFalse("No mafiosos part of 707 family", family.getMafiosos().isEmpty());

        // See if we can find any members of the other family.
        for (Integer id : family007Mafiosos) {
            assertNull("Found family 007 mafioso.", em.find(Mafioso.class, id));
        }

        // Query directly for the boss from the other family.
        Boss otherBoss = em.find(Boss.class, family007Mafiosos.get(0));
        assertNull("Found family 007 boss.", otherBoss);

        // See if we can find any contracts of the other family.
        for (Integer id : family007Contracts) {
            assertNull("Found family 007 contract.", em.find(Contract.class, id));
        }

        // Try deleting a contract from the 007 family.
        Query deleteQuery = em.createNamedQuery("DeleteContractByPrimaryKey");
        deleteQuery.setParameter("id", family007Contracts.get(0));
        int result = deleteQuery.executeUpdate();
        assertTrue("Was able to delete a contract from the 007 family", result == 0);

        // Update all our contract descriptions to be 'voided'
        Query updateAllQuery = em.createNamedQuery("UpdateAllContractDescriptions");
        updateAllQuery.executeUpdate();
        // Need to check that tenant id column is present
        assertTrue("Tenant discriminator column not found in update all query", ((EJBQueryImpl) updateAllQuery).getDatabaseQuery().getCall().getSQLString().contains("TENANT_ID"));

        // Read and validate the contracts
        List<Contract> contracts = em.createNamedQuery("FindAllContracts").getResultList();
        int contractNumber = contracts.size();
        assertTrue("Incorrect number of contracts were returned [" + contracts.size() + "], expected [3]", contracts.size() == 3);

        for (Contract contract : contracts) {
            assertTrue("Contract description was not voided.", contract.getDescription().equals("voided"));
        }

        // See how many soldiers are returned from a jpql query
        List soldiers = em.createQuery("SELECT s from Soldier s").getResultList();
        assertTrue("Incorrect number of soldiers were returned [" + soldiers.size() + "], expected [5]",  soldiers.size() == 5);

        if(getServerSession(getMULTI_TENANT_PU()).getPlatform().isSymfoware()) {
            getServerSession(getMULTI_TENANT_PU()).logMessage("Test AdvancedMultiTenantJunitTest partially skipped for this platform, "
                    +"which uses UpdateAll internally to check tenant-id when updating an entity using JOINED inheritance strategy. "
                    +"Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            commitTransaction(em);
            return;
        }

        // We know what the boss's id is for the 007 family to try to update him from the 707 pu.
        // The 007 family is validated after this test.
        Query query = em.createNamedQuery("UpdateBossName");
        query.setParameter("name", "Compromised");
        query.setParameter("id", family007Mafiosos.get(0));
        query.executeUpdate();
        commitTransaction(em);
    }

    public void testComplexMultitenantQueries() {
        EntityManager em = createTenant123EntityManager();

        try {
            clearCache(getMULTI_TENANT_PU_123());
            em.clear();

            // Try passing in a sub entity as a parameter.
            try {
                Query q = em.createQuery("SELECT s FROM Soldier s WHERE s.capo=?1");
                SubCapo subCapo = new SubCapo();
                subCapo.setId(capo123Id);
                q.setParameter(1, subCapo);
                List<Soldier> soldiers = q.getResultList();
                assertTrue("Incorrect number of soldiers returned [" + soldiers.size() +"], expected [1]", soldiers.size() == 1);
                assertTrue("Mafioso returned was not a soldier", soldiers.get(0).isSoldier());
                assertTrue("Soldier returned was not the expected soldier", soldiers.get(0).getId() == soldier123Id);
            } catch (Exception e) {
                fail("Exception encountered on named parameter query (with tenant discriminator columns) : " + e);
            }

            // Try a join fetch
            try {
                TypedQuery<MafiaFamily> q = em.createQuery("SELECT m FROM MafiaFamily m ORDER BY m.id DESC", MafiaFamily.class);
                q.setHint(QueryHints.FETCH, "m.mafiosos");
                q.getResultList();
            } catch (Exception e) {
                fail("Exception encountered on join fetch query (with tenant discriminator columns): " + e);
            }

            // Try a nested join fetch
            try {
                TypedQuery<MafiaFamily> q = em.createQuery("SELECT f FROM MafiaFamily f ORDER BY f.id ASC", MafiaFamily.class);
                q.setHint(QueryHints.FETCH, "f.mafiosos.rewards");
                q.getResultList();
            } catch (Exception e) {
                fail("Exception encountered on nested join fetch query (with tenant discriminator columns): " + e);
            }

            // Try a batch fetch
            try {
                TypedQuery<MafiaFamily> query = em.createQuery("SELECT f FROM MafiaFamily f", MafiaFamily.class);
                query.setHint(QueryHints.BATCH, "f.mafiosos");
                List<MafiaFamily> families = query.getResultList();

                // Should only be one family
                assertTrue("Incorrect number of families returned [" + families.size() +"], expected [1]", families.size() == 1);

                int size = families.get(0).getMafiosos().size();
                assertTrue("Incorrect number of mafiosos returned [" + size + "], expected [6]", size == 6);

            } catch (Exception e) {
                fail("Exception encountered on batch fetch query (with tenant discriminator columns): " + e);
            }

            // Try a multiple select
            try {
                Query query = em.createQuery("SELECT m.address, m.family FROM Mafioso m WHERE m.address.city = 'Ottawa' AND m.family.name LIKE 'Galore'", MafiaFamily.class);
                List results = query.getResultList();
                int size = results.size();
                assertTrue("Incorrect number of results returned [" + size + "], expected [6]", size == 6);
            } catch (Exception e) {
                fail("Exception encountered on mulitple select statement (with tenant discriminator columns): " + e);
            }

            // Try a delete all on single table (Contracts)
            try {
                beginTransaction(em);
                int contracts = em.createNamedQuery("FindAllContracts").getResultList().size();
                int deletes = em.createNamedQuery("DeleteAllContracts").executeUpdate();
                assertTrue("Incorrect number of contracts deleted [" + deletes + "], expected [" + contracts + "]", deletes == 2);
                commitTransaction(em);
            } catch (Exception e) {
                fail("Exception encountered on delete all query with single table (with tenant discriminator columns): " + e);
            }

            if(getServerSession(getMULTI_TENANT_PU_123()).getPlatform().isSymfoware()) {
                getServerSession(getMULTI_TENANT_PU_123()).logMessage("Test AdvancedMultiTenantJunitTest partially skipped for this platform, "
                        +"Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            } else {
                // Try a delete all on multiple table (MafiaFamily)
                try {
                    beginTransaction(em);
                    List<MafiaFamily> allFamilies = em.createNamedQuery("findAllMafiaFamilies").getResultList();
                    int families = allFamilies.size();
                    assertTrue("More than one family was found ["+ families +"]", families == 1);
                    Query deleteQuery = em.createNamedQuery("DeleteAllMafiaFamilies");
                    deleteQuery.setHint(QueryHints.ALLOW_NATIVE_SQL_QUERY, true);
                    int deletes = deleteQuery.executeUpdate();
                    assertTrue("Incorrect number of families deleted [" + deletes + "], expected [" + families + "]", deletes == 1);
                    commitTransaction(em);
                } catch (Exception e) {
                    fail("Exception encountered on delete all query with multiple table (with tenant discriminator columns): " + e);
                }
            }
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        // Some verification of what was deleted.
        EntityManager em007 = createSharedEMFEntityManager();
        try {
            List<MafiaFamily> families = em007.createNativeQuery("select * from JPA_MAFIA_FAMILY", MafiaFamily.class).getResultList();
            assertTrue("Incorrect number of families found through SQL [" + families.size() + "], expected [2]", families.size() == 2);

            // Clear out the shared cache with what we read through native SQL.
            clearCache(getMULTI_TENANT_PU());
            em007.clear();

            beginTransaction(em007);
            em007.setProperty("tenant.id", "007");
            em007.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");
            MafiaFamily family = em007.find(MafiaFamily.class, family007);
            assertFalse("Family 007 tags were nuked in delete all query above!", family.getTags().isEmpty());
            assertFalse("Family 007 revenue was nuked in delete all query above!", family.getRevenue() == null);
            commitTransaction(em007);
        } catch (Exception e) {
            fail("Exception caught: " + e);
        } finally {
            if (isTransactionActive(em007)) {
                rollbackTransaction(em007);
            }
            closeEntityManager(em007);
        }
    }

    /**
     * This test will try to DDL generate on deploy. Meaning once we try to
     * access the VPD PU we'll get an exception if we are not an oracle platform
     * so check before throwing an exception.
     *
     * VPD is currently supported only on Oracle.
     */
    public void testVPDEMPerTenant() {
        EntityManager em1 = null;
        EntityManager em2 = null;

        try {
            em1 = createVPDEntityManager();
            em1.setProperty("tenant.id", "bsmith@here.com");

            em2 = createVPDEntityManager();
            em2.setProperty("tenant.id", "gdune@there.ca");

            testInsertTask(em1, "blah", false);
            testInsertTask(em2, "halb", false);

            assertTrue("Found more than one result", em1.createQuery("Select t from Task t").getResultList().size() == 1);
            assertTrue("Found more than one result", em2.createQuery("Select t from Task t").getResultList().size() == 1);

            Task task1 = testInsertTaskWithOneSubtask(em1, "Rock that Propsal", false, "Write Proposal", false);
            assertNotNull(em1.find(Task.class, task1.getId()));
            assertNull(em2.find(Task.class, task1.getId())); // negative test

            Task task3 = testInsertTask(em2, "mow lawn", true);
            assertNull(em1.find(Task.class, task3.getId())); // negative test
            assertNotNull(em2.find(Task.class, task3.getId()));

            SubTask task4 = testInsertSubTaskObject(em1, "SubTask Object Creation", true);
            assertNotNull(em1.find(SubTask.class, task4.getId()));
            assertNull(em2.find(SubTask.class, task4.getId())); // negative test

        } catch (RuntimeException e) {
            if (em1 != null && isTransactionActive(em1)){
                rollbackTransaction(em1);
            }

            if (em2 != null && isTransactionActive(em2)){
                rollbackTransaction(em2);
            }

            if (! getPlatform(getMULTI_TENANT_VPD_PU()).isOracle()) {
                warning("VPD tests currently run only on an Oracle platform");
            } else {
                throw e;
            }
        } finally {
            if (em1 != null) {
                closeEntityManager(em1);
            }

            if (em2 != null) {
                closeEntityManager(em2);
            }
        }
    }

    public void testMultitenantPrimaryKeyWithIdClass() {
        EntityManager em = createSharedEMFEntityManager();

        PhoneNumber number = new PhoneNumber();;
        number.setAreaCode("613");
        number.setNumber("123-4567");
        number.setType("Home");

        try {
            beginTransaction(em);
            // On server side, you have to set the em properties after
            // transaction begins
            em.setProperty("tenant.id", "707");
            em.persist(number);
            commitTransaction(em);

            // This should hit the cache.
            beginTransaction(em);
            em.setProperty("tenant.id", "707");
            PhoneNumber refreshedNumber = em.find(PhoneNumber.class, number.buildPK());
            rollbackTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testMultitenantOneToOneReadObjectRead() {
        EntityManager em = createSharedEMFEntityManager();

        try {
            beginTransaction(em);

            em.setProperty("tenant.id", "371453");

            Envelope envelope = new Envelope();
            envelope.setColor("Red");

            Card card = new Card();
            card.setPrice(2.99);
            card.setColor("Yellow");
            card.setPrintYear(2012);
            card.setOccasion("Sympathy");
            card.setFrontCaption("Get well soon");
            card.setInsideCaption("Here's to a speedy recovery");

            card.setEnvelope(envelope);
            envelope.setCard(card);

            // Will cascade to the envelope
            em.persist(card);
            commitTransaction(em);

            em.clear();
            clearCache(getMULTI_TENANT_PU());

            beginTransaction(em);
            em.setProperty("tenant.id", "371453");
            envelope = em.merge(envelope);
            commitTransaction(em);

            em.clear();
            clearCache(getMULTI_TENANT_PU());

            beginTransaction(em);
            em.setProperty("tenant.id", "371453");
            card = em.merge(card);
            commitTransaction(em);

        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    private Task testInsertTask(EntityManager em, String description, boolean isCompleted) {
        beginTransaction(em);
        Task task = new Task();
        task.setDescription(description);
        task.setCompleted(isCompleted);
        em.persist(task);
        commitTransaction(em);
        return task;
    }

    private Task testInsertTaskWithOneSubtask(EntityManager em, String description, boolean isCompleted, String subtaskDesc, boolean isSubtaskCompleted) {
        beginTransaction(em);
        Task task = new Task();
        Task subtask = new Task();
        task.setDescription(description);
        task.setCompleted(isCompleted);
        subtask.setDescription(subtaskDesc);
        subtask.setCompleted(isSubtaskCompleted);
        task.addSubtask(subtask);
        em.persist(subtask);
        em.persist(task);
        commitTransaction(em);
        return task;
    }

    private SubTask testInsertSubTaskObject(EntityManager em, String description, boolean isCompleted) {
        beginTransaction(em);
        SubTask subTask = new SubTask();
        subTask.setDescription(description);
        subTask.setCompleted(isCompleted);
        em.persist(subTask);
        commitTransaction(em);
        return subTask;
    }
}
