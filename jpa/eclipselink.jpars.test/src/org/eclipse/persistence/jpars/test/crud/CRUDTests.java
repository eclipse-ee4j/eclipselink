/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.crud;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactory;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.XMLFilePathBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test basic CRUD operations on a dynamically created PersistenceContext
 * @author tware
 *
 */
public class CRUDTests {

    private static PersistenceContext persistenceContext;
    private static PersistenceFactory factory;
    
    @BeforeClass
    public static void setup(){
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties); 
        factory = null;
        try{
            factory = new PersistenceFactory();
            FileInputStream xmlStream = new FileInputStream(XMLFilePathBuilder.getXMLFileName("auction-persistence.xml"));
            persistenceContext = factory.bootstrapPersistenceContext("auction", xmlStream, properties, true);
        } catch (Exception e){
            fail(e.toString());
        }
        EntityManager em = persistenceContext.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from Bid b").executeUpdate();
        em.createQuery("delete from Auction a").executeUpdate();
        em.createQuery("delete from User u").executeUpdate();
        em.getTransaction().commit();
        
        persistenceContext = factory.getPersistenceContext("auction");
    }
    
    @AfterClass
    public static void tearDown(){
        EntityManager em = persistenceContext.getEmf().createEntityManager();
        em.getTransaction().begin();
        try{
            em.createQuery("delete from Bid b").executeUpdate();
            em.createQuery("delete from Auction a").executeUpdate();
            em.createQuery("delete from User u").executeUpdate();
        } catch (Exception e){};
        em.getTransaction().commit();
        factory.closePersistenceContext("auction");
    }
    
    @Test
    public void testCreateAndDelete()  {
        DynamicEntity entity = (DynamicEntity)persistenceContext.newEntity("User");
        entity.set("name", "Jim");
        persistenceContext.create(null, entity);
        entity = (DynamicEntity)persistenceContext.find("User", entity.get("id"));
        
        assertNotNull("Entity was note persisted", entity);
        assertTrue("Entity Name was incorrect", entity.get("name").equals("Jim"));
        
        persistenceContext.delete(null, "User", entity.get("id"));
        
        entity = (DynamicEntity)persistenceContext.find("User", entity.get("id"));
        
        assertNull("Entity was not deleted", entity);
    }

    
    @Test
    @SuppressWarnings("unchecked")
    public void testQuery(){
        DynamicEntity entity = (DynamicEntity)persistenceContext.newEntity("User");
        entity.set("name", "Jill");
        persistenceContext.create(null, entity);
        
        entity = (DynamicEntity)persistenceContext.newEntity("User");
        entity.set("name", "Arthur");
        persistenceContext.create(null, entity);
        
        entity = (DynamicEntity)persistenceContext.newEntity("User");
        entity.set("name", "Judy");
        persistenceContext.create(null, entity);
        
        List<DynamicEntity> users = (List<DynamicEntity>)persistenceContext.query(null, "User.all", null);
        assertTrue(users.size() == 3);
    }
    
    @Test
    public void testUpdate(){
        DynamicEntity entity = (DynamicEntity)persistenceContext.newEntity("User");
        entity.set("name", "Tom");
        persistenceContext.create(null, entity);
        entity = (DynamicEntity)persistenceContext.find("User", entity.get("id"));
        entity.set("name", "Thomas");
        persistenceContext.merge(null, entity);
        entity = (DynamicEntity)persistenceContext.find("User", entity.get("id"));
        assertTrue("Entity name was not correctly updated.", entity.get("name").equals("Thomas"));
        
    }

}
