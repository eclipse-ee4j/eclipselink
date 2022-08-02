/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2010 Frank Schwarz. All rights reserved.
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
//     08/20/2008-1.0.1 Nathan Beyer (Cerner)
//       - 241308: Primary key is incorrectly assigned to embeddable class
//                 field with the same name as the primary key field's name
//     01/12/2009-1.1 Daniel Lo, Tom Ware, Guy Pelletier
//       - 247041: Null element inserted in the ArrayList
//     07/17/2009 - tware -  added tests for DDL generation of maps
//     01/22/2010-2.0.1 Guy Pelletier
//       - 294361: incorrect generated table for element collection attribute overrides
//     06/14/2010-2.2 Guy Pelletier
//       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
//     09/15/2010-2.2 Chris Delahunt
//       - 322233 - AttributeOverrides and AssociationOverride dont change field type info
//     11/17/2010-2.2.0 Chris Delahunt
//       - 214519: Allow appending strings to CREATE TABLE statements
//     11/23/2010-2.2 Frank Schwarz
//       - 328774: TABLE_PER_CLASS-mapped key of a java.util.Map does not work for querying
//     01/04/2011-2.3 Guy Pelletier
//       - 330628: @PrimaryKeyJoinColumn(...) is not working equivalently to @JoinColumn(..., insertable = false, updatable = false)
//     01/06/2011-2.3 Guy Pelletier
//       - 312244: can't map optional one-to-one relationship using @PrimaryKeyJoinColumn
//     01/11/2011-2.3 Guy Pelletier
//       - 277079: EmbeddedId's fields are null when using LOB with fetchtype LAZY
package org.eclipse.persistence.testing.tests.jpa.ddlgeneration.tableperclass;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.CodeExample;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.DesignPattern;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.GoldBenefit;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.GoldCustomer;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.LuxuryCar;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.PlatinumBenefit;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.PlatinumCustomer;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.ProgrammingLanguage;
import org.eclipse.persistence.testing.tests.jpa.ddlgeneration.DDLGenerationTestBase;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * JUnit test case(s) for DDL generation.
 */
public class DDLTablePerClassTest extends DDLGenerationTestBase {

    public DDLTablePerClassTest() {
        super();
    }

    public DDLTablePerClassTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "ddlTablePerClass";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLTablePerClassTest");
        suite.addTest(new DDLTablePerClassTest("testSetup"));
        suite.addTest(new DDLTablePerClassTest("testDDLTablePerClassModel"));
        suite.addTest(new DDLTablePerClassTest("testDDLTablePerClassModelQuery"));
        suite.addTest(new DDLTablePerClassTest("testTPCMappedKeyMapQuery"));
        suite.addTest(new DDLTablePerClassTest("testLazyBlob"));
        return suite;
    }

    public void testDDLTablePerClassModel() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        GoldCustomer goldCustomer = new GoldCustomer();
        PlatinumCustomer platinumCustomer = new PlatinumCustomer();

        try {
            goldCustomer.setFullName("GoldCustomer");
            goldCustomer.setAge(21);
            goldCustomer.setGender("Male");
            goldCustomer.setNationality("Canadian");
            em.persist(goldCustomer);

            platinumCustomer.setFullName("PlatinumCustomer");
            platinumCustomer.setAge(22);
            platinumCustomer.setGender("Female");
            platinumCustomer.setNationality("American");
            em.persist(platinumCustomer);

            LuxuryCar luxuryCar1 = new LuxuryCar();
            em.persist(luxuryCar1);

            GoldBenefit goldBenefit1 = new GoldBenefit();
            goldBenefit1.setBenefitDescription("Gold benefit 1");
            goldBenefit1.setLuxuryCar(luxuryCar1);
            goldBenefit1.setCustomer(goldCustomer);
            em.persist(goldBenefit1);

            LuxuryCar luxuryCar2 = new LuxuryCar();
            em.persist(luxuryCar2);

            GoldBenefit goldBenefit2 = new GoldBenefit();
            goldBenefit2.setBenefitDescription("Gold benefit 2");
            goldBenefit2.setLuxuryCar(luxuryCar2);
            em.persist(goldBenefit2);

            LuxuryCar luxuryCar3 = new LuxuryCar();
            em.persist(luxuryCar3);

            PlatinumBenefit platinumBenefit1 = new PlatinumBenefit();
            platinumBenefit1.setBenefitDescription("Platinum benefit 1");
            platinumBenefit1.setCar(luxuryCar3);
            em.persist(platinumBenefit1);

            List<GoldBenefit> goldBenefits1 = new ArrayList<>();
            List<GoldBenefit> goldBenefits2 = new ArrayList<>();
            List<PlatinumBenefit> platinumBenefits1 = new ArrayList<>();

            goldCustomer.setGoldBenefitList(goldBenefits1);
            platinumCustomer.setGoldBenefitList(goldBenefits2);
            platinumCustomer.setPlatinumBenefitList(platinumBenefits1);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }

        clearCache();
        em = createEntityManager();

        assertFalse("ReadAll did not find subclasses.", em.createQuery("Select c from Customer c").getResultList().isEmpty());

        GoldCustomer refreshedGoldCustomer = em.find(GoldCustomer.class, goldCustomer.getCustomerId());
        assertTrue("The gold customer read back did not match the original", getPersistenceUnitServerSession().compareObjects(goldCustomer, refreshedGoldCustomer));

        PlatinumCustomer refreshedPlatinumCustomer = em.find(PlatinumCustomer.class, platinumCustomer.getCustomerId());
        assertTrue("The platinum customer read back did not match the original", getPersistenceUnitServerSession().compareObjects(platinumCustomer, refreshedPlatinumCustomer));
    }

    public void testDDLTablePerClassModelQuery() {
        EntityManager em = createEntityManager();

        List<?> goldCustomers = em.createNamedQuery("GoldCustomer.findAll").getResultList();
        List<?> platinumCustomers = em.createNamedQuery("PlatinumCustomer.findAll").getResultList();

        assertFalse("No gold customers returned", goldCustomers.isEmpty());
        assertFalse("No platinum customers returned", platinumCustomers.isEmpty());
    }

    // Test for bug 328774
    public void testTPCMappedKeyMapQuery() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            ProgrammingLanguage java = new ProgrammingLanguage();
            java.setName("Java");

            DesignPattern designPattern = new DesignPattern();
            designPattern.setName("Singleton");
            CodeExample codeExample = new CodeExample();
            codeExample.setContent("...");
            designPattern.getCodeExamples().put(java, codeExample);

            em.persist(java);
            em.persist(designPattern);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("Error persisting the PropertyRecord : " + e);
        } finally {
            closeEntityManager(em);
        }

        clearCache();
        em = createEntityManager();

        TypedQuery<DesignPattern> query = em.createQuery("SELECT x FROM DesignPattern x", DesignPattern.class);
        List<DesignPattern> resultList = query.getResultList();
        assertEquals("Unexpected number of design patterns returned", 1, resultList.size());
        closeEntityManager(em);
    }

    // Bug 357670
    public void testLazyBlob(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        LuxuryCar car = new LuxuryCar();
        try {
            java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream();
            java.io.PrintWriter pw = new PrintWriter(baos);
            pw.print("TestString1");
            pw.close();
            baos.close();
            car.setPic( baos.toByteArray());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("we failed!!!");
        }
        em.persist(car);
        em.flush();
        em.refresh(car);
        commitTransaction(em);
        em.clear();

        car = em.find(LuxuryCar.class, car.getRegNumber());
        byte[] pic = car.getPic();
        assertNotNull("Blob was null after flush, refresh, commit.", pic);
    }
}
