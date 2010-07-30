/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
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
 *     06/18/2010-2.2 Guy Pelletier 
 *       - 300458: EclispeLink should throw a more specific exception than NPE
 *     07/16/2010-2.2 Guy Pelletier 
 *       - 260296: mixed access with no Transient annotation does not result in error
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.inherited;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inherited.Accredidation;
import org.eclipse.persistence.testing.models.jpa.inherited.Becks;
import org.eclipse.persistence.testing.models.jpa.inherited.BecksTag;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Birthday;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.Alpine;
import org.eclipse.persistence.testing.models.jpa.inherited.BlueLight;
import org.eclipse.persistence.testing.models.jpa.inherited.BuildingBylaw;
import org.eclipse.persistence.testing.models.jpa.inherited.Committee;
import org.eclipse.persistence.testing.models.jpa.inherited.Corona;
import org.eclipse.persistence.testing.models.jpa.inherited.CoronaTag;
import org.eclipse.persistence.testing.models.jpa.inherited.ExpertBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Heineken;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
import org.eclipse.persistence.testing.models.jpa.inherited.Location;
import org.eclipse.persistence.testing.models.jpa.inherited.NoiseBylaw;
import org.eclipse.persistence.testing.models.jpa.inherited.NoviceBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Official;
import org.eclipse.persistence.testing.models.jpa.inherited.OfficialEntry;
import org.eclipse.persistence.testing.models.jpa.inherited.Record;
import org.eclipse.persistence.testing.models.jpa.inherited.RedStripe;
import org.eclipse.persistence.testing.models.jpa.inherited.SerialNumber;
import org.eclipse.persistence.testing.models.jpa.inherited.Venue;
import org.eclipse.persistence.testing.models.jpa.inherited.Witness;
import org.eclipse.persistence.testing.models.jpa.inherited.ServiceTime;
 
public class InheritedModelJunitTest extends JUnitTestCase {
    private static BigDecimal m_blueId;
    private static Integer m_beerConsumerId;
    private static Integer m_noviceBeerConsumerId;
    private static Integer m_expertBeerConsumerId;
    
    private static Timestamp m_quote1Stamp;
    private static final String QUOTE_ONE = "Beer is blood";
    private static Timestamp m_quote2Stamp;
    private static final String QUOTE_TWO = "My first wife was a beer";
    
    public InheritedModelJunitTest() {
        super();
    }
    
    public InheritedModelJunitTest(String name) {
        super(name);
    }
    
    public void setUp() {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("InheritedModelJunitTest");
        
        suite.addTest(new InheritedModelJunitTest("testSetup"));
        suite.addTest(new InheritedModelJunitTest("testCreateBlue"));
        suite.addTest(new InheritedModelJunitTest("testReadBlue"));
        suite.addTest(new InheritedModelJunitTest("testCreateBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testCreateNoviceBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testReadNoviceBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testOrderColumnNoviceBeerConsumerDesignations"));
        suite.addTest(new InheritedModelJunitTest("testCreateExpertBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testReadExpertBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testExpertBeerConsumerRecordsCRUD"));
        suite.addTest(new InheritedModelJunitTest("testUpdateBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testInheritedClone"));
        suite.addTest(new InheritedModelJunitTest("testCascadeRemove"));
        // 1-M map using direct key
        suite.addTest(new InheritedModelJunitTest("testHeinekenBeerConsumer"));
        // 1-M map using entity key
        suite.addTest(new InheritedModelJunitTest("testBecksBeerConsumer"));
        // 1-M map using embeddable key
        suite.addTest(new InheritedModelJunitTest("testCoronaBeerConsumer"));
        // Element collection with generic map key type, with embeddable values.
        suite.addTest(new InheritedModelJunitTest("testRedStripeExpertConsumer"));
        // Element collection with generic map key type, with embeddable values.
        suite.addTest(new InheritedModelJunitTest("testRedStripeNoviceConsumer"));
        // OrderColumn with OrderCorrectionType.EXCEPTION
        suite.addTest(new InheritedModelJunitTest("testBreakOrder_CorrectionType_EXCEPTION"));
        // OrderColumn with OrderCorrectionType.READ_WRITE
        suite.addTest(new InheritedModelJunitTest("testBreakOrder_CorrectionType_READ_WRITE"));
        
        suite.addTest(new InheritedModelJunitTest("testMapOrphanRemoval"));
        
        suite.addTest(new InheritedModelJunitTest("testSerializedElementCollectionMap"));
        suite.addTest(new InheritedModelJunitTest("testVersionUpdateOnElementCollectionChange"));
        
        suite.addTest(new InheritedModelJunitTest("testAddToHeinekenBeerConsumerMap"));
        suite.addTest(new InheritedModelJunitTest("testColumnUpdatableAndInsertable"));
        suite.addTest(new InheritedModelJunitTest("testColumnUpdatableAndInsertableThroughQuery"));
        suite.addTest(new InheritedModelJunitTest("testElementCollectionMapEmbeddable"));
        suite.addTest(new InheritedModelJunitTest("testMultipleIdButNonIdClassEntity"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritedTableManager().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    public void  testMultipleIdButNonIdClassEntity() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        NoiseBylaw noiseBylaw = new NoiseBylaw();
        int noiseBylawId = 0;
        BuildingBylaw buildingBylaw = new BuildingBylaw();
        int buildingBylawId = 0;
        
        try {
            noiseBylaw.setCity("Ottawa");
            noiseBylaw.setDescription("Can't mow your grass after 9PM!");
            em.persist(noiseBylaw);
            
            buildingBylaw.setCity("Ottawa");
            buildingBylaw.setDescription("Can't build without a permit");
            em.persist(buildingBylaw);
            
            noiseBylawId = noiseBylaw.getNumber();
            buildingBylawId = buildingBylaw.getNumber();
            
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
        
        // find by object entity will not work since there is no IdClass in 
        // this case so we will look it up through jpql
        String jpqlString = "SELECT n FROM NoiseBylaw n WHERE n.number =" + noiseBylawId;
        NoiseBylaw refreshedNoiseBylaw = (NoiseBylaw) em.createQuery(jpqlString).getSingleResult();
        assertTrue("The noise bylaw read back did not match the original", getServerSession().compareObjects(noiseBylaw, refreshedNoiseBylaw));
        
        String jpqlString2 = "SELECT n FROM BuildingBylaw n WHERE n.number =" + buildingBylawId;
        BuildingBylaw refreshedBuildingBylaw = (BuildingBylaw) em.createQuery(jpqlString2).getSingleResult();
        assertTrue("The building bylaw read back did not match the original", getServerSession().compareObjects(buildingBylaw, refreshedBuildingBylaw));
        
        closeEntityManager(em);
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
    
    public void testCreateBlue() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            Blue blue = new Blue();
            blue.setAlcoholContent(new Float(5.3));
            em.persist(blue);
            m_blueId = blue.getId();
            blue.setUniqueKey(m_blueId.toBigInteger());
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        
        closeEntityManager(em);
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
            
            //String stringFour = beerConsumer.getDesignations().remove(1);
            //String stringTwo = beerConsumer.getDesignations().remove(1);
           //beerConsumer.getDesignations().add(stringTwo);
            
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
    
    public void testReadBlue() {
        Blue blue = createEntityManager().find(Blue.class, m_blueId);
        assertTrue("Error on reading back a Blue beer", blue != null);
    }
    
    public void testCreateBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {    
            BeerConsumer beerConsumer = new BeerConsumer();
            beerConsumer.setName("Blue Consumer");
            em.persist(beerConsumer);
            m_beerConsumerId = beerConsumer.getId();
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
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
            Venue venue1 = new Venue();
            venue1.setAttendance(10);
            venue1.setName("Champs-Elysees");
            record1.setVenue(venue1);
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
            // The compareObjects check below would fail in some configurations
            // as a result of this.
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
            Venue venue1 = new Venue();
            venue1.setAttendance(10000);
            venue1.setName("Scotiabank PLace");
            record1.setVenue(venue1);
            beerConsumer.getRecords().add(record1);
            
            Record record2 = new Record();
            record2.setDescription("Most beers consumed in a second - 5");
            record2.setDate(Helper.dateFromYearMonthDate(2005, 12, 12));
            record2.setLocation(new Location("Miami", "USA"));
            Venue venue2 = new Venue();
            venue2.setAttendance(63000);
            venue2.setName("Dolphin Stadium");
            record2.setVenue(venue2);
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
    
    // Bug 296606 - issues with ElementCollections 
    // This test should be run after testCreateExpertBearConsumer
    // This test makes changes, so testReadExpertBeerConsumer will fail after this test.
    public void testExpertBeerConsumerRecordsCRUD() {        
        String errorMsg = "";
        
        int nRecords;
        int nRecordsExpected = 2;
        
        clearCache();
        EntityManager em = createEntityManager();
        try {
            // read all Records inside transaction
            beginTransaction(em);
            ExpertBeerConsumer consumer = em.find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
            // trigger indirection
            nRecords = consumer.getRecords().size();
            // Bug 296606 - issues with ElementCollections cause commit to fail with NPE
            // Before the bug was fixed, the workaround was to annotate Record with @ChangeTracking(ChangeTrackingType.DEFERRED)
            commitTransaction(em);
            closeEntityManager(em);
            if(nRecords != nRecordsExpected) {
                errorMsg += "wrong number of records after read; ";
            }
            
            // remove Record
            em = createEntityManager();
            beginTransaction(em);
            consumer = em.find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
            Record recordToRemove = consumer.getRecords().iterator().next(); 
            consumer.getRecords().remove(recordToRemove);
            commitTransaction(em);
            closeEntityManager(em);
            nRecordsExpected--;
            // verify in cache
            em = createEntityManager();
            consumer = em.find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
            closeEntityManager(em);
            nRecords = consumer.getRecords().size(); 
            if(nRecords != nRecordsExpected) {
                errorMsg += "cache: wrong number of records after remove; ";
            }
            // verify in db
            clearCache();
            em = createEntityManager();
            consumer = em.find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
            closeEntityManager(em);
            nRecords = consumer.getRecords().size(); 
            if(nRecords != nRecordsExpected) {
                errorMsg += "db: wrong number of records after remove; ";
            }
            
            // add Record
            Record record1 = new Record();
            record1.setDescription("Original");
            record1.setDate(Helper.dateFromYearMonthDate(2009, 1, 1));
            record1.setLocation(new Location("Ottawa", "Canada"));
            Venue venue1 = new Venue();
            venue1.setAttendance(10);
            venue1.setName("Original");
            record1.setVenue(venue1);
            em = createEntityManager();
            beginTransaction(em);
            consumer = em.find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
            consumer.getRecords().add(record1);
            commitTransaction(em);
            closeEntityManager(em);
            nRecordsExpected++;
            // verify in cache
            em = createEntityManager();
            consumer = em.find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
            closeEntityManager(em);
            nRecords = consumer.getRecords().size(); 
            if(nRecords != nRecordsExpected) {
                errorMsg += "cache: wrong number of records after add; ";
            }
            // verify in db
            clearCache();
            em = createEntityManager();
            consumer = em.find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
            closeEntityManager(em);
            nRecords = consumer.getRecords().size(); 
            if(nRecords != nRecordsExpected) {
                errorMsg += "db: wrong number of records after add; ";
            }

            // update all Records one by one.
            String newDescription = "New Description ";
            String newName = "New Name ";
            em = createEntityManager();
            beginTransaction(em);
            consumer = em.find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
            int i=0;
            Iterator<Record> it = consumer.getRecords().iterator();
            while(it.hasNext()) {
                Record record = it.next();
                String index = Integer.toString(i++);
                record.setDescription(newDescription + index);
                record.getVenue().setName(newName + index );
            }
            // Bug 296606 - issues with ElementCollections cause commit to fail with NPE
            // Before the bug was fixed, the workaround was to annotate both Record and Venue with @ChangeTracking(ChangeTrackingType.DEFERRED).
            // With that workaround the test still used to fail - Record and Venue were not updated in the data base.
            // Before the bug was fixed, the workaround for that was to set property "eclipselink.weaving.internal" to "false" in persistence.xml.
            commitTransaction(em);
            // verify in cache
            em = createEntityManager();
            consumer = em.find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
            closeEntityManager(em);
            nRecords = consumer.getRecords().size(); 
            if(nRecords != nRecordsExpected) {
                errorMsg += "cache: wrong number of records after update; ";
            }
            HashSet<String> usedDescriptions = new HashSet(nRecords); 
            HashSet<String> usedNames = new HashSet(nRecords); 
            it = consumer.getRecords().iterator();
            while(it.hasNext()) {
                Record record = it.next();
                String description = record.getDescription();
                if(!description.startsWith(newDescription)) {
                    errorMsg += "cache: wrong record description after update; ";
                }
                usedDescriptions.add(description);
                String name = record.getVenue().getName();
                if(!name.startsWith(newName)) {
                    errorMsg += "cache: wrong venue name after update; ";
                }
                usedNames.add(name);
            }
            if(usedDescriptions.size() != nRecords) {
                errorMsg += "cache: records with same description; ";
            }
            if(usedNames.size() != nRecords) {
                errorMsg += "cache: venues with same name; ";
            }
            // verify in db
            clearCache();
            em = createEntityManager();
            consumer = em.find(ExpertBeerConsumer.class, m_expertBeerConsumerId);
            closeEntityManager(em);
            nRecords = consumer.getRecords().size(); 
            if(nRecords != nRecordsExpected) {
                errorMsg += "db: wrong number of records after update; ";
            }
            usedDescriptions.clear(); 
            usedNames.clear(); 
            it = consumer.getRecords().iterator();
            while(it.hasNext()) {
                Record record = it.next();
                String description = record.getDescription();
                if(!description.startsWith(newDescription)) {
                    errorMsg += "db: wrong record description after update; ";
                }
                usedDescriptions.add(description);
                String name = record.getVenue().getName();
                if(!name.startsWith(newName)) {
                    errorMsg += "db: wrong venue name after update; ";
                }
                usedNames.add(name);
            }
            if(usedDescriptions.size() != nRecords) {
                errorMsg += "db: records with same description; ";
            }
            if(usedNames.size() != nRecords) {
                errorMsg += "db: venues with same name; ";
            }
            
            if(errorMsg.length() > 0) {
                fail(errorMsg);
            }
        } finally {
            if(em != null) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                if(isOnServer()) {
                    closeEntityManager(em);
                } else if(em.isOpen()) {
                    closeEntityManager(em);
                }
            }
        }
    }
    
    public void testRedStripeExpertConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        ExpertBeerConsumer initialEBC = new ExpertBeerConsumer();
        initialEBC.setAccredidation(new Accredidation());
        int beerConsumerId = 0;
        
        try {
            RedStripe redStripe1 = new RedStripe();
            redStripe1.setAlcoholContent(5.0);
            initialEBC.addRedStripeBeersToConsume(redStripe1, "1");

            RedStripe redStripe2 = new RedStripe();
            redStripe2.setAlcoholContent(5.0);
            initialEBC.addRedStripeBeersToConsume(redStripe2, "2");
            
            initialEBC.setName("Expert Red Stripe Consumer");
            em.persist(initialEBC);
            beerConsumerId = initialEBC.getId();
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            e.printStackTrace();
            
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
        
        clearCache();
        em = createEntityManager();
        ExpertBeerConsumer refreshedEBC = em.find(ExpertBeerConsumer.class, beerConsumerId);       
        assertTrue("The expert beer consumer read back did not match the original", getServerSession().compareObjects(initialEBC, refreshedEBC));
    }
    
    public void testRedStripeNoviceConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        NoviceBeerConsumer initialNBC = new NoviceBeerConsumer();
        initialNBC.setAccredidation(new Accredidation());
        int beerConsumerId = 0;
        
        try {
            RedStripe redStripe1 = new RedStripe();
            redStripe1.setAlcoholContent(5.0);
            initialNBC.addRedStripeBeersToConsume(redStripe1, "3");

            RedStripe redStripe2 = new RedStripe();
            redStripe2.setAlcoholContent(5.0);
            initialNBC.addRedStripeBeersToConsume(redStripe2, "4");
            
            initialNBC.setName("Novice Red Stripe Consumer");
            em.persist(initialNBC);
            beerConsumerId = initialNBC.getId();
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            e.printStackTrace();
            
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
        
        clearCache();
        em = createEntityManager();
        NoviceBeerConsumer refreshedNBC = em.find(NoviceBeerConsumer.class, beerConsumerId);       
        assertTrue("The novice beer consumer read back did not match the original", getServerSession().compareObjects(initialNBC, refreshedNBC));
    }
    
    public void testUpdateBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {    
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
            
            // Create a detached object that mirrors the beer consumer from
            // testCreateBeerConsumer.
            BeerConsumer beerConsumerDetached = new BeerConsumer();
            beerConsumerDetached.setName(beerConsumer.getName());
            beerConsumerDetached.setId(beerConsumer.getId());
            beerConsumerDetached.setVersion(beerConsumer.getVersion());
            
            Blue blue = em.find(Blue.class, m_blueId);
            beerConsumerDetached.addBlueBeerToConsume(blue);
            
            em.merge(beerConsumerDetached);
            commitTransaction(em);    
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during the merge of the detached beer consumer: [" + e.getMessage() + "]");
        }
        
        closeEntityManager(em);
    }
    
    public void testVersionUpdateOnElementCollectionChange() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            BeerConsumer beerConsumer = (BeerConsumer) em.createQuery("select b from BeerConsumer b join b.redStripes r").getResultList().get(0);
            int currentVersion = beerConsumer.getVersion();
            beerConsumer.getRedStripes().put("version", new RedStripe(Double.valueOf("343")));
            commitTransaction(em);
            assertTrue("Did not increment version for change to element collection", beerConsumer.getVersion() == ++currentVersion);
            
            
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }
        
        closeEntityManager(em);

    }
    
    // Test the clone method works with LAZY attributes at multiple levels of an inheritance hierarchy
    public void testInheritedClone() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        SerialNumber serialNumber = new SerialNumber();
        em.persist(serialNumber);
        Alpine alpine = new Alpine(serialNumber);
        alpine.setBestBeforeDate(Helper.dateFromYearMonthDate(2005, 8, 21));
        alpine.setAlcoholContent(5.0);
        alpine.setClassification(Alpine.Classification.BITTER);
        alpine.addInspectionDate(new Date(System.currentTimeMillis()));
        alpine.getBeerConsumer();
        Alpine clone = null;
        try{
            clone = alpine.clone();
        } catch (CloneNotSupportedException ex){
            fail("Caught CloneNotSupportedException " + ex);
        }
        
        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Keith Alexander");
        
        BlueLight blueLight = new BlueLight();
        blueLight.setAlcoholContent(new Float(4.0));
        blueLight.setUniqueKey(new BigInteger((new Long(System.currentTimeMillis()).toString())));
        em.persist(blueLight);
        consumer.addBlueLightBeerToConsume(blueLight);
        clone.setBeerConsumer(consumer);
        consumer.addAlpineBeerToConsume(clone);
        em.persist(clone);
        em.persist(consumer);
        if (alpine.getBeerConsumer() == clone.getBeerConsumer()) {
            fail("Changing clone beerConsumer changed original.");
        }
        commitTransaction(em);
        Integer alpineId = clone.getId();
        Integer consumerId = consumer.getId();
        BigDecimal blueLightId = blueLight.getId();
        clearCache();
        closeEntityManager(em);
        em = createEntityManager();
        beginTransaction(em);
        alpine = em.find(Alpine.class, alpineId);
        try{
            clone = alpine.clone();
        } catch (CloneNotSupportedException ex){
            fail("Caught CloneNotSupportedException " + ex);
        }
        
        consumer = new BeerConsumer();
        consumer.setName("Frank Keith");
        clone.setBeerConsumer(consumer);
        consumer.addAlpineBeerToConsume(clone);
        clone.getBeerConsumer();
        if (alpine.getBeerConsumer() == clone.getBeerConsumer()) {
            fail("Changing clone beerConsumer changed original.");
        }
        if (alpine.getBeerConsumer() == null) {
            fail("Changing clone address reset original to null.");
        }
        if (clone.getBeerConsumer() != consumer) {
            fail("Changing clone did not work.");
        }
        
        try{
            consumer = em.find(BeerConsumer.class, consumerId);
            BeerConsumer consumerClone = (BeerConsumer)consumer.clone();
            
            Iterator<Alpine> alpineIterator = consumer.getAlpineBeersToConsume().iterator();
            while (alpineIterator.hasNext()){
                alpine = alpineIterator.next();
                assertTrue("The original beerConsumer has an alpine beer with an incorrect back pointer.", consumer == alpine.getBeerConsumer());
            }
            
            alpineIterator = consumerClone.getAlpineBeersToConsume().iterator();
            while (alpineIterator.hasNext()){
                alpine = alpineIterator.next();
                assertTrue("The cloned beerConsumer has an alpine beer with an incorrect back pointer.", consumerClone == alpine.getBeerConsumer());
            }
            
            Iterator<BlueLight> blueLightIterator = consumer.getBlueLightBeersToConsume().iterator();
            while (blueLightIterator.hasNext()){
                blueLight = blueLightIterator.next();
                assertTrue("The original beerConsumer has an BlueLight beer with an incorrect back pointer.", consumer == blueLight.getBeerConsumer());
            }
            
            blueLightIterator = consumerClone.getBlueLightBeersToConsume().iterator();
            while (blueLightIterator.hasNext()){
                blueLight = blueLightIterator.next();
                assertTrue("The cloned beerConsumer has an BlueLight beer with an incorrect back pointer.", consumerClone == blueLight.getBeerConsumer());
            }
        } catch (CloneNotSupportedException e){
            fail("Call to clone threw CloneNotSupportedException");
        }
        
        alpine = em.find(Alpine.class, clone.getId());
        em.remove(alpine);
        blueLight = em.find(BlueLight.class, blueLightId);
        em.remove(blueLight);
        consumer = em.find(BeerConsumer.class, consumerId);
        em.remove(consumer);
        commitTransaction(em);
        closeEntityManager(em);
    }
    
    // BUG 227345
    public void testCascadeRemove() {
        BeerConsumer beerConsumer = null;
        BlueLight blueLightPersisted = null;
        BlueLight blueLightDetached = null;
        EntityManager em = createEntityManager();

        // Create and persist the beerConsumer
        try {
            beginTransaction(em);
            beerConsumer = new BeerConsumer();
            beerConsumer.setName("Beer Man");

            blueLightPersisted = new BlueLight();
            beerConsumer.getBlueLightBeersToConsume().add(blueLightPersisted);
            blueLightPersisted.setBeerConsumer(beerConsumer);

            em.persist(beerConsumer);

            // Unique key must be set before commit.
            blueLightPersisted.setUniqueKey(blueLightPersisted.getId().toBigInteger());

            // They should be known by the EM
            assertTrue(em.contains(beerConsumer));
            assertTrue(em.contains(blueLightPersisted));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

        // Create BlueLightDetached and manage the relations
        try {
            beginTransaction(em);
            blueLightDetached = new BlueLight();
            blueLightDetached.setUniqueKey(new BigDecimal(blueLightPersisted.getUniqueKey().intValue() + 1).toBigInteger());

            // Set the pointers
            beerConsumer.getBlueLightBeersToConsume().add(blueLightDetached);
            blueLightDetached.setBeerConsumer(beerConsumer);

            // And now remove the beer consumer. The remove-operation should cascade
            if (isOnServer()){
                beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
            }
            em.remove(beerConsumer);

            // It's o.k. should be detached
            assertFalse(em.contains(blueLightDetached));
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

        try {
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            fail("An exception was caught during the remove of the BeerConsumer: [" + e.getMessage() + "]");
        }

        // Ensure neither the beer consumer nor the blue light
        // persisted/detached are available.
        assertFalse("The beer consumer was not removed", em.contains(beerConsumer));
        assertFalse("The blue light persisted was not removed even though the its owning beer comsumer was removed", em.contains(blueLightPersisted));
        assertFalse("The blue light detached was persisted even though the its owning beer comsumer was removed", em.contains(blueLightDetached));

        closeEntityManager(em);
    }
    
    public void testBreakOrder_CorrectionType_EXCEPTION() {
        // create BeerConsumer with designations
        EntityManager em = createEntityManager();
        beginTransaction(em);        
        NoviceBeerConsumer beerConsumer = new NoviceBeerConsumer();
        beerConsumer.setAccredidation(new Accredidation());
        beerConsumer.setName("Broken order");        
        beerConsumer.getDesignations().add("0");
        beerConsumer.getDesignations().add("1");
        beerConsumer.getDesignations().add("2");
        try {
            em.persist(beerConsumer);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            fail("failed to create beerConsumer");
        }            
        Integer id = beerConsumer.getId();
        
        try {
        
            // break the order of designations
            beginTransaction(em); 
            try {
                em.createNativeQuery("UPDATE NOVICE_CONSUMER_DESIGNATIONS SET ORDER_COLUMN = null WHERE NOVICE_CONSUMER_ID = " + id +" AND ORDER_COLUMN = 0").executeUpdate();            
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                fail("failed to break order of designations");
            } finally {
                closeEntityManager(em);
            }
            
            // remove beerConsumer from the cache
            clearCache();
            
            // read back the beer consumer, reading of the list of designations with the broken order should trigger exception
            em = createEntityManager();
            try {
                beerConsumer = em.find(NoviceBeerConsumer.class, id);
                // trigger indirection
                beerConsumer.getDesignations().size();
                fail("Exception was expected - but not thrown");
            } catch (RuntimeException e) {
                boolean isCorrectException = false;
                if(e instanceof QueryException) {
                    QueryException queryException = (QueryException)e;
                    if(((QueryException) e).getErrorCode() == QueryException.LIST_ORDER_FIELD_WRONG_VALUE) {
                        isCorrectException = true;
                    }
                }
                if(!isCorrectException) {
                    throw e;
                }
            } finally {
                closeEntityManager(em);
            }
        } finally {        
            // clean up
            em = createEntityManager();
            beginTransaction(em); 
            try {
                beerConsumer = em.find(NoviceBeerConsumer.class, id);
                em.remove(beerConsumer);
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            } finally {
                closeEntityManager(em);
            }
        }
    }

    public void testBreakOrder_CorrectionType_READ_WRITE() {
        // create BeerConsumer with committees
        EntityManager em = createEntityManager();
        beginTransaction(em);        
        ExpertBeerConsumer beerConsumer = new ExpertBeerConsumer();
        beerConsumer.setName("Beer order");
        beerConsumer.setAccredidation(new Accredidation());

        Committee committee0 = new Committee();
        committee0.setDescription("Broken Order 0");
        beerConsumer.addCommittee(committee0);
        
        Committee committee1 = new Committee();
        committee1.setDescription("Broken Order 1");
        beerConsumer.addCommittee(committee1);
        
        Committee committee2 = new Committee();
        committee2.setDescription("Broken Order 2");
        beerConsumer.addCommittee(committee2);
        try {
            em.persist(beerConsumer);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            fail("failed to create beerConsumer");
        }            
        Integer id = beerConsumer.getId();
        
        try {
        
            // break the order of committees
            beginTransaction(em); 
            try {
                em.createNativeQuery("UPDATE JPA_CONSUMER_COMMITTEE SET ORDER_COLUMN = null WHERE CONSUMER_ID = " + id +" AND ORDER_COLUMN = 0").executeUpdate();            
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                fail("failed to break order of committees");
            } finally {
                closeEntityManager(em);
            }
            
            // remove beerConsumer from the cache
            clearCache();
            
            // read back the beer consumer, reading of the list of committees with the broken should fix the order "on the fly":
            // the default correction performed on the list substitutes null in the first element back to 0;
            // then alter order of committees - that would cause the list order to be updated in the db (and become valid).
            em = createEntityManager();
            beginTransaction(em); 
            try {
                beerConsumer = em.find(ExpertBeerConsumer.class, id);
                // committees #1 and #2 switch their positions in the list.
                Committee committee = beerConsumer.getCommittees().remove(2);
                beerConsumer.getCommittees().add(1, committee);
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                fail("failed to change committees order");
            }
            
            // now verify that the order in the db has been corrected
            List<Number> results = em.createNativeQuery("SELECT ORDER_COLUMN FROM JPA_CONSUMER_COMMITTEE WHERE CONSUMER_ID = " +id+ " ORDER BY ORDER_COLUMN").getResultList();
            int expectedSize = beerConsumer.getCommittees().size();
            if(expectedSize != results.size()) {
                closeEntityManager(em);
                fail("read in "+ results.size() + " committees; expected " + expectedSize);
            }
            for(int i=0; i < expectedSize; i++) {
                if(results.get(i) == null || results.get(i).intValue() != i) {
                    closeEntityManager(em);
                    fail("read in list in wrong order: " + results + "; expected: 0, 1, 2");
                }
            }
            closeEntityManager(em);
            
        } finally {        
            // clean up
            em = createEntityManager();
            beginTransaction(em); 
            try {
                beerConsumer = em.find(ExpertBeerConsumer.class, id);
                em.remove(beerConsumer);
                commitTransaction(em);
            } catch (RuntimeException e) {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
            } finally {
                closeEntityManager(em);
            }
        }
    }
    
    public void testMapOrphanRemoval(){
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

            initialBC.setName("Becks Consumer");
            initialBC.addBecksBeerToConsume(becks1, becksTag1);
            
            em.persist(initialBC);
            beerConsumerId = initialBC.getId();
            
            em.flush();
            
            clearCache();

            BeerConsumer refreshedBC = em.find(BeerConsumer.class, beerConsumerId);
            refreshedBC.getBecksBeersToConsume().remove(becksTag1);
            
            em.flush();
            
            clearCache();
            becksTag1 = (BecksTag)em.find(BecksTag.class, becksTag1.getId());
            assertTrue("Key was deleted when it should not be.", becksTag1 != null);
            becks1 = (Becks)em.find(Becks.class, becks1.getId());
            assertTrue("Orphan removal did not remove the orphan", becks1 == null);
            
            rollbackTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
    }
    
    public void testSerializedElementCollectionMap(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try{
            BeerConsumer initialBC = new BeerConsumer();
            em.persist(initialBC);
            
            SerialNumber serialNumber = new SerialNumber();
            em.persist(serialNumber);
            
            initialBC.addCommentLookup(serialNumber, "A test comment");
            
            em.flush();
            em.clear();
            
            initialBC = em.find(BeerConsumer.class, initialBC.getId());
            serialNumber = em.find(SerialNumber.class, serialNumber.getNumber());
            
            assertTrue("The serialized map was not properly retrieved after persist.", initialBC.getCommentLookup().size() == 1);
            assertTrue("The serialized map did not contain the proper entry.", initialBC.getCommentLookup().get(serialNumber) != null);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
    
    // this method is a test for the fix for bug 299847, prior to the fix, it fails when
    // weaving is disabled
    public void testAddToHeinekenBeerConsumerMap() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        int beerConsumerId = 0;
        int heinekenId = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(2008, 12, 12);
        try {
            BeerConsumer initialBC = new BeerConsumer();
            initialBC.setName("Heineken Consumer");
            em.persist(initialBC);
            Heineken heineken1  = new Heineken();
            heineken1.setAlcoholContent(5.0);
            em.persist(heineken1);
            commitTransaction(em);

            beerConsumerId = initialBC.getId();
            heinekenId = heineken1.getId();
            
            clearCache();
            
            beginTransaction(em);

            initialBC = em.find(BeerConsumer.class, initialBC.getId());
            heineken1 = em.find(Heineken.class, heineken1.getId());
            initialBC.addHeinekenBeerToConsume(heineken1, cal.getTime());
            commitTransaction(em);
            
            clearCache();
            
            initialBC = em.find(BeerConsumer.class, initialBC.getId());
            heineken1 = em.find(Heineken.class, heineken1.getId());
            assertTrue("The beer consumer is not appropriately associated with the heineken.", heineken1.getBeerConsumer() != null);
            assertTrue("The heineken is not appropriately associated with the beer consumer.", initialBC.getHeinekenBeersToConsume().size() == 1);
        } catch (RuntimeException e) {
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            beginTransaction(em);
            BeerConsumer consumer = em.find(BeerConsumer.class, beerConsumerId);
            Heineken heineken = em.find(Heineken.class, heinekenId);
            if (consumer != null){
                em.remove(consumer);
            }
            if (heineken != null){
                em.remove(heineken);
            }
            
            commitTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testColumnUpdatableAndInsertable() {
        EntityManager em = createEntityManager();
        
        try {
            // Create an official
            beginTransaction(em);
            
            OfficialEntry officialEntry = new OfficialEntry();
            em.persist(officialEntry);
            
            Official initialOfficial = new Official();
            initialOfficial.setName("Gui Pelletier"); // insertable=true, updatable=false
            initialOfficial.setAge(25); // insertable=false, updatable=true
            initialOfficial.setSalary(50000); // insertable=true, updatable=false
            initialOfficial.setBonus(10000); // insertable=false, updatable=true
            
            // Tests multiple mappings to the same column. The M-1 is read only
            // and the basic is the writable mapping.
            initialOfficial.setOfficialEntry(officialEntry); // insertable=false, updatable=false
            initialOfficial.setOfficialEntryId(officialEntry.getId()); // insertable=true, updatable=true
            
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
            assertTrue("The official entry was not inserted", official.getOfficialEntryId() == officialEntry.getId());
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
            Query query = em.createNamedQuery("UpdateOfficalName");
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
    
    public void testElementCollectionMapEmbeddable(){
        EntityManager em = createEntityManager();
        
        try {
            // Create an official
            beginTransaction(em);
            BeerConsumer consumer = new BeerConsumer();
            consumer.setName("Lionel");
            RedStripe rs = new RedStripe();
            rs.setAlcoholContent(4.5);
            consumer.addRedStripeByAlcoholContent(rs);
            em.persist(consumer);
            em.flush();
            
            rs = new RedStripe();
            rs.setAlcoholContent(3.5);
            consumer.addRedStripeByAlcoholContent(rs);
            em.flush();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            rollbackTransaction(em);
        }
    }
}
