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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 *     02/18/2010-2.0.2 Guy Pelletier 
 *       - 294803: @Column(updatable=false) has no effect on @Basic mappings
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.inherited;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.DatabaseSession;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Consumer;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Official;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Accredidation;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Birthday;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Alpine;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Becks;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.BecksTag;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Committee;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Corona;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.CoronaTag;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Heineken;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Location;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.NoviceBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.ExpertBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Record;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Canadian;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Certification;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.InheritedTableManager;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.ServiceTime;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.TelephoneNumber;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Witness;
 
/**
 * JUnit test case(s) for testing the XML configurations of JPA.
 */
public class EntityMappingsInheritedJUnitTestCase extends JUnitTestCase {
    private static Integer beerConsumerId;
    private static Integer m_noviceBeerConsumerId;
    private static Integer m_expertBeerConsumerId;
    
    private static Timestamp m_quote1Stamp;
    private static final String QUOTE_ONE = "Beer is blood";
    private static Timestamp m_quote2Stamp;
    private static final String QUOTE_TWO = "My first wife was a beer";
   
    public EntityMappingsInheritedJUnitTestCase() {
        super();
    }
    
    public EntityMappingsInheritedJUnitTestCase(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("EntityMappingsInheritedJUnitTestCase");
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testSetup"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testOneToManyRelationships"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testVerifyOneToManyRelationships"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testCreateBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testReadBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testCreateNoviceBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testReadNoviceBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testOrderColumnNoviceBeerConsumerDesignations"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testCreateExpertBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testReadExpertBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testNamedNativeQueryBeerConsumers"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testNamedNativeQueryCertifications"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testUpdateBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testDeleteBeerConsumer"));
        // 1-M map using direct key
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testHeinekenBeerConsumer"));
        // 1-M map using entity key
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testBecksBeerConsumer"));
        // 1-M map using embeddable key
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testCoronaBeerConsumer"));

        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testColumnUpdatableAndInsertable"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testColumnUpdatableAndInsertableThroughQuery"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = JUnitTestCase.getServerSession();
        new InheritedTableManager().replaceTables(session);
        clearCache();
    }
    
    public void testBecksBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        BeerConsumer initialBC = new BeerConsumer();
        int beerConsumerId = 0;
        
        try {
            Becks becks1  = new Becks();
            becks1.setAlcoholContent(5.1);
            BecksTag becksTag1 = new BecksTag();
            becksTag1.setCallNumber("0A.789");
            em.persist(becksTag1);
            
            Becks becks2  = new Becks();
            becks2.setAlcoholContent(5.1);
            BecksTag becksTag2 = new BecksTag();
            becksTag2.setCallNumber("BX.521");
            em.persist(becksTag2);
            
            Becks becks3  = new Becks();
            becks3.setAlcoholContent(5.1);
            BecksTag becksTag3 = new BecksTag();
            becksTag3.setCallNumber("UY.429");
            em.persist(becksTag3);
            
            initialBC.setName("Becks Consumer");
            initialBC.addBecksBeerToConsume(becks1, becksTag1);
            initialBC.addBecksBeerToConsume(becks2, becksTag2);
            initialBC.addBecksBeerToConsume(becks3, becksTag3);
            
            em.persist(initialBC);
            beerConsumerId = initialBC.getId();
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
        
        clearCache();
        em = createEntityManager();
        BeerConsumer refreshedBC = em.find(BeerConsumer.class, beerConsumerId);       
        assertTrue("The beer consumer read back did not match the original", getServerSession().compareObjects(initialBC, refreshedBC));
    }
    
    public void testCoronaBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        BeerConsumer initialBC = new BeerConsumer();
        int beerConsumerId = 0;
        
        try {
            Corona corona1  = new Corona();
            corona1.setAlcoholContent(5.3);
            CoronaTag coronaTag1 = new CoronaTag();
            coronaTag1.setCode("0A");
            coronaTag1.setNumber(789);
            
            Corona corona2  = new Corona();
            corona2.setAlcoholContent(5.3);
            CoronaTag coronaTag2 = new CoronaTag();
            coronaTag2.setCode("BX");
            coronaTag2.setNumber(521);
            
            Corona corona3  = new Corona();
            corona3.setAlcoholContent(5.3);
            CoronaTag coronaTag3 = new CoronaTag();
            coronaTag3.setCode("UY");
            coronaTag3.setNumber(429);
            
            initialBC.setName("Corona Consumer");
            initialBC.addCoronaBeerToConsume(corona1, coronaTag1);
            initialBC.addCoronaBeerToConsume(corona2, coronaTag2);
            initialBC.addCoronaBeerToConsume(corona3, coronaTag3);
            
            em.persist(initialBC);
            beerConsumerId = initialBC.getId();
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
        
        clearCache();
        em = createEntityManager();
        BeerConsumer refreshedBC = em.find(BeerConsumer.class, beerConsumerId);
        assertTrue("The beer consumer read back did not match the original", getServerSession().compareObjects(initialBC, refreshedBC));
    }
    
    public void verifyCalled(int countBefore, int countAfter, String callback) {
        assertFalse("The callback method [" + callback + "] was not called", countBefore == countAfter);
        assertTrue("The callback method [" + callback + "] was called more than once", countAfter == (countBefore + 1));
    }
    
    public void verifyNotCalled(int countBefore, int countAfter, String callback) {
        assertTrue("The callback method [" + callback + "] was called.", countBefore == countAfter);
    }
    
    public void testCreateBeerConsumer() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            BeerConsumer consumer = new BeerConsumer();
            consumer.setName("Joe Black");

            int beerPrePersistCountBefore = consumer.pre_persist_count;
            em.persist(consumer);
            int beerPrePersistCountAfter = consumer.pre_persist_count;
            verifyCalled(beerPrePersistCountBefore, beerPrePersistCountAfter, "pre-persist");
            
            beerConsumerId = consumer.getId();
            
            Alpine alpine1 = new Alpine();
            alpine1.setAlcoholContent(5.0);
            alpine1.setBeerConsumer(consumer);
            alpine1.setBestBeforeDate(new Date(System.currentTimeMillis()+10000000));
            em.persist(alpine1);

            Canadian canadian1 = new Canadian();
            canadian1.setAlcoholContent(5.5);
            canadian1.setBeerConsumer(consumer);
            canadian1.setBornOnDate(new Date(System.currentTimeMillis()-30000000));
            em.persist(canadian1);
            
            Canadian canadian2 = new Canadian();
            canadian2.setAlcoholContent(5.0);
            canadian2.setBeerConsumer(consumer);
            canadian2.setBornOnDate(new Date(System.currentTimeMillis()-23000000));
            em.persist(canadian2);

            /*
            TelephoneNumber homeNumber = new TelephoneNumber();
            homeNumber.setAreaCode("555");
            homeNumber.setType("Home");
            homeNumber.setNumber("123-1234");
            getEntityManager().persist(homeNumber);
            
            TelephoneNumber workNumber = new TelephoneNumber();
            workNumber.setAreaCode("555");
            workNumber.setType("Work");
            workNumber.setNumber("987-9876");
            getEntityManager().persist(workNumber);
            */
            
            Certification cert1 = new Certification();
            cert1.setDescription("Value brand beer consumption certified");
            cert1.setBeerConsumer(consumer);
            em.persist(cert1);

            Certification cert2 = new Certification();
            cert2.setDescription("Premium brand beer consumption certified");
            cert2.setBeerConsumer(consumer);
            em.persist(cert2);

            em.persist(consumer);
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
        
    }

    public void testCreateExpertBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        ExpertBeerConsumer beerConsumer = new ExpertBeerConsumer();
        
        try {    
            beerConsumer.setName("Expert Beer Consumer");
            beerConsumer.setIQ(110);
            
            beerConsumer.getAcclaims().add("A");
            beerConsumer.getAcclaims().add("B");
            beerConsumer.getAcclaims().add("C");
            
            // Commenting out this mapping until bug 272298 is resolved.
            //beerConsumer.getAudio().add(new byte[]{1});
            //beerConsumer.getAudio().add(new byte[]{2});
            //beerConsumer.getAudio().add(new byte[]{3});
            
            beerConsumer.getAwards().put("A", "A");
            beerConsumer.getAwards().put("B", "B");
            beerConsumer.getAwards().put("C", "C");
            
            beerConsumer.getDesignations().add("A");
            beerConsumer.getDesignations().add("B");
            
            m_quote1Stamp = Helper.timestampFromDate(Helper.dateFromYearMonthDate(2009, 1, 1));
            beerConsumer.getQuotes().put(m_quote1Stamp, QUOTE_ONE);
            m_quote2Stamp = Helper.timestampFromDate(Helper.dateFromYearMonthDate(2005, 7, 9));
            beerConsumer.getQuotes().put(m_quote2Stamp, QUOTE_TWO);
            
            Record record1 = new Record();
            record1.setDescription("Fastest beer ever consumed - 10 ms");
            record1.setDate(Helper.dateFromYearMonthDate(2009, 10, 10));
            record1.setLocation(new Location("Ottawa", "Canada"));
            beerConsumer.getRecords().add(record1);
            
            Record record2 = new Record();
            record2.setDescription("Most beers consumed in a second - 5");
            record2.setDate(Helper.dateFromYearMonthDate(2005, 12, 12));
            record2.setLocation(new Location("Miami", "USA"));
            beerConsumer.getRecords().add(record2);
            
            Accredidation accredidation = new Accredidation();
            accredidation.setDetails("Elite, absolutely elite!");
            Witness witness1 = new Witness();
            witness1.setName("Big Bobby");
            accredidation.addWitness(witness1);
            Witness witness2 = new Witness();
            witness2.setName("Little Bobby");
            accredidation.addWitness(witness2);
            Official official = new Official();
            official.setName("Authority Joe");
            accredidation.addOfficial(official);
            beerConsumer.setAccredidation(accredidation);
            
            Birthday birthday1 = new Birthday();
            birthday1.setDay(9);
            birthday1.setMonth(7);
            birthday1.setYear(2005);
            beerConsumer.addCelebration(birthday1, "Drank a 24 of Heineken");
            
            Birthday birthday2 = new Birthday();
            birthday2.setDay(10);
            birthday2.setMonth(7);
            birthday2.setYear(2006);
            beerConsumer.addCelebration(birthday2, "Drank a 24 of Becks");
            
            Committee committee1 = new Committee();
            committee1.setDescription("New beer committee");
            beerConsumer.addCommittee(committee1);
            
            Committee committee2 = new Committee();
            committee2.setDescription("Alcohol content regulation");
            beerConsumer.addCommittee(committee2);
            
            em.persist(beerConsumer);
            m_expertBeerConsumerId = beerConsumer.getId();
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation for an expert beer consumer: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
        
        clearCache();
        em = createEntityManager();
        BeerConsumer refreshedBC = em.find(BeerConsumer.class, m_expertBeerConsumerId);       
        assertTrue("The expert beer consumer read back did not match the original", getServerSession().compareObjects(beerConsumer, refreshedBC));
    }
    
    public void testCreateNoviceBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        NoviceBeerConsumer beerConsumer = new NoviceBeerConsumer();
        
        try {    
            beerConsumer.setName("Novice Beer Consumer");
            beerConsumer.setIQ(100);
            
            beerConsumer.getAcclaims().add(1);
            beerConsumer.getAcclaims().add(2);
            beerConsumer.getAcclaims().add(3);
            
            beerConsumer.getAwards().put(1, 1);
            beerConsumer.getAwards().put(2, 2);
            beerConsumer.getAwards().put(3, 3);
            
            beerConsumer.getDesignations().add("5");
            beerConsumer.getDesignations().add("4");
            beerConsumer.getDesignations().add("2");
            beerConsumer.getDesignations().add("3");
            beerConsumer.getDesignations().add("1");

            Record record1 = new Record();
            record1.setDescription("Slowest beer ever consumed - 10 hours");
            record1.setDate(Helper.dateFromYearMonthDate(2008, 1, 1));
            record1.setLocation(new Location("Paris", "France"));
            beerConsumer.getRecords().add(record1);
            
            Accredidation accredidation = new Accredidation();
            accredidation.setDetails("Superb, just superb!");
            Witness witness1 = new Witness();
            witness1.setName("Mickey Blue Eyes");
            accredidation.addWitness(witness1);
            Witness witness2 = new Witness();
            witness2.setName("Donny Trafalgo");
            accredidation.addWitness(witness2);
            beerConsumer.setAccredidation(accredidation);
            
            Committee committee1 = new Committee();
            committee1.setDescription("Moral labelling");
            beerConsumer.addCommittee(committee1);
            
            Committee committee2 = new Committee();
            committee2.setDescription("Crimes against beer");
            beerConsumer.addCommittee(committee2);
            
            Committee committee3 = new Committee();
            committee3.setDescription("BADD - Beers against drunk dorks");
            beerConsumer.addCommittee(committee3);
            
            em.persist(beerConsumer);
            m_noviceBeerConsumerId = beerConsumer.getId();
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation for a novice beer consumer: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
        
        clearCache();
        em = createEntityManager();
        BeerConsumer refreshedBC = em.find(BeerConsumer.class, m_noviceBeerConsumerId);
        assertTrue("The novice beer consumer read back did not match the original", getServerSession().compareObjects(beerConsumer, refreshedBC));
    }
    
    public void testReadNoviceBeerConsumer() {
        NoviceBeerConsumer consumer = createEntityManager().find(NoviceBeerConsumer.class, m_noviceBeerConsumerId);
        
        assertTrue("Error on reading back a NoviceBeerConsumer", consumer != null);
        
        assertTrue("IQ Level was not persisted.", consumer.getIQ() == 100);
        
        assertTrue("Incorrect number of acclaims returned.", consumer.getAcclaims().size() == 3);
        assertTrue("Missing acclaim - 1", consumer.getAcclaims().contains(1));
        assertTrue("Missing acclaim - 2", consumer.getAcclaims().contains(2));
        assertTrue("Missing acclaim - 3", consumer.getAcclaims().contains(3));
        
        assertTrue("Incorrect number of awards returned.", consumer.getAwards().size() == 3);
        Integer awardCode = consumer.getAwards().get(1);
        assertFalse("Missing award code - 1", awardCode == null);
        assertTrue("Award code 1 is incorrect", awardCode.equals(1));
        
        awardCode = consumer.getAwards().get(2);
        assertFalse("Missing award code - 2", awardCode == null);
        assertTrue("Award code 2 is incorrect", awardCode.equals(2));
        
        awardCode = consumer.getAwards().get(3);
        assertFalse("Missing award code - 3", awardCode == null);
        assertTrue("Award code 3 is incorrect", awardCode.equals(3));    
        
        assertTrue("Incorrect number of designations returned.", consumer.getDesignations().size() == 5);
        assertTrue("Missing designation - 5 at index 0", consumer.getDesignations().get(0).equals("5"));
        assertTrue("Missing designation - 4 at index 1", consumer.getDesignations().get(1).equals("4"));
        assertTrue("Missing designation - 2 at index 2", consumer.getDesignations().get(2).equals("2"));
        assertTrue("Missing designation - 3 at index 3", consumer.getDesignations().get(3).equals("3"));
        assertTrue("Missing designation - 1 at index 4", consumer.getDesignations().get(4).equals("1"));
        
        assertTrue("Incorrect number of records returned.", consumer.getRecords().size() == 1);
    }
    
    public void testReadExpertBeerConsumer() {
        ExpertBeerConsumer consumer = createEntityManager().find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
        
        assertTrue("Error on reading back an ExpertBeerConsumer", consumer != null);
        
        assertTrue("IQ Level was not persisted.", consumer.getIQ() == 110);
        
        assertTrue("Incorrect number of acclaims returned.", consumer.getAcclaims().size() == 3);
        assertTrue("Missing acclaim - A", consumer.getAcclaims().contains("A"));
        assertTrue("Missing acclaim - B", consumer.getAcclaims().contains("B"));
        assertTrue("Missing acclaim - C", consumer.getAcclaims().contains("C"));
        
        // Commenting out this mapping until bug 272298 is resolved.
        //assertTrue("Incorrect number of audio returned.", consumer.getAudio().size() == 3);
        // don't individually check them, assume they are correct.
        
        assertTrue("Incorrect number of awards returned.", consumer.getAwards().size() == 3);
        String awardCode = consumer.getAwards().get("A");
        assertFalse("Missing award code - A", awardCode == null);
        assertTrue("Award code A is incorrect", awardCode.equals("A"));
        
        awardCode = consumer.getAwards().get("B");
        assertFalse("Missing award code - B", awardCode == null);
        assertTrue("Award code B is incorrect", awardCode.equals("B"));
        
        awardCode = consumer.getAwards().get("C");
        assertFalse("Missing award code - C", awardCode == null);
        assertTrue("Award code C is incorrect", awardCode.equals("C"));
        
        assertTrue("Incorrect number of designations returned.", consumer.getDesignations().size() == 2);
        assertTrue("Missing designation - A", consumer.getDesignations().contains("A"));
        assertTrue("Missing designation - B", consumer.getDesignations().contains("B"));
        
        assertTrue("Incorrect number of quotes returned.", consumer.getQuotes().size() == 2);
        String quote = consumer.getQuotes().get(m_quote1Stamp);
        assertFalse("Missing quote from Jan 1, 2009", quote == null);
        assertTrue("Quote from Jan 1, 2009 was incorrect", quote.equals(QUOTE_ONE));
        
        quote = consumer.getQuotes().get(m_quote2Stamp);
        assertFalse("Missing quote from Jul 9, 2005", quote == null);
        assertTrue("Quote from Jul 9, 2005 was incorrect", quote.equals(QUOTE_TWO));
        
        assertTrue("Incorrect number of records returned.", consumer.getRecords().size() == 2);
        // don't individually check them ... assume they are correct.
    }
    
    public void testHeinekenBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        BeerConsumer initialBC = new BeerConsumer();
        int beerConsumerId = 0;
        
        try {
            Heineken heineken1  = new Heineken();
            heineken1.setAlcoholContent(5.0);
            
            Heineken heineken2  = new Heineken();
            heineken2.setAlcoholContent(5.0);
            
            Heineken heineken3  = new Heineken();
            heineken3.setAlcoholContent(5.0);
            
            initialBC.setName("Heineken Consumer");
            Calendar cal = Calendar.getInstance();
            cal.set(2008, 12, 12);
            initialBC.addHeinekenBeerToConsume(heineken1, cal.getTime());
            cal.set(2009, 1, 1);
            initialBC.addHeinekenBeerToConsume(heineken2, cal.getTime());
            cal.set(2009, 2, 2);
            initialBC.addHeinekenBeerToConsume(heineken3, cal.getTime());
            
            em.persist(initialBC);
            beerConsumerId = initialBC.getId();
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
        
        clearCache();
        em = createEntityManager();
        BeerConsumer refreshedBC = em.find(BeerConsumer.class, beerConsumerId);
        assertTrue("The beer consumer read back did not match the original", getServerSession().compareObjects(initialBC, refreshedBC));
        
    }
    
    public void testNamedNativeQueryBeerConsumers() {
        List consumers = createEntityManager().createNamedQuery("findAnySQLBeerConsumer").getResultList();
        assertTrue("Error executing native query 'findAnySQLBeerConsumer'", consumers != null);
    }

    public void testNamedNativeQueryCertifications() {
        List certifications = createEntityManager().createNamedQuery("findAllSQLCertifications").getResultList();
        assertTrue("Error executing native query 'findAllSQLCertifications'", certifications != null);
    }

    public void testDeleteBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            em.remove(em.find(BeerConsumer.class, beerConsumerId));
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw ex;
        }
        assertTrue("Error deleting BeerConsumer", em.find(BeerConsumer.class, beerConsumerId) == null);
    }

    public void testReadBeerConsumer() {
        BeerConsumer consumer = createEntityManager().find(BeerConsumer.class, beerConsumerId);
        assertTrue("Error reading BeerConsumer", consumer.getId() == beerConsumerId);
    }

    public void testUpdateBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
        
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, beerConsumerId);
            beerConsumer.setName("Joe White");
            
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw ex;
        }
        clearCache();

        BeerConsumer newBeerConsumer = em.find(BeerConsumer.class, beerConsumerId);
        closeEntityManager(em);
        assertTrue("Error updating BeerConsumer name", newBeerConsumer.getName().equals("Joe White"));
    }
    
    //Merge Test:Have a class(TelephoneNumber) that uses a composite primary key (defined in annotations) and define a 1-M (BeerConsumer->TelephoneNumber) for it in XML
    //Setup Relationship
    public void testOneToManyRelationships() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            
            BeerConsumer consumer = new BeerConsumer();
            consumer.setName("Joe Black");
            
            TelephoneNumber homeNumber = new TelephoneNumber();
            homeNumber.setAreaCode("555");
            homeNumber.setType("Home");
            homeNumber.setNumber("123-1234");
            
            TelephoneNumber workNumber = new TelephoneNumber();
            workNumber.setAreaCode("555");
            workNumber.setType("Work");
            workNumber.setNumber("987-9876");
             
            consumer.addTelephoneNumber(homeNumber);
            consumer.addTelephoneNumber(workNumber);
            em.persist(consumer);
            beerConsumerId = consumer.getId();
            
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                            rollbackTransaction(em);
                        }
            throw e;
        }
        
    }
    
    public void testOrderColumnNoviceBeerConsumerDesignations() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        NoviceBeerConsumer beerConsumer;
        
        try {
            // These are the designations we have right now ... 
            // index: 0 - 1 - 2 - 3 - 4
            //  item: 5 - 4 - 2 - 3 - 1
           beerConsumer = em.find(NoviceBeerConsumer.class, m_noviceBeerConsumerId);
            
            String stringFour = beerConsumer.getDesignations().remove(1);
            String stringTwo = beerConsumer.getDesignations().remove(1);
            beerConsumer.getDesignations().add(stringTwo);
            
            // This is what we have done 
            // index: 0 - 1 - 2 - 3
            //  item: 5 - 3 - 1 - 2

            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        
        closeEntityManager(em);
        
        clearCache();
        em = createEntityManager();
        BeerConsumer refreshedBC = em.find(BeerConsumer.class, m_noviceBeerConsumerId);       
        assertTrue("The novice beer consumer read back did not match the original", getServerSession().compareObjects(beerConsumer, refreshedBC));
    }
    
    //Verify Relationship
    public void testVerifyOneToManyRelationships() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            
            BeerConsumer cm = em.find(BeerConsumer.class, beerConsumerId);
            java.util.Collection phones = cm.getTelephoneNumbers().values();
            assertTrue("Wrong phonenumbers associated with BeerConsumer", phones.size() == 2);
            for (Iterator iterator = phones.iterator(); iterator.hasNext();){
                    TelephoneNumber phone = (TelephoneNumber)(iterator.next());
                    assertTrue("Wrong owner of the telephone",phone.getBeerConsumer().getId() == beerConsumerId);
            }
            
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
    }

    public void testColumnUpdatableAndInsertable() {
        EntityManager em = createEntityManager();
        
        try {
            // Create an official
            beginTransaction(em);
            Official initialOfficial = new Official();
            initialOfficial.setName("Gui Pelletier"); // insertable=true, updatable=false
            initialOfficial.setAge(25); // insertable=false, updatable=true
            initialOfficial.setSalary(50000); // insertable=true, updatable=false
            initialOfficial.setBonus(10000); // insertable=false, updatable=true
            
            ServiceTime service = new ServiceTime();
            service.setStartDate("Jan 1, 2008"); // insertable=true, updatable=false 
            service.setEndDate("Jul 1, 2010"); // insertable=false, updatable=true
            initialOfficial.setServiceTime(service);
            
            em.persist(initialOfficial);
            
            commitTransaction(em);
            
            // Close the EM, clear cache and get new EM.
            closeEntityManager(em);
            clearCache();
            em = createEntityManager();
            
            // Read the official and verify its content
            beginTransaction(em);
            Official official = em.find(Official.class, initialOfficial.getId());
            assertTrue("The name was not inserted", official.getName().equals("Gui Pelletier"));
            assertTrue("The age was inserted", official.getAge() == null);
            assertTrue("The salary was not inserted", official.getSalary() == 50000);
            assertTrue("The bonus was inserted", official.getBonus() == null);
            assertTrue("The embeddable start date was not inserted", official.getServiceTime().getStartDate().equals("Jan 1, 2008"));
            assertTrue("The embeddable end date was inserted", official.getServiceTime().getEndDate() == null);
            
            // Change the updatable=false fields:
            official.setName("Guy Pelletier");
            official.setSalary(100000);
            official.getServiceTime().setStartDate("Jan 30, 2008"); 
            
            // Update the insertable=false fields: 
            official.setAge(25);
            official.setBonus(10000);
            official.getServiceTime().setEndDate("Jul 1, 2010");
            
            commitTransaction(em);
            
            // Close the EM, clear cache and get new EM.
            closeEntityManager(em);
            clearCache();
            em = createEntityManager();
            
            // The refreshed official at this point should not have had any
            // update changes to name but age should now be updated.
            Official refreshedOfficial = em.find(Official.class, initialOfficial.getId());       
            assertTrue("The refreshedOfficial did not match the original", getServerSession().compareObjects(initialOfficial, refreshedOfficial));
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testColumnUpdatableAndInsertableThroughQuery() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test testColumnUpdatableAndInsertableThroughQuery skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

        EntityManager em = createEntityManager();
        
        try {
            // Create an official
            beginTransaction(em);
            Official initialOfficial = new Official();
            initialOfficial.setName("Gui Pelletier");
            em.persist(initialOfficial);
            commitTransaction(em);
            
            // Close the EM, clear cache and get new EM.
            closeEntityManager(em);
            clearCache();
            em = createEntityManager();
            
            // Update the official using a named query.
            beginTransaction(em);
            Query query = em.createNamedQuery("UpdateXMLOfficalName");
            query.setParameter("name", "Guy");
            query.setParameter("id", initialOfficial.getId());
            query.executeUpdate();
            Official modifiedOfficial = em.find(Official.class, initialOfficial.getId());
            assertTrue("The name was not updated after executing the named query", modifiedOfficial.getName().equals("Guy"));
            commitTransaction(em);
            
            // Close the EM, clear cache and get new EM.
            closeEntityManager(em);
            clearCache();
            em = createEntityManager();
            
            Official refreshedOfficial = em.find(Official.class, modifiedOfficial.getId());       
            assertTrue("The refreshedOfficial did not match the modified", getServerSession().compareObjects(modifiedOfficial, refreshedOfficial));
            
        } catch (Exception e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            fail("Update query failed: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }
}
