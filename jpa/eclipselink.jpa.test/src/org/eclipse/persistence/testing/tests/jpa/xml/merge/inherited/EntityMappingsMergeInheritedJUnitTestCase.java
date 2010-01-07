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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     04/02/2009-2.0 Guy Pelletier 
 *       - 270853: testBeerLifeCycleMethodAnnotationIgnored within xml merge testing need to be relocated
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.merge.inherited;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.*;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.merge.inherited.Alpine;
import org.eclipse.persistence.testing.models.jpa.xml.merge.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.xml.merge.inherited.Canadian;
import org.eclipse.persistence.testing.models.jpa.xml.merge.inherited.Certification;
import org.eclipse.persistence.testing.models.jpa.xml.merge.inherited.Beer;
import org.eclipse.persistence.testing.models.jpa.xml.merge.inherited.BeerListener;
import org.eclipse.persistence.testing.models.jpa.xml.merge.inherited.EmbeddedSerialNumber;
import org.eclipse.persistence.testing.models.jpa.xml.merge.inherited.TelephoneNumber;
 
/**
 * JUnit test case(s) for model using a mix of annotations, XML, and XML
 * overrides, with entities defined in separate XML mapping files.
 */
public class EntityMappingsMergeInheritedJUnitTestCase extends JUnitTestCase {
    private static Integer beerConsumerId;
    private static Integer canadianId;
    private static Integer alpineId;
    private static EmbeddedSerialNumber embeddedSerialNumber;
    
    public EntityMappingsMergeInheritedJUnitTestCase() {
        super();
    }
    
    public EntityMappingsMergeInheritedJUnitTestCase(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Inherited Model");
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testSetup"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testOneToManyRelationships"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testVerifyOneToManyRelationships"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testCreateBeerConsumer"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testReadBeerConsumer"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testNamedNativeQueryBeerConsumers"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testNamedNativeQueryCertifications"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testMappedSuperclassTransientField"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testTransientField"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testUpdateBeerConsumer"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testDeleteBeerConsumer"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testBeerLifeCycleMethodAnnotationIgnored"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testMappedSuperclassEntityListener"));
        suite.addTest(new EntityMappingsMergeInheritedJUnitTestCase("testMappedSuperclassEmbeddedXMLElement"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = JUnitTestCase.getServerSession();
        clearCache("ddlGeneration");
    }
    
    public void testCreateBeerConsumer() {
        EntityManager em = createEntityManager("ddlGeneration");
        try {
            beginTransaction(em);

            BeerConsumer consumer = new BeerConsumer();
            consumer.setName("Joe Black");
            em.persist(consumer);
            beerConsumerId = consumer.getId();
            
            Alpine alpine1 = new Alpine();
            alpine1.setAlcoholContent(5.0);
            alpine1.setBestBeforeDate(new Date(System.currentTimeMillis()+10000000));
            alpine1.setLocalTransientString("This should never be persisted");
            em.persist(alpine1);
            alpineId=alpine1.getId();
            consumer.addAlpineBeerToConsume(alpine1);

            embeddedSerialNumber = new EmbeddedSerialNumber();
            embeddedSerialNumber.number = 123456;
            embeddedSerialNumber.setBreweryCode("MOLSON");
             
            Canadian canadian1 = new Canadian();
            canadian1.setAlcoholContent(5.5);
            canadian1.setBeerConsumer(consumer);
            canadian1.setBornOnDate(new Date(System.currentTimeMillis()-30000000));
            canadian1.setTransientString("This should never be persisted");
            canadian1.setEmbeddedSerialNumber(embeddedSerialNumber);
            em.persist(canadian1);
            canadianId=canadian1.getId();
            consumer.getCanadianBeersToConsume().put(canadian1.getId(), canadian1);
            
            Canadian canadian2 = new Canadian();
            canadian2.setAlcoholContent(5.0);
            canadian2.setBeerConsumer(consumer);
            canadian2.setBornOnDate(new Date(System.currentTimeMillis()-23000000));
            em.persist(canadian2);
            consumer.getCanadianBeersToConsume().put(canadian2.getId(), canadian2);

            Certification cert1 = new Certification();
            cert1.setDescription("Value brand beer consumption certified");
            cert1.setBeerConsumer(consumer);
            em.persist(cert1);
            consumer.getCertifications().put(cert1.getId(), cert1);

            Certification cert2 = new Certification();
            cert2.setDescription("Premium brand beer consumption certified");
            cert2.setBeerConsumer(consumer);
            em.persist(cert2);
            consumer.getCertifications().put(cert2.getId(), cert2);

            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }
    }

    public void testNamedNativeQueryBeerConsumers() {
        List consumers = createEntityManager("ddlGeneration").createNamedQuery("findAnyMergeSQLBeerConsumer").getResultList();
        assertTrue("Error executing native query 'findAnyMergeSQLBeerConsumer'", consumers != null);
    }

    public void testNamedNativeQueryCertifications() {
        List certifications = createEntityManager("ddlGeneration").createNamedQuery("findAllMergeSQLCertifications").getResultList();
        assertTrue("Error executing native query 'findAllMergeSQLCertifications'", certifications != null);
    }

    public void testDeleteBeerConsumer() {
        EntityManager em = createEntityManager("ddlGeneration");
        beginTransaction(em);
        try{
            em.remove(em.find(BeerConsumer.class, beerConsumerId));
            commitTransaction(em);
        }catch (RuntimeException ex){
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        assertTrue("Error deleting BeerConsumer", em.find(BeerConsumer.class, beerConsumerId) == null);
    }

    public void testReadBeerConsumer() {
        BeerConsumer consumer = createEntityManager("ddlGeneration").find(BeerConsumer.class, beerConsumerId);
        assertTrue("Error reading BeerConsumer", consumer.getId() == beerConsumerId);
    }

    public void testUpdateBeerConsumer() {
        EntityManager em = createEntityManager("ddlGeneration");
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
        clearCache("ddlGeneration");

        BeerConsumer newBeerConsumer = em.find(BeerConsumer.class, beerConsumerId);
        closeEntityManager(em);
        assertTrue("Error updating BeerConsumer name", newBeerConsumer.getName().equals("Joe White"));
    }
	
        /** 
         * Merge Test:Have a class(TelephoneNumber) that uses a composite primary
         * key (defined partially in annotations and XML) and define a 1-M 
         * (BeerConsumer->TelephoneNumber) for it in XML 
         */
	public void testOneToManyRelationships() {
		EntityManager em = createEntityManager("ddlGeneration");
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
        EntityManager em = createEntityManager("ddlGeneration");
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
    
    // Verify transient property from mapped superclass is not persisted
    public void testMappedSuperclassTransientField() {
        clearCache("ddlGeneration");
        Canadian canadianBeer = createEntityManager("ddlGeneration").find(Canadian.class, canadianId);
        assertTrue("Error reading Canadian", canadianBeer.getId().equals(canadianId));
        assertTrue("Mapped superclass transientString was persisted to the database", canadianBeer.getTransientString() == null);
    }

    // Verify transient property is not persisted
    public void testTransientField() {
        clearCache("ddlGeneration");
        Alpine alpineBeer = createEntityManager("ddlGeneration").find(Alpine.class, alpineId);
        assertTrue("Error reading Alpine", alpineBeer.getId().equals(alpineId));
        assertTrue("localTransientString was persisted to the database", alpineBeer.getLocalTransientString() == null);
    }

    public void testBeerLifeCycleMethodAnnotationIgnored() {
        // Since metadata-complete specified on Beer superclass, all annotations
        // including lifecycle methods should be ignored.
        int beerPrePersistCount = Beer.BEER_PRE_PERSIST_COUNT;
        EntityManager em = createEntityManager("ddlGeneration");
        BeerConsumer consumer = createEntityManager("ddlGeneration").find(BeerConsumer.class, beerConsumerId);

        try {
            beginTransaction(em);

            Canadian canadian1 = new Canadian();
            canadian1.setAlcoholContent(5.5);
            canadian1.setBeerConsumer(consumer);
            canadian1.setBornOnDate(new Date(System.currentTimeMillis()-30000000));
            em.persist(canadian1);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        assertTrue("The callback method [PrePersist] was called.", beerPrePersistCount == Beer.BEER_PRE_PERSIST_COUNT);
    }

    public void testMappedSuperclassEntityListener() {
        int listenerPostPersistCount = BeerListener.POST_PERSIST_COUNT;
        EntityManager em = createEntityManager("ddlGeneration");
        BeerConsumer consumer = createEntityManager("ddlGeneration").find(BeerConsumer.class, beerConsumerId);

        try {
            beginTransaction(em);
            Canadian canadian1 = new Canadian();
            canadian1.setAlcoholContent(5.5);
            canadian1.setBeerConsumer(consumer);
            canadian1.setBornOnDate(new Date(System.currentTimeMillis()-30000000));
            em.persist(canadian1);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw ex;
        }
        
        assertFalse("The listener callback method [PostPersist] was not called.", listenerPostPersistCount == BeerListener.POST_PERSIST_COUNT);
    }
    
    public void testMappedSuperclassEmbeddedXMLElement() {
        /**
         * Canadian canadianBeer = (Canadian) createEntityManager("ddlGeneration").find(Canadian.class, canadianId);
         * assertTrue("Error reading Canadian", canadianBeer.getId() == canadianId);
         * assertTrue("Mapped superclass embedded element was not processed correctly", (canadianBeer.getEmbeddedSerialNumber().getNumber() == embeddedSerialNumber.getNumber())
         *    &&(canadianBeer.getEmbeddedSerialNumber().getBreweryCode().equals(embeddedSerialNumber.getBreweryCode())));        
         */
    }

}
