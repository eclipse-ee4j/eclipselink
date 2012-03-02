/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle, Frank Schwarz. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     08/20/2008-1.0.1 Nathan Beyer (Cerner) 
 *       - 241308: Primary key is incorrectly assigned to embeddable class 
 *                 field with the same name as the primary key field's name
 *     01/12/2009-1.1 Daniel Lo, Tom Ware, Guy Pelletier
 *       - 247041: Null element inserted in the ArrayList 
 *     07/17/2009 - tware -  added tests for DDL generation of maps
 *     01/22/2010-2.0.1 Guy Pelletier 
 *       - 294361: incorrect generated table for element collection attribute overrides
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     09/15/2010-2.2 Chris Delahunt
 *       - 322233 - AttributeOverrides and AssociationOverride dont change field type info
 *     11/17/2010-2.2.0 Chris Delahunt 
 *       - 214519: Allow appending strings to CREATE TABLE statements
 *     11/23/2010-2.2 Frank Schwarz 
 *       - 328774: TABLE_PER_CLASS-mapped key of a java.util.Map does not work for querying
 *     01/04/2011-2.3 Guy Pelletier 
 *       - 330628: @PrimaryKeyJoinColumn(...) is not working equivalently to @JoinColumn(..., insertable = false, updatable = false)
 *     01/06/2011-2.3 Guy Pelletier
 *       - 312244: can't map optional one-to-one relationship using @PrimaryKeyJoinColumn
 *     01/11/2011-2.3 Guy Pelletier  
 *       - 277079: EmbeddedId's fields are null when using LOB with fetchtype LAZY
 *     04/28/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 6)
 ******************************************************************************/   
package org.eclipse.persistence.testing.tests.jpa.ddlgeneration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Boss;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Capo;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Contract;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.MafiaFamily;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Mafioso;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Soldier;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Underboss;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.CodeExample;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.DesignPattern;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.GoldBenefit;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.GoldCustomer;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.LuxuryCar;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.PlatinumBenefit;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.PlatinumCustomer;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass.ProgrammingLanguage;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Port;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.impl.EquipmentDAO;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.impl.PortDAO;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.*;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Equipment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * JUnit test case(s) for DDL generation.
 */
public class DDLGenerationJUnitTestSuite extends JUnitTestCase {
    // the persistence unit name which is used in this test suite
    protected static final String DDL_PU = "ddlGeneration";
    private static final String DDL_TPC_PU = "ddlTablePerClass";

    private static final String DDL_TABLE_CREATION_SUFFIX_PU = "ddlTableSuffix";
    //DDL file name created from DDL_TABLE_CREATION_SUFFIX_PU initilization
    private static final String DDL_TABLE_CREATION_SUFFIX_DDL_FILENAME = "createDDL_ddlTableSuffix.jdbc";
    
    public static int family707;
    public static int family007;
    public static List<Integer> family707Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family707Contracts = new ArrayList<Integer>();
    public static List<Integer> family007Mafiosos = new ArrayList<Integer>();
    public static List<Integer> family007Contracts = new ArrayList<Integer>();

    public DDLGenerationJUnitTestSuite() {
        super();
    }

    public DDLGenerationJUnitTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DDLGenerationJUnitTestSuite.class);
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        // Trigger DDL generation
        EntityManager em = createEntityManager(DDL_PU);
        closeEntityManager(em);
        clearCache(DDL_PU);

        EntityManager emDDLTPC = createEntityManager(DDL_TPC_PU);
        closeEntityManager(emDDLTPC);
        clearCache(DDL_TPC_PU);
    }

    /**
     * 214519 - Allow appending strings to CREATE TABLE statements 
     * This test uses PU ddlTableSuffix to create file  DDL_TABLE_CREATION_SUFFIX_DDL_FILENAME, which it then
     * reads back in to verify the CREATE TABLE statements have the correct strings appended to them
     */
    public void testDDLTableCreationWithSuffix(){
        if(this.isOnServer){
            return;
        }
      //strings searched for:
        String property_suffix = " propertyCreationSuffix";
        String xml_suffix = "creationSuffixString";
        final String CREATE_TABLE = "CREATE TABLE ";

      //results:
        //used for checking eclipselink-orm.xml settings:
        ArrayList<String> statements = new ArrayList();//used to output the create statement should it not have the suffix set in the orm.xml
        ArrayList<Boolean> results = new ArrayList();

        //used for checking settings from properties:
        boolean has_property_suffix = true; //set to false when we find one create table statement missing the value
        String property_statement = "";//used to output the create statement should it not have the suffix set through properties

        EntityManager em = createEntityManager(DDL_TABLE_CREATION_SUFFIX_PU);

        List<String> strings = getDDLFile(DDL_TABLE_CREATION_SUFFIX_DDL_FILENAME);
        for (String statement: strings){
            if (statement.startsWith(CREATE_TABLE)) {
                if( !statement.endsWith(property_suffix)){
                    has_property_suffix = false;
                    property_statement = statement;
                }
                if (statement.startsWith(CREATE_TABLE+"DDL_SALARY")){
                    statements.add(statement);
                    //xml_suffix will be right before property_suffix in the statement.  Should be enough to check that its there
                    results.add(statement.contains(xml_suffix+"1"));
                } else if (statement.startsWith(CREATE_TABLE+"DDL_RESPONS")) {
                    statements.add(statement);
                    results.add(statement.contains(xml_suffix+"2"));
                } else if (statement.startsWith(CREATE_TABLE+"DDL_COMMENT")) {
                    statements.add(statement);
                    results.add(statement.contains(xml_suffix+"3"));
                } else if (statement.startsWith(CREATE_TABLE+"DDL_MANY_MANY")) {
                    statements.add(statement);
                    results.add(statement.contains(xml_suffix+"4"));
                }
            }
        }

        assertTrue("missing creation suffix from properties on statement: "+property_statement, has_property_suffix);
        int size = statements.size();
        assertTrue("Missing some create Table statements, only got "+size+" of the 4 expected", size==4);
        for(int i=0;i<size; i++) {
            assertTrue("missing creation suffix "+i+" from eclipselink-orm.xml on statement: "+statements.get(i), results.get(i));
        }
    }
    
    public void testDDLTablePerClassModel() {
        EntityManager em = createEntityManager(DDL_TPC_PU);
        beginTransaction(em);

        GoldCustomer goldCustomer = new GoldCustomer();;
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

            List<GoldBenefit> goldBenefits1 = new ArrayList<GoldBenefit>();
            List<GoldBenefit> goldBenefits2 = new ArrayList<GoldBenefit>();
            List<PlatinumBenefit> platinumBenefits1 = new ArrayList<PlatinumBenefit>();

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

        clearCache(DDL_TPC_PU);
        em = createEntityManager(DDL_TPC_PU);  

        assertTrue("ReadAll did not find subclasses.", !em.createQuery("Select c from Customer c").getResultList().isEmpty());

        GoldCustomer refreshedGoldCustomer = em.find(GoldCustomer.class, goldCustomer.getCustomerId());
        assertTrue("The gold customer read back did not match the original", getServerSession(DDL_TPC_PU).compareObjects(goldCustomer, refreshedGoldCustomer));

        PlatinumCustomer refreshedPlatinumCustomer = em.find(PlatinumCustomer.class, platinumCustomer.getCustomerId());
        assertTrue("The platinum customer read back did not match the original", getServerSession(DDL_TPC_PU).compareObjects(platinumCustomer, refreshedPlatinumCustomer));
    }

    public void testDDLTablePerClassModelQuery() {
        EntityManager em = createEntityManager(DDL_TPC_PU);
        
        List goldCustomers = em.createNamedQuery("GoldCustomer.findAll").getResultList();
        List platinumCustomers = em.createNamedQuery("PlatinumCustomer.findAll").getResultList();

        assertFalse("No gold customers returned", goldCustomers.isEmpty());
        assertFalse("No platinum customers returned", platinumCustomers.isEmpty());
    }

    // Test for GF#1392
    // If there is a same name column for the entity and many-to-many table, wrong pk constraint generated.
    public void testDDLPkConstraintErrorIncludingRelationTableColumnName() {
        EntityManager em = createEntityManager(DDL_PU);
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
        EntityManager em = createEntityManager(DDL_PU);
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
        EntityManager em = createEntityManager(DDL_PU);
        beginTransaction(em);
        
        try {
            Country country = new Country();
            country.setName("USA");
            country.setIsoCode("840");
            
            State state = new State();
            state.setCountry(country);
            state.setIsoCode("36");
            state.setName("New York");
            
            Set<State> states = new HashSet<State>();
            states.add(state);
            country.setStates(states);
            
            City city = new City();
            city.setName("Rochester");
            city.setState(state);
            
            Set<City> cities = new HashSet<City>();
            cities.add(city);
            state.setCities(cities);
            
            ZipArea zipArea = new ZipArea();
            zipArea.setCity(city);
            
            Set<ZipArea> zipAreas = new HashSet<ZipArea>();
            zipAreas.add(zipArea);
            city.setZipAreas(zipAreas);
            
            Zip zip = new Zip();
            zip.setCode("14621");
            zip.setCountry(country);
            zip.setZipAreas(zipAreas);
            zipArea.setZip(zip);

            Set<Zip> zips = new HashSet<Zip>();
            country.setZips(zips);
            
            em.persist(country);
            em.persist(state);
            em.persist(city);
            em.persist(zipArea);
            em.persist(zip);
            
            commitTransaction(em);
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("Error persisting new country with city, states and zips : " + e);
        } finally {
            closeEntityManager(em);
        }
    }

    // Test for bug 294361
    public void testDDLEmbeddableMapKey() {
        EntityManager em = createEntityManager(DDL_PU);
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
        EntityManager em = createEntityManager(DDL_PU);
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
        this.assertNotNull("Expected an exception persisting null into a field with nullable=false set on an override", expectedException);
    }

    // Test for bug 322233, that optional field info defined in Overrides are used in DDL
    public void testDDLAttributeOverridesOnElementCollection() {
        EntityManager em = createEntityManager(DDL_PU);
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
        this.assertNotNull("Expected an exception persisting null into a field with nullable=false set on an override", expectedException);
    }

    // Test for relationships using candidate(unique) keys
    public void testDDLUniqueKeysAsJoinColumns() {
        CKeyEntityAPK aKey;
        CKeyEntityBPK bKey;
        
        EntityManager em = createEntityManager(DDL_PU);
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
        //clearCache(DDL_PU);

        em = createEntityManager(DDL_PU);
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
        //clearCache(DDL_PU);

        em = createEntityManager(DDL_PU);
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
        if(!getServerSession(DDL_PU).getPlatform().supportsUniqueKeyConstraints()
                || getServerSession(DDL_PU).getPlatform().isSybase()) {
            return;
        }
        UniqueConstraintsEntity1 ucEntity;

        EntityManager em = createEntityManager(DDL_PU);
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

        em = createEntityManager(DDL_PU);
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

        em = createEntityManager(DDL_PU);
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

        em = createEntityManager(DDL_PU);
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
        if(!getServerSession(DDL_PU).getPlatform().supportsUniqueKeyConstraints()
                || getServerSession(DDL_PU).getPlatform().isSybase()) {
            return;
        }
        UniqueConstraintsEntity2 ucEntity;

        EntityManager em = createEntityManager(DDL_PU);
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

        em = createEntityManager(DDL_PU);
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

        em = createEntityManager(DDL_PU);
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

        em = createEntityManager(DDL_PU);
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
        EntityManager em = createEntityManager(DDL_PU);
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
        EntityManager em = createEntityManager(DDL_PU);
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
        EntityManager em = createEntityManager(DDL_PU);
        beginTransaction(em);
        try {
            // add comments
            long seq = System.currentTimeMillis(); // just to get unique value :-)
            CKeyEntityB b = new CKeyEntityB(new CKeyEntityBPK(seq, "B1210"));
            List<Comment<String>> comments = new ArrayList(2);
            comments.add(new Comment("comment 1"));
            comments.add(new Comment("comment 2"));
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

    public void testCascadeMergeOnManagedEntityWithOrderedList() {
        EntityManagerFactory factory = getEntityManagerFactory(DDL_PU);

        // Clean up first
        cleanupEquipmentAndPorts(factory);

        // Create a piece equipment with one port.
        createEquipment(factory);

        // Add two ports to the equipment
        addPorts(factory);

        // Fetch the equipment and validate there is no null elements in
        // the ArrayList of Port.
        verifyPorts(factory);
    }

    public void testDirectCollectionMapping(){
        EntityManager em = createEntityManager(DDL_PU);
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

            clearCache(DDL_PU);
            em.refresh(holder);
            assertTrue(holder.getDCMap().get(key) != null);

            holder.getDCMap().remove(key);
            em.remove(holder);
            em.remove(key);

            em.flush();

            rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testAggregateCollectionMapping(){
        EntityManager em = createEntityManager(DDL_PU);
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

            clearCache(DDL_PU);
            em.refresh(holder);
            assertTrue(holder.getACMap().get(key) != null);

            holder.getACMap().remove(key);
            em.remove(holder);
            em.remove(key);

            em.flush();

            rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testOneToManyMapping(){
        EntityManager em = createEntityManager(DDL_PU);
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

            clearCache(DDL_PU);
            em.refresh(holder);
            holder.getOTMMap().get(key);
            assertTrue(holder.getOTMMap().get(key) != null);

            holder.getOTMMap().remove(key);
            value.setHolder(null);
            em.remove(holder);
            em.remove(value);

            em.flush();

            rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testUnidirectionalOneToManyMapping(){
        EntityManager em = createEntityManager(DDL_PU);
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

            clearCache(DDL_PU);
            em.refresh(holder);
            assertTrue(holder.getUOTMMap().get(4) != null);

            holder.getUOTMMap().remove(4);
            em.remove(holder);
            em.remove(value);

            em.flush();

            rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testManyToManyMapping(){
        EntityManager em = createEntityManager(DDL_PU);
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

            clearCache(DDL_PU);
            em.refresh(holder);
            assertTrue(holder.getMTMMap().get(key) != null);

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

            rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    // Bug 279784 - Incomplete OUTER JOIN based on JoinTable 
    public void testManyToManyWithMultipleJoinColumns() {
        String prefix = "testManyToManyWithMultipleJoinColumns";
        EntityManager em = createEntityManager(DDL_PU);
        beginTransaction(em);

        // persist and flush c1, cache its pk
        CKeyEntityC c1 = new CKeyEntityC(new CKeyEntityCPK(prefix + "_1"));
        c1.setTempRole(prefix + "_1_Role");
        em.persist(c1);
        // that insures that sequencing has worked 
        em.flush();
        CKeyEntityCPK c1KeyPK = c1.getKey(); 

        // create b1 with the same (seq, code) as c1's (seq, role); add to c1
        ArrayList bs1 = new ArrayList();
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
        ArrayList bs2 = new ArrayList();
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

        clearCache(DDL_PU);
        em = createEntityManager(DDL_PU);
        List<CKeyEntityC> clist = em.createQuery("SELECT c from CKeyEntityC c LEFT OUTER JOIN c.bs bs WHERE c.key = :key").setParameter("key", c1KeyPK).getResultList();
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

        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testEmbeddedManyToMany(){
        EntityManager em = createEntityManager(DDL_PU);
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
            clearCache(DDL_PU);

            inventor = em.find(Inventor.class, 1);
            assertTrue("Embeddable is null", inventor.getPatentCollection() != null);
            assertFalse("Collection is empty", inventor.getPatentCollection().getPatents().isEmpty());
            assertTrue("Target is incorrect", inventor.getPatentCollection().getPatents().get(0).getId() == 1);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testEmbeddedOneToOne(){
        EntityManager em = createEntityManager(DDL_PU);
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
            assertTrue("investigatorRead.getLast() == null", investigatorRead.getLast() != null);
            assertTrue("investigatorRead.getLast.getPatent() == null", investigatorRead.getLast().getPatent() != null);
            assertTrue("investigatorRead.getLast.getPatent().getId() is incorrect", investigatorRead.getLast().getPatent().getId() == lastPatent.getId());
            assertTrue("investigatorRead.getCurrent() != null", investigatorRead.getCurrent() == null);
            assertTrue("investigatorRead.getNext() != null", investigatorRead.getNext() == null);
            
            Patent currentPatent = new Patent();
            currentPatent.setId(2);
            em.persist(currentPatent);
            em.flush();
            em.createNativeQuery("UPDATE " + em.unwrap(ServerSession.class).getDescriptor(PatentInvestigator.class).getDefaultTable().getName() + " SET CURRENT_DESRIPTION = 'Current', CURRENT_PATENT = " + currentPatent.getId() + " WHERE ID = " + investigator.getId()).executeUpdate();            
            em.refresh(investigatorRead);
            assertTrue("after refresh investigatorRead.getLast() == null", investigatorRead.getLast() != null);
            assertTrue("after refresh investigatorRead.getLast.getPatent() == null", investigatorRead.getLast().getPatent() != null);
            assertTrue("after refresh investigatorRead.getLast.getPatent().getId() is incorrect", investigatorRead.getLast().getPatent().getId() == lastPatent.getId());
            assertTrue("after refresh investigatorRead.getCurrent() == null", investigatorRead.getCurrent() != null);
            assertTrue("after refresh investigatorRead.getCurrent.getPatent() == null", investigatorRead.getCurrent().getPatent() != null);
            assertTrue("after refresh investigatorRead.getCurrent.getPatent().getId() is incorrect", investigatorRead.getCurrent().getPatent().getId() == currentPatent.getId());
            assertTrue("after refresh investigatorRead.getNext() != null", investigatorRead.getNext() == null);

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
        EntityManager em = createEntityManager(DDL_PU);

        try {
            beginTransaction(em);

            Employee employee = new Employee();
            PhoneNumber phoneNumber = new PhoneNumber();
            employee.addPhoneNumber(phoneNumber);
            em.persist(employee);
            
            getServerSession(DDL_PU).setLogLevel(0);
            commitTransaction(em);

            // Force the read to hit the database and make sure the phone number is read back.
            clearCache(DDL_PU);
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
        EntityManager em = createEntityManager(DDL_PU);

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

    protected void cleanupEquipmentAndPorts(EntityManagerFactory factory) {
        EntityManager em = null;

        try {
            em = factory.createEntityManager();
            beginTransaction(em);

            em.createQuery("DELETE FROM PortDAO").executeUpdate();     
            em.createQuery("DELETE FROM EquipmentDAO").executeUpdate();

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    protected void createEquipment(EntityManagerFactory factory) {
        EntityManager em = null;

        try {
            em = factory.createEntityManager();

            beginTransaction(em);

            Equipment eq = new EquipmentDAO();
            eq.setId("eq");

            Port port = new PortDAO();
            port.setId("p1");
            port.setPortOrder(0);

            eq.addPort(port);

            em.persist(eq);
            commitTransaction(em);
        } catch (Exception e) {
            if (em != null && isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("En error occurred creating new equipment: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    protected void addPorts(EntityManagerFactory factory) {
        EntityManager em = null;

        try {
            em = factory.createEntityManager();
            beginTransaction(em);
            Query query = em.createNamedQuery("Equipment.findEquipmentById");
            query.setParameter("id", "eq");
            Equipment eq = (Equipment) query.getResultList().get(0);
            commitTransaction(em);

            em = factory.createEntityManager();
            beginTransaction(em); 
            eq = em.merge(eq);
            
            Port port = new PortDAO();
            port.setId("p2");
            port.setPortOrder(1);
            eq.addPort(port);

            port = new PortDAO();
            port.setId("p3");
            port.setPortOrder(2);
            eq.addPort(port);
            
            eq = em.merge(eq);
            commitTransaction(em);
        } catch (Exception e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("En error occurred adding new ports: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    protected void verifyPorts(EntityManagerFactory factory) {
        EntityManager em = null;

        try {
            em = factory.createEntityManager();
            beginTransaction(em);
            Query query = em.createNamedQuery("Equipment.findEquipmentById");
            query.setParameter("id", "eq");
            Equipment eq = (Equipment) query.getResultList().get(0);
            commitTransaction(em);

            for (Port port: eq.getPorts()) {
                if (port == null) {
                    fail("A null PORT was found in the collection of ports.");
                }
            } 
        } catch (Exception e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            fail("En error occurred fetching the results to verify: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Returns a List of strings representing the lines within the fileName. 
     * @param fileName
     * @return List<String>
     */
    public List<String> getDDLFile(String fileName){
        ArrayList<String> array = new ArrayList();
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        if (fileStream == null) {
            File file = new File(fileName);
            try {
                fileStream = new FileInputStream(file);
            } catch (FileNotFoundException exception) {
                this.warning("cannot load file "+fileName+ " due to error: "+exception);
                throw ValidationException.fatalErrorOccurred(exception);
            }
        }
        InputStreamReader reader = null;
        BufferedReader buffReader = null;
        try {
            try {
                // Bug2631348  Only UTF-8 is supported
                reader = new InputStreamReader(fileStream, "UTF-8");
            } catch (UnsupportedEncodingException exception) {
                throw ValidationException.fatalErrorOccurred(exception);
            }
            buffReader = new BufferedReader(reader);
            boolean go = true;
            while (go){
                String line = buffReader.readLine();
                if (line==null) {
                    go=false;
                } else {
                    array.add(line);
                }
            }

        }catch (Exception e) {
            //ignore exception
        } finally {
            try {
                if (buffReader != null) {
                    buffReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }
        return array;
    }
    
    // Test for bug 328774
    public void testTPCMappedKeyMapQuery() {
        if (! isJPA10()) {
            EntityManager em = createEntityManager(DDL_TPC_PU);
            
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
            
            clearCache(DDL_TPC_PU);
            em = createEntityManager(DDL_TPC_PU);  
            
            TypedQuery<DesignPattern> query = em.createQuery("SELECT x FROM DesignPattern x", DesignPattern.class);
            List<DesignPattern> resultList = query.getResultList();
            assertEquals("Unexpected number of design patterns returned", 1, resultList.size());
            closeEntityManager(em);
        }
    }
    
    // Test for bug 277079
    public void testLAZYLOBWithEmbeddedId() {
        EntityManager em = createEntityManager(DDL_PU);
            
        LobtestPK pk = new LobtestPK();
        
        try {
            beginTransaction(em);
                
            Lobtest lobtest = new Lobtest();
            Byte b1 = new Byte("1");
            Byte b2 = new Byte("2");
            lobtest.setContentdata(new byte[]{b1, b2});

            lobtest.setUuid("123456789");
                
            pk.setDocid("blah");
            pk.setVersionid(new BigInteger(new Long(System.currentTimeMillis()).toString()));
            lobtest.setLobtestPK(pk);
                
            getServerSession(DDL_PU).setLogLevel(0);
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
            
        clearCache(DDL_PU);
        em = createEntityManager(DDL_PU);
        
        Lobtest refreshedLobtest = em.find(Lobtest.class, pk);
        assertNotNull("The query returned nothing: ", refreshedLobtest);
        assertNotNull("Doc id from Lobtest was null", refreshedLobtest.getLobtestPK().getDocid());
        assertNotNull("Version id from Lobtest was null", refreshedLobtest.getLobtestPK().getVersionid());
        
        closeEntityManager(em);
    }
    
    //// Multitenant tests 
    
    public void testCreateMafiaFamily707() {
        EntityManager em = createEntityManager(DDL_PU);
        
        try {
            beginTransaction(em);
        
            // Set the tenant properties (within Glassfish, we can't do this till the EM becomes transactional)
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
            family707Mafiosos = new ArrayList<Integer>();
            family707Mafiosos.add(boss.getId());
            family707Mafiosos.add(underboss.getId());
            family707Mafiosos.add(capo1.getId());
            family707Mafiosos.add(capo2.getId());
            family707Mafiosos.add(soldier1.getId());
            family707Mafiosos.add(soldier2.getId());
            family707Mafiosos.add(soldier3.getId());
            family707Mafiosos.add(soldier4.getId());
            family707Mafiosos.add(soldier5.getId());

            family707Contracts = new ArrayList<Integer>();
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
        EntityManager em = createEntityManager(DDL_PU);

        try {
            beginTransaction(em);
            
            // Set the tenant properties (within Glassfish, we can't do this till the EM becomes transactional)
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
            family007Mafiosos = new ArrayList<Integer>();
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

            family007Contracts = new ArrayList<Integer>();
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
    
    public void testValidateMafiaFamily707() {
        EntityManager em = createEntityManager(DDL_PU);

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
        EntityManager em = createEntityManager(DDL_PU);

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
    
    protected void validateMafiaFamily707(EntityManager em) {
        clearCache(DDL_PU);
        em.clear();
        
        beginTransaction(em);
        em.setProperty("tenant.id", "707");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");
        
        MafiaFamily family = em.find(MafiaFamily.class, family707);
        assertNotNull("The Mafia Family with id: " + family707 + ", was not found", family);
        assertTrue("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [3]", family.getTags().size() == 3);
        assertNull("The Mafia Family with id: " + family007 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family007));
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
        
        // Update all our contract descriptions to be 'voided'
        em.createNamedQuery("UpdateAllContractDescriptions").executeUpdate();
        commitTransaction(em);
        
        // Read and validate the contracts
        beginTransaction(em);
        em.setProperty("tenant.id", "707");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "707");
        
        List<Contract> contracts = em.createNamedQuery("FindAllContracts").getResultList();
        assertTrue("Incorrect number of contracts were returned [" + contracts.size() + "], expected[3]", contracts.size() == 3);
        
        for (Contract contract : contracts) {
            assertTrue("Contract description was not voided.", contract.getDescription().equals("voided"));
        }
        
        // See how many soldiers are returned from a jpql query
        List soldiers = em.createQuery("SELECT s from Soldier s").getResultList();
        assertTrue("Incorrect number of soldiers were returned [" + soldiers.size() + "], expected [5]",  soldiers.size() == 5);
        
        if(getServerSession(DDL_PU).getPlatform().isSymfoware()) {
            getServerSession(DDL_PU).logMessage("Test DDLGenerationJUnitTestSuite partially skipped for this platform, "
                    +"which uses UpdateAll internally to check tenant-id when updating an entity using JOINED inheritance strategy. "
                    +"Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
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
    
    protected void validateMafiaFamily007(EntityManager em) {
        clearCache(DDL_PU);
        em.clear();
        
        beginTransaction(em);
        em.setProperty("tenant.id", "007");
        em.setProperty(EntityManagerProperties.MULTITENANT_PROPERTY_DEFAULT, "007");
        
        MafiaFamily family =  em.find(MafiaFamily.class, family007);
        assertNotNull("The Mafia Family with id: " + family007 + ", was not found", family);
        assertTrue("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [5]", family.getTags().size() == 5);
        assertNull("The Mafia Family with id: " + family707 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family707));
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
        List families = em.createNamedQuery("findJPQLMafiaFamilies").getResultList();
        assertTrue("Incorrect number of families were returned [" + families.size() + "], expected [1]",  families.size() == 1);
   
        // Find our boss and make sure his name has not been compromised from the 707 family.
        Boss boss = em.find(Boss.class, family007Mafiosos.get(0));
        assertFalse("The Boss name has been compromised", boss.getFirstName().equals("Compromised"));
        commitTransaction(em);
    }

    // Bug 357670
    public void testLazyBlob(){
        EntityManager em = createEntityManager(DDL_TPC_PU);
        beginTransaction(em);
        LuxuryCar car = new LuxuryCar();
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintWriter pw = new java.io.PrintWriter(baos);
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
        assertTrue("Blob was null after flush, refresh, commit.", pic != null);
    }

    public void testSimpleSelectFoo() {
        EntityManager em = createEntityManager(DDL_PU);
        //simple test to make sure the table was created.  We do not care about the results, 
        // only that the query can be run without an exception
        em.createQuery("Select f from Foo f").getResultList();
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
