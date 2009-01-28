/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     01/28/2009-1.1 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.inherited;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Alpine;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Location;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.NoviceBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.ExpertBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Record;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Canadian;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.Certification;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.InheritedTableManager;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.inherited.TelephoneNumber;
 
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
        TestSuite suite = new TestSuite("Inherited Model");
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testSetup"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testOneToManyRelationships"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testVerifyOneToManyRelationships"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testCreateBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testReadBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testCreateNoviceBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testReadNoviceBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testCreateExpertBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testReadExpertBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testNamedNativeQueryBeerConsumers"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testNamedNativeQueryCertifications"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testUpdateBeerConsumer"));
        suite.addTest(new EntityMappingsInheritedJUnitTestCase("testDeleteBeerConsumer"));
        
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
    
    public void testCreateBeerConsumer() {
        boolean exceptionCaught = false;
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            BeerConsumer consumer = new BeerConsumer();
            consumer.setName("Joe Black");

            em.persist(consumer);
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
        
        try {    
            ExpertBeerConsumer beerConsumer = new ExpertBeerConsumer();
            beerConsumer.setName("Expert Beer Consumer");
            beerConsumer.setIQ(110);
            
            beerConsumer.getAcclaims().add("A");
            beerConsumer.getAcclaims().add("B");
            beerConsumer.getAcclaims().add("C");
            
            beerConsumer.getAudio().add(new byte[]{1});
            beerConsumer.getAudio().add(new byte[]{2});
            beerConsumer.getAudio().add(new byte[]{3});
            
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
    }
    
    public void testCreateNoviceBeerConsumer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        ServerSession session = JUnitTestCase.getServerSession();
        ClassDescriptor desc = session.getDescriptor(NoviceBeerConsumer.class);
        
        try {    
            NoviceBeerConsumer beerConsumer = new NoviceBeerConsumer();
            beerConsumer.setName("Novice Beer Consumer");
            beerConsumer.setIQ(100);
            
            beerConsumer.getAcclaims().add(1);
            beerConsumer.getAcclaims().add(2);
            beerConsumer.getAcclaims().add(3);
            
            beerConsumer.getAwards().put(1, 1);
            beerConsumer.getAwards().put(2, 2);
            beerConsumer.getAwards().put(3, 3);
            
            beerConsumer.getDesignations().add("1");
            beerConsumer.getDesignations().add("2");

            Record record1 = new Record();
            record1.setDescription("Slowest beer ever consumed - 10 hours");
            record1.setDate(Helper.dateFromYearMonthDate(2008, 1, 1));
            record1.setLocation(new Location("Paris", "France"));
            beerConsumer.getRecords().add(record1);
            
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
        
        assertTrue("Incorrect number of designations returned.", consumer.getDesignations().size() == 2);
        assertTrue("Missing designation - 1", consumer.getDesignations().contains("1"));
        assertTrue("Missing designation - 2", consumer.getDesignations().contains("2"));
        
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
        
        assertTrue("Incorrect number of audio returned.", consumer.getAudio().size() == 3);
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
}
