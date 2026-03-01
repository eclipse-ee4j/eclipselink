/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014 IBM Corporation. All rights reserved.
 * Copyright (c) 2010 Frank Schwarz. All rights reserved.
 * Copyright (c) 2008 Daryl Davis. All rights reserved.
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
//     04/28/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 6)
//     31/05/2012-2.4 Guy Pelletier
//       - 381196: Multitenant persistence units with a dedicated emf should allow for DDL generation.
//     09/10/2008-2.4.1 Daryl Davis
//       - 386939: @ManyToMany Map<Entity,Entity> unidirectional reverses Key and Value fields on Update
//     07/07/2014-2.5.3 Rick Curtis
//       - 375101: Date and Calendar should not require @Temporal.
package org.eclipse.persistence.testing.tests.jpa.ddlgeneration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Address;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.AggregateMapKey;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.AggregateMapValue;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.CKeyEntityA;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.CKeyEntityAPK;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.CKeyEntityB;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.CKeyEntityB2;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.CKeyEntityBPK;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.CKeyEntityC;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.CKeyEntityCPK;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.City;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Comment;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Country;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Course;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Employee;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.EntityMapKey;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.EntityMapValue;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.EntityMapValueWithBackPointer;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Inventor;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Lobtest;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.LobtestPK;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.MMEntityMapValue;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.MachineState;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.MapHolder;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.MapHolderEmbeddable;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Money;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Patent;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.PatentCollection;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.PatentInvestigation;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.PatentInvestigator;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.PropertyInfo;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.PropertyRecord;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Purchase;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.State;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.ThreadInfo;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.UniqueConstraintsEntity1;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.UniqueConstraintsEntity2;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Zip;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.ZipArea;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.Zipcode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JUnit test case(s) for DDL generation.
 */
public class DDLGenerationTest extends DDLGenerationTestBase {

    public DDLGenerationTest() {
        super();
    }

    public DDLGenerationTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "ddlGeneration";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLGenerationTest");
        suite.addTest(new DDLGenerationTest("testSetup"));
        List<String> tests = new ArrayList<>();
        //Some of these tests need to be run in a particular order.

        tests.add("testDDLPkConstraintErrorIncludingRelationTableColumnName");
        tests.add("testOptionalPrimaryKeyJoinColumnRelationship");
        tests.add("testPrimaryKeyJoinColumns");
        tests.add("testDDLEmbeddableMapKey");
        tests.add("testDDLAttributeOverrides");
        tests.add("testDDLAttributeOverridesOnElementCollection");
        tests.add("testDDLUniqueKeysAsJoinColumns");
        tests.add("testSimpleSelectFoo");
        tests.add("testElementMapOnEmbedded");
        tests.add("testDDLUniqueConstraintsByAnnotations");
        tests.add("testDDLUniqueConstraintsByXML");
        tests.add("testDDLSubclassEmbeddedIdPkColumnsInJoinedStrategy");
        tests.add("testBug241308");
        tests.add("testDDLUnidirectionalOneToMany");
        tests.add("testDirectCollectionMapping");
        tests.add("testAggregateCollectionMapping");
        tests.add("testOneToManyMapping");
        tests.add("testUnidirectionalOneToManyMapping");
        tests.add("testManyToManyMapping");
        tests.add("testManyToManyWithMultipleJoinColumns");
        tests.add("testEmbeddedManyToMany");
        tests.add("testEmbeddedOneToOne");
        tests.add("testAssociationOverrideToEmbeddedManyToMany");
        tests.add("testDeleteObjectWithEmbeddedManyToMany");
        tests.add("testLAZYLOBWithEmbeddedId");
        tests.add("testBug386939");

        for (String test : tests) {
            suite.addTest(new DDLGenerationTest(test));
        }

        return suite;
    }

    // Test for GF#1392
    // If there is a same name column for the entity and many-to-many table, wrong pk constraint generated.
    public void testDDLPkConstraintErrorIncludingRelationTableColumnName() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

            CKeyEntityC c = new CKeyEntityC(new CKeyEntityCPK("Manager"));
            em.persist(c);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            fail("DDL generation may generate wrong Primary Key constraint, thrown:" + e);
        } finally {
            closeEntityManager(em);
        }
    }

    // Test for bug 312244
    public void testOptionalPrimaryKeyJoinColumnRelationship() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            em.persist(new Course());
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("Error persisting new course without material failed : " + e);
        } finally {
            closeEntityManager(em);
        }
    }

    // Test for bug 330628
    public void testPrimaryKeyJoinColumns() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Country country = new Country();
            country.setName("USA");
            country.setIsoCode("840");

            State state = new State();
            state.setCountry(country);
            state.setIsoCode("36");
            state.setName("New York");

            Set<State> states = new HashSet<>();
            states.add(state);
            country.setStates(states);

            City city = new City();
            city.setName("Rochester");
            city.setState(state);

            Set<City> cities = new HashSet<>();
            cities.add(city);
            state.setCities(cities);

            ZipArea zipArea = new ZipArea();
            zipArea.setCity(city);

            Set<ZipArea> zipAreas = new HashSet<>();
            zipAreas.add(zipArea);
            city.setZipAreas(zipAreas);

            Zip zip = new Zip();
            zip.setCode("14621");
            zip.setCountry(country);
            zip.setZipAreas(zipAreas);
            zipArea.setZip(zip);

            Set<Zip> zips = new HashSet<>();
            country.setZips(zips);

            em.persist(country);
            em.persist(state);
            em.persist(city);
            em.persist(zipArea);
            em.persist(zip);

            em.flush();

        } catch (RuntimeException e) {
            fail("Error persisting new country with city, states and zips : " + e);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    // Test for bug 294361
    public void testDDLEmbeddableMapKey() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            PropertyRecord propertyRecord = new PropertyRecord();

            Zipcode zipCode = new Zipcode();
            zipCode.plusFour = "1234";
            zipCode.zip = "78627";

            Address address = new Address();
            address.city = "Ottawa";
            address.state = "Ontario";
            address.street = "Main";
            address.zipcode = zipCode;

            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.parcelNumber = 1;
            propertyInfo.size = 2;
            propertyInfo.tax = new BigDecimal(10);

            propertyRecord.propertyInfos.put(address, propertyInfo);

            em.persist(propertyRecord);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("Error persisting the PropertyRecord : " + e);
        } finally {
            closeEntityManager(em);
        }
    }

    // Test for bug 322233, that optional field info defined in Overrides are used in DDL
    public void testDDLAttributeOverrides() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Exception expectedException = null;
        try {
            Purchase purchase = new Purchase();
            purchase.setFee(new Money());
            em.persist(purchase);

            em.flush();
        } catch (RuntimeException e) {
            //test expects flush to throw an exception because the FEE_AMOUNT field is null
            expectedException = e;
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        assertNotNull("Expected an exception persisting null into a field with nullable=false set on an override", expectedException);
    }

    // Test for bug 322233, that optional field info defined in Overrides are used in DDL
    public void testDDLAttributeOverridesOnElementCollection() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Exception expectedException = null;
        try {
            PropertyRecord propertyRecord = new PropertyRecord();

            Zipcode zipCode = new Zipcode();
            zipCode.plusFour = "1234";
            zipCode.zip = "78627";

            Address address = new Address();
            address.city = "Ottawa";
            address.state = "Ontario";
            address.street = "Main";
            address.zipcode = zipCode;

            PropertyInfo propertyInfo = new PropertyInfo();
            //propertyInfo.parcelNumber = 1;//Keep this as null, to test the nullable=false setting was used
            propertyInfo.size = 2;
            propertyInfo.tax = new BigDecimal(10);

            propertyRecord.propertyInfos.put(address, propertyInfo);

            em.persist(propertyRecord);
            em.flush();
        } catch (RuntimeException e) {
            //test expects flush to throw an exception because the PARCEL_NUMBER field is null
            expectedException = e;
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        assertNotNull("Expected an exception persisting null into a field with nullable=false set on an override", expectedException);
    }

    // Test for relationships using candidate(unique) keys
    public void testDDLUniqueKeysAsJoinColumns() {
        CKeyEntityAPK aKey;
        CKeyEntityBPK bKey;

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CKeyEntityA a = new CKeyEntityA("Wonseok", "Kim");
            int seq = (int)System.currentTimeMillis(); // just to get unique value :-)
            CKeyEntityB b = new CKeyEntityB(new CKeyEntityBPK(seq, "B1209"));
            //set unique keys
            b.setUnq1("u0001");
            b.setUnq2("u0002");

            a.setUniqueB(b);
            b.setUniqueA(a);

            em.persist(a);
            em.persist(b);

            commitTransaction(em);

            aKey = a.getKey();
            bKey = b.getKey();
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }
        //clearCache();

        em = createEntityManager();
        beginTransaction(em);
        try {
            CKeyEntityA a = em.find(CKeyEntityA.class, aKey);
            assertNotNull(a);

            CKeyEntityB b = a.getUniqueB();
            assertNotNull(b);

            assertEquals(b.getUnq1(), "u0001");
            assertEquals(b.getUnq2(), "u0002");

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }
        //clearCache();

        em = createEntityManager();
        beginTransaction(em);
        try {

            CKeyEntityB b = em.find(CKeyEntityB.class, bKey);
            assertNotNull(b);

            CKeyEntityA a = b.getUniqueA();
            assertNotNull(a);
            assertEquals(a.getKey(), aKey);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }

    }

    // Test to check if unique constraints are generated correctly
    public void testDDLUniqueConstraintsByAnnotations() {
        // Some database do not support constraints, Sybase does not raise any errors (until commit perhaps?)
        if(!getPersistenceUnitServerSession().getPlatform().supportsUniqueKeyConstraints()
                || getPersistenceUnitServerSession().getPlatform().isSybase()) {
            return;
        }
        UniqueConstraintsEntity1 ucEntity;

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            ucEntity = em.find(UniqueConstraintsEntity1.class, 1);
            if(ucEntity == null) {
                ucEntity = new UniqueConstraintsEntity1(1);
                ucEntity.setColumns(1, 1, 1, 1);
                em.persist(ucEntity);
            }
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
            throw e;
        }

        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity1(2);
            ucEntity.setColumns(1, 2, 2, 2);
            em.persist(ucEntity);
            em.flush();

            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }

        em = createEntityManager();
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity1(2);
            ucEntity.setColumns(2, 1, 2, 2);
            em.persist(ucEntity);
            em.flush();

            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }

        em = createEntityManager();
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity1(2);
            ucEntity.setColumns(2, 2, 1, 1);
            em.persist(ucEntity);
            em.flush();

            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }

        em = createEntityManager();
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity1(2);
            ucEntity.setColumns(2, 2, 1, 2);
            em.persist(ucEntity);
            em.flush();
        } catch (PersistenceException e) {
            fail("Unique constraint violation is not expected, thrown:" + e);
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    // Test to check if unique constraints are generated correctly
    public void testDDLUniqueConstraintsByXML() {
        // Some database do not support constraints, Sybase does not raise any errors (until commit perhaps?)
        if(!getPersistenceUnitServerSession().getPlatform().supportsUniqueKeyConstraints()
                || getPersistenceUnitServerSession().getPlatform().isSybase()) {
            return;
        }
        UniqueConstraintsEntity2 ucEntity;

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            ucEntity = em.find(UniqueConstraintsEntity2.class, 1);
            if(ucEntity == null) {
                ucEntity = new UniqueConstraintsEntity2(1);
                ucEntity.setColumns(1, 1, 1, 1);
                em.persist(ucEntity);
            }
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
            throw e;
        }

        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity2(2);
            ucEntity.setColumns(1, 2, 2, 2);
            em.persist(ucEntity);
            em.flush();

            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }

        em = createEntityManager();
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity2(2);
            ucEntity.setColumns(2, 1, 2, 2);
            em.persist(ucEntity);
            em.flush();

            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }

        em = createEntityManager();
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity2(2);
            ucEntity.setColumns(2, 2, 1, 1);
            em.persist(ucEntity);
            em.flush();

            fail("Unique constraint violation is expected");
        } catch (PersistenceException e) {
            //expected
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }

        em = createEntityManager();
        beginTransaction(em);
        try {
            ucEntity = new UniqueConstraintsEntity2(2);
            ucEntity.setColumns(2, 2, 1, 2);
            em.persist(ucEntity);
            em.flush();
        } catch (PersistenceException e) {
            fail("Unique constraint violation is not expected, thrown:" + e);
        } finally {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    // test if the primary key columns of subclass entity whose root entity has EmbeddedId
    // are generated properly in joined inheritance strategy
    // Issue: GF#1391
    public void testDDLSubclassEmbeddedIdPkColumnsInJoinedStrategy() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // let's see if a subclass entity is persisted and found well
        try {
            int seq = (int)System.currentTimeMillis(); // just to get unique value :-)
            String code = "B1215";
            CKeyEntityB2 b = new CKeyEntityB2(new CKeyEntityBPK(seq, code));
            //set unique keys
            b.setUnq1(String.valueOf(seq));
            b.setUnq2(String.valueOf(seq));

            em.persist(b);
            em.flush();
            String query = "SELECT b FROM CKeyEntityB2 b WHERE b.key.seq = :seq AND b.key.code = :code";
            Object result = em.createQuery(query).setParameter("seq", seq).setParameter("code", code)
                            .getSingleResult();
            assertNotNull(result);

            rollbackTransaction(em);

        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    // Bug 241308 - Primary key is incorrectly assigned to embeddable class
    // field with the same name as the primary key field's name
    public void testBug241308() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            ThreadInfo threadInfo1 = new ThreadInfo();
            threadInfo1.setId(0);
            threadInfo1.setName("main");

            MachineState machineState1 = new MachineState();
            machineState1.setId(0);
            machineState1.setThread(threadInfo1);

            em.persist(machineState1);

            ThreadInfo threadInfo2 = new ThreadInfo();
            threadInfo2.setId(0);
            threadInfo2.setName("main");

            MachineState machineState2 = new MachineState();
            machineState2.setId(1);
            machineState2.setThread(threadInfo2);

            em.persist(machineState2);

            commitTransaction(em);

        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testDDLUnidirectionalOneToMany() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            // add comments
            long seq = System.currentTimeMillis(); // just to get unique value :-)
            CKeyEntityB b = new CKeyEntityB(new CKeyEntityBPK(seq, "B1210"));
            List<Comment<String>> comments = new ArrayList<>(2);
            comments.add(new Comment<>("comment 1"));
            comments.add(new Comment<>("comment 2"));
            b.setComments(comments);
            //set unique keys
            b.setUnq1("u0003");
            b.setUnq2("u0004");
            em.persist(b);
            commitTransaction(em);

            // clean-up
            beginTransaction(em);
            CKeyEntityB b0 = em.find(CKeyEntityB.class, b.getKey());
            em.remove(b0.getComments().get(0));
            em.remove(b0.getComments().get(1));
            em.remove(b0);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }


    public void testDirectCollectionMapping(){
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);

            MapHolder holder = new MapHolder();
            holder.setId(1);
            EntityMapKey key = new EntityMapKey();
            key.setId(1);
            holder.getDCMap().put(key, "test1");
            em.persist(holder);
            em.persist(key);

            try{
                em.flush();
            } catch (Exception e){
                e.printStackTrace();
                fail("Caught Exception while trying to flush a new ddl-generated DirectCollectionMapping." + e);
            }

            clearCache();
            em.refresh(holder);
            assertNotNull(holder.getDCMap().get(key));

            holder.getDCMap().remove(key);
            em.remove(holder);
            em.remove(key);

            em.flush();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testAggregateCollectionMapping(){
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);

            MapHolder holder = new MapHolder();
            holder.setId(2);
            EntityMapKey key = new EntityMapKey();
            key.setId(2);
            AggregateMapValue value = new AggregateMapValue();
            value.setDescription("test2");
            holder.getACMap().put(key, value);
            em.persist(holder);
            em.persist(key);

            em.flush();

            clearCache();
            em.refresh(holder);
            assertNotNull(holder.getACMap().get(key));

            holder.getACMap().remove(key);
            em.remove(holder);
            em.remove(key);

            em.flush();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testOneToManyMapping(){
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);

            MapHolder holder = new MapHolder();
            holder.setId(3);
            AggregateMapKey key = new AggregateMapKey();
            key.setDescription("test3");
            EntityMapValueWithBackPointer value = new EntityMapValueWithBackPointer();
            value.setHolder(holder);
            value.setId(3);
            holder.getOTMMap().put(key, value);
            em.persist(holder);
            em.persist(value);

            em.flush();

            clearCache();
            em.refresh(holder);
            holder.getOTMMap().get(key);
            assertNotNull(holder.getOTMMap().get(key));

            holder.getOTMMap().remove(key);
            value.setHolder(null);
            em.remove(holder);
            em.remove(value);

            em.flush();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testUnidirectionalOneToManyMapping(){
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);

            MapHolder holder = new MapHolder();
            holder.setId(4);
            EntityMapValue value = new EntityMapValue();
            value.setId(4);
            holder.getUOTMMap().put(4, value);
            em.persist(holder);
            em.persist(value);

            em.flush();

            clearCache();
            em.refresh(holder);
            assertNotNull(holder.getUOTMMap().get(4));

            holder.getUOTMMap().remove(4);
            em.remove(holder);
            em.remove(value);

            em.flush();
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testManyToManyMapping(){
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);

            MapHolder holder = new MapHolder();
            holder.setId(5);
            EntityMapKey key = new EntityMapKey();
            key.setId(5);
            MMEntityMapValue value = new MMEntityMapValue();
            value.getHolders().add(holder);
            value.setId(5);
            holder.getMTMMap().put(key, value);
            em.persist(holder);
            em.persist(key);
            em.persist(value);

            em.flush();

            clearCache();
            em.refresh(holder);
            assertNotNull(holder.getMTMMap().get(key));

            holder.getMTMMap().remove(key);
            value.getHolders().remove(0);
            em.remove(holder);
            em.remove(key);
            em.remove(value);

            try{
                em.flush();
            } catch (Exception e){
                e.printStackTrace();
                fail("Caught Exception while trying to remove a new ddl-generated OneToManyMapping." + e);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    // Bug 279784 - Incomplete OUTER JOIN based on JoinTable
    public void testManyToManyWithMultipleJoinColumns() {
        String prefix = "testManyToManyWithMultipleJoinColumns";
        EntityManager em = createEntityManager();
        beginTransaction(em);

        // persist and flush c1, cache its pk
        CKeyEntityC c1 = new CKeyEntityC(new CKeyEntityCPK(prefix + "_1"));
        c1.setTempRole(prefix + "_1_Role");
        em.persist(c1);
        // that insures that sequencing has worked
        em.flush();
        CKeyEntityCPK c1KeyPK = c1.getKey();

        // create b1 with the same (seq, code) as c1's (seq, role); add to c1
        List<CKeyEntityB> bs1 = new ArrayList<>();
        CKeyEntityBPK b1KeyPK = new CKeyEntityBPK(c1KeyPK.seq, c1KeyPK.role);
        CKeyEntityB b1 = new CKeyEntityB(b1KeyPK);
        b1.setUnq1(prefix + "_1_1");
        b1.setUnq2(prefix + "_1_2");
        bs1.add(b1);
        c1.setBs(bs1);
        em.persist(b1);

        // create c2 with the same seq as c1, but with different role
        CKeyEntityC c2 = new CKeyEntityC(new CKeyEntityCPK(prefix + "_2"));
        c2.setTempRole(prefix + "_2_Role");
        CKeyEntityCPK c2KeyPK = c2.getKey();
        c2KeyPK.seq = c1KeyPK.seq;

        // create b2 with the same (seq, code) as c2's (seq, role); add to c2
        List<CKeyEntityB> bs2 = new ArrayList<>();
        CKeyEntityBPK b2KeyPK = new CKeyEntityBPK(c2KeyPK.seq, c2KeyPK.role);
        CKeyEntityB b2 = new CKeyEntityB(b2KeyPK);
        b2.setUnq1(prefix + "_2_1");
        b2.setUnq2(prefix + "_2_2");
        bs2.add(b2);
        c2.setBs(bs2);

        // persist c2
        em.persist(c2);
        em.persist(b2);

        commitTransaction(em);
        closeEntityManager(em);

        clearCache();
        em = createEntityManager();
        List<?> clist = em.createQuery("SELECT c from CKeyEntityC c LEFT OUTER JOIN c.bs bs WHERE c.key = :key").setParameter("key", c1KeyPK).getResultList();
        // verify
        String errorMsg = "";
        int nSize = clist.size();
        if(nSize == 1) {
            // Expected result - nothing to do. Correct sql has been generated (the second ON clause is correct):
            // SELECT t1.C_ROLE, t1.SEQ, t1.ROLE_, t1.A_SEQ, t1.A_L_NAME, t1.A_F_NAME
            // FROM DDL_CKENTC t1 LEFT OUTER JOIN (DDL_CKENT_C_B t2 JOIN DDL_CKENTB t0 ON ((t0.CODE = t2.B_CODE) AND (t0.SEQ = t2.B_SEQ))) ON ((t2.C_ROLE = t1.C_ROLE) AND (t2.C_SEQ = t1.SEQ))
            // WHERE ((t1.SEQ = 1) AND (t1.ROLE_ = testManyToManyWithMultipleJoinColumns_1))

        } else if(nSize == 2) {
            if(clist.get(0) != clist.get(1)) {
                errorMsg = "Read 2 cs, but they are not identical - test problem.";
            } else {
                // That wrong sql was generated before the fix (the second ON clause was incorrect):
                // SELECT t1.C_ROLE, t1.SEQ, t1.ROLE_, t1.A_SEQ, t1.A_L_NAME, t1.A_F_NAME
                // FROM DDL_CKENTC t1 LEFT OUTER JOIN (DDL_CKENT_C_B t2 JOIN DDL_CKENTB t0 ON ((t0.CODE = t2.B_CODE) AND (t0.SEQ = t2.B_SEQ))) ON (t2.C_SEQ = t1.SEQ)
                // WHERE ((t1.SEQ = 1) AND (t1.ROLE_ = testManyToManyWithMultipleJoinColumns_1))
                // That caused picking up two CKeyEntityB objects instead of one (because they both have the same SEQ),
                // outer joining causes return of two identical copies of c1.
                errorMsg = "Read 2 identical cs instead of one - likely the second on clause was incomplete";
            }
        } else {
            errorMsg = "Read "+nSize+" cs, 1 or 2 were expected - test problem.";
        }

        // clean-up
        beginTransaction(em);
        c1 = em.find(CKeyEntityC.class, c1KeyPK);
        c1.getBs().clear();
        em.remove(c1);
        c2 = em.find(CKeyEntityC.class, c2KeyPK);
        c2.getBs().clear();
        em.remove(c2);
        b1 = em.find(CKeyEntityB.class, b1KeyPK);
        em.remove(b1);
        b2 = em.find(CKeyEntityB.class, b2KeyPK);
        em.remove(b2);
        commitTransaction(em);
        closeEntityManager(em);

        if(!errorMsg.isEmpty()) {
            fail(errorMsg);
        }
    }

    public void testEmbeddedManyToMany(){
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            Inventor inventor = new Inventor();
            inventor.setId(1);
            Patent patent = new Patent();
            patent.setId(1);
            PatentCollection patents = new PatentCollection();
            patents.getPatents().add(patent);
            inventor.setPatentCollection(patents);
            em.persist(inventor);
            em.persist(patent);
            em.flush();
            em.clear();
            clearCache();

            inventor = em.find(Inventor.class, 1);
            assertNotNull("Embeddable is null", inventor.getPatentCollection());
            assertFalse("Collection is empty", inventor.getPatentCollection().getPatents().isEmpty());
            assertEquals("Target is incorrect", 1, inventor.getPatentCollection().getPatents().get(0).getId());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testEmbeddedOneToOne(){
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            PatentInvestigator investigator = new PatentInvestigator();
            investigator.setId(1);
            Patent lastPatent = new Patent();
            lastPatent.setId(1);
            investigator.setLastCompleted(new PatentInvestigation("Last", lastPatent));
            em.persist(investigator);
            em.persist(lastPatent);
            em.flush();
            em.clear();

            PatentInvestigator investigatorRead = em.find(PatentInvestigator.class, investigator.getId());
            assertNotNull("investigatorRead.getLast() == null", investigatorRead.getLast());
            assertNotNull("investigatorRead.getLast.getPatent() == null", investigatorRead.getLast().getPatent());
            assertEquals("investigatorRead.getLast.getPatent().getId() is incorrect", investigatorRead.getLast().getPatent().getId(), lastPatent.getId());
            assertNull("investigatorRead.getCurrent() != null", investigatorRead.getCurrent());
            assertNull("investigatorRead.getNext() != null", investigatorRead.getNext());

            Patent currentPatent = new Patent();
            currentPatent.setId(2);
            em.persist(currentPatent);
            em.flush();
            em.createNativeQuery("UPDATE " + em.unwrap(ServerSession.class).getDescriptor(PatentInvestigator.class).getDefaultTable().getName() + " SET CURRENT_DESRIPTION = 'Current', CURRENT_PATENT = " + currentPatent.getId() + " WHERE ID = " + investigator.getId()).executeUpdate();
            em.refresh(investigatorRead);
            assertNotNull("after refresh investigatorRead.getLast() == null", investigatorRead.getLast());
            assertNotNull("after refresh investigatorRead.getLast.getPatent() == null", investigatorRead.getLast().getPatent());
            assertEquals("after refresh investigatorRead.getLast.getPatent().getId() is incorrect", investigatorRead.getLast().getPatent().getId(), lastPatent.getId());
            assertNotNull("after refresh investigatorRead.getCurrent() == null", investigatorRead.getCurrent());
            assertNotNull("after refresh investigatorRead.getCurrent.getPatent() == null", investigatorRead.getCurrent().getPatent());
            assertEquals("after refresh investigatorRead.getCurrent.getPatent().getId() is incorrect", investigatorRead.getCurrent().getPatent().getId(), currentPatent.getId());
            assertNull("after refresh investigatorRead.getNext() != null", investigatorRead.getNext());

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Fix for bug 264417: Table generation is incorrect for JoinTables in
     * AssociationOverrides. Problem is that non-owning mappings were processed
     * before the association overrides in turn causing the multiple table
     * generation (owning side - correct, non-owning side - incorrect)
     * This test was added as well to test the fix, also manual inspection of
     * the DDL was done.
     */
    public void testAssociationOverrideToEmbeddedManyToMany(){
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            Employee employee = new Employee();
            PhoneNumber phoneNumber = new PhoneNumber();
            employee.addPhoneNumber(phoneNumber);
            em.persist(employee);

            commitTransaction(em);

            // Force the read to hit the database and make sure the phone number is read back.
            clearCache();
            em.clear();
            assertNotNull("Unable to read back the phone number", em.find(PhoneNumber.class, phoneNumber.getNumber()));
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }

    // Bug 338812
    public void testDeleteObjectWithEmbeddedManyToMany(){
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            Employee employee = new Employee();
            employee.addPhoneNumber(new PhoneNumber());
            employee.addComment(new Comment());
            employee.addUpdate("Update record 1");
            em.persist(employee);
            commitTransaction(em);

            beginTransaction(em);
            Employee emp = em.find(Employee.class, employee.getId());
            em.remove(emp);
            commitTransaction(em);

        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }

    // Test for bug 277079
    public void testLAZYLOBWithEmbeddedId() {
        EntityManager em = createEntityManager();

        LobtestPK pk = new LobtestPK();

        try {
            beginTransaction(em);

            Lobtest lobtest = new Lobtest();
            byte b1 = Byte.parseByte("1");
            byte b2 = Byte.parseByte("2");
            lobtest.setContentdata(new byte[]{b1, b2});

            lobtest.setUuid("123456789");

            pk.setDocid("blah");
            pk.setVersionid(new BigInteger(Long.valueOf(System.currentTimeMillis()).toString()));
            lobtest.setLobtestPK(pk);

            em.persist(lobtest);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("Error persisting the lobtest : " + e);
        } finally {
            closeEntityManager(em);
        }

        clearCache();
        em = createEntityManager();

        Lobtest refreshedLobtest = em.find(Lobtest.class, pk);
        assertNotNull("The query returned nothing: ", refreshedLobtest);
        assertNotNull("Doc id from Lobtest was null", refreshedLobtest.getLobtestPK().getDocid());
        assertNotNull("Version id from Lobtest was null", refreshedLobtest.getLobtestPK().getVersionid());

        closeEntityManager(em);
    }

    public void testSimpleSelectFoo() {
        EntityManager em = createEntityManager();
        //simple test to make sure the table was created.  We do not care about the results,
        // only that the query can be run without an exception
        em.createQuery("Select f from Foo f").getResultList();
    }

    // Bug 373295
    public void testElementMapOnEmbedded() {
        // setup
        EntityManager em = createEntityManager();
        MapHolder holder1 = new MapHolder();
        holder1.setId(1);
        MapHolderEmbeddable embeddable1 = new MapHolderEmbeddable();
        embeddable1.getStringMap().put("key 1 1", "value 1 1");
        embeddable1.getStringMap().put("key 1 2", "value 1 2");
        holder1.setMapHolderEmbedded(embeddable1);
        holder1.getStringMap().put("key 1 1", "value 1 1");
        holder1.getStringMap().put("key 1 2", "value 1 2");

        MapHolder holder2 = new MapHolder();
        holder2.setId(2);
        MapHolderEmbeddable embeddable2 = new MapHolderEmbeddable();
        embeddable2.getStringMap().put("key 2 1", "value 2 1");
        embeddable2.getStringMap().put("key 2 2", "value 2 2");
        embeddable2.getStringMap().put("key 2 3", "value 2 3");
        holder2.setMapHolderEmbedded(embeddable2);
        holder2.getStringMap().put("key 2 1", "value 2 1");
        holder2.getStringMap().put("key 2 2", "value 2 2");
        holder2.getStringMap().put("key 2 3", "value 2 3");

        beginTransaction(em);
        em.persist(holder1);
        em.persist(holder2);
        commitTransaction(em);
        closeEntityManager(em);

        // test
        StringBuilder errorMsg = new StringBuilder();
        try {
            clearCache();
            em = createEntityManager();
            TypedQuery<MapHolder> query = em.createQuery("SELECT mh FROM MapHolder mh WHERE mh.id = 1 OR mh.id = 2", MapHolder.class);
            List<MapHolder> mapHolders = query.getResultList();

            // verify
            if (mapHolders.size() == 2) {
                for (MapHolder mh : mapHolders) {
                    if (mh.getMapHolderEmbedded().getStringMap().size() != mh.getId() + 1) {
                        errorMsg.append("Wrong getMapHolderEmbedded().getStringMap().size() ").append(mh.getMapHolderEmbedded().getStringMap().size()).append("; expected ").append(mh.getId() + 1).append("\n");
                    }
                    if (mh.getStringMap().size() != mh.getId() + 1) {
                        errorMsg.append("Wrong getStringMap().size() ").append(mh.getStringMap().size()).append("; expected ").append(mh.getId() + 1).append("\n");
                    }
                }
            } else {
                errorMsg.append("Wrong mapHolders size");
            }
        } finally {
            // clean-up
            beginTransaction(em);
            em.remove(em.find(MapHolder.class, 1));
            em.remove(em.find(MapHolder.class, 2));
            commitTransaction(em);
            closeEntityManager(em);
        }

        if (!errorMsg.isEmpty()) {
            fail("\n" + errorMsg);
        }
    }


    // Bug 386939 - @ManyToMany Map<Entity,Entity> unidirectional reverses Key and Value fields on Update
    public void testBug386939() {
        // Commenting out the test until bug 390026 is fixed.
        /*
        EntityManager em = createEntityManager(DDL_PU);

        try {
            // Step 1 - create some objects
            beginTransaction(em);

            ValueEntity value1 = new ValueEntity("V1", "value1");
            ValueEntity value2 = new ValueEntity("V2", "value2");
            ValueEntity value3 = new ValueEntity("V3", "value3");

            DetailEntity detail1 = new DetailEntity("D1", "detail1");
            DetailEntity detail2 = new DetailEntity("D2", "detail2");
            DetailEntity detail3 = new DetailEntity("D3", "detail3");

            Master master1 = new Master("M1", "master1");
            HashMap<DetailEntity, ValueEntity> map1 = new HashMap<DetailEntity, ValueEntity>();
            map1.put(detail1, value3);
            map1.put(detail3, value2);
            master1.setDetails(map1);

            Master master2 = new Master("M2", "master2");
            HashMap<DetailEntity, ValueEntity> map2 = new HashMap<DetailEntity, ValueEntity>();
            map2.put(detail2, value1);
            master2.setDetails(map2);

            Master master3 = new Master("M3", "master3");
            HashMap<DetailEntity, ValueEntity> map3 = new HashMap<DetailEntity, ValueEntity>();
            map3.put(detail2, value1);
            map3.put(detail1, value1);
            master3.setDetails(map3);

            em.persist(value1);
            em.persist(value2);
            em.persist(value3);

            em.persist(detail1);
            em.persist(detail2);
            em.persist(detail3);

            em.persist(master1);
            em.persist(master2);
            em.persist(master3);

            commitTransaction(em);

        } catch (RuntimeException e) {
            if (isTransactionActive(em))
                rollbackTransaction(em);
            throw e;
        } finally {
            closeEntityManager(em);
        }
        */
    }
}
