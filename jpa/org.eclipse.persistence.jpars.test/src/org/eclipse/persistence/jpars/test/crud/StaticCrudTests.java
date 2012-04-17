/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;


import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactory;
import org.eclipse.persistence.jpars.test.model.StaticUser;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test basic CRUD operations on a dynamically created PersistenceContext
 * @author tware
 *
 */
public class StaticCrudTests {

    private static PersistenceContext persistenceContext;
    private static PersistenceFactory factory;
    
    @BeforeClass
    public static void setup(){
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties); 
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        factory = null;
        try{
            factory = new PersistenceFactory();
            persistenceContext = factory.bootstrapPersistenceContext("auction-static", Persistence.createEntityManagerFactory("auction-static", properties), new URI("http://localhost:8080/JPA-RS/"), true);
        } catch (Exception e){
            e.printStackTrace();
            fail(e.toString());
        }
        EntityManager em = persistenceContext.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from StaticBid b").executeUpdate();
        em.createQuery("delete from StaticAuction a").executeUpdate();
        em.createQuery("delete from StaticUser u").executeUpdate();
        em.getTransaction().commit();
        
        persistenceContext = factory.getPersistenceContext("auction-static");
    }
    
    @AfterClass
    public static void tearDown(){
        EntityManager em = persistenceContext.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from StaticBid b").executeUpdate();
        em.createQuery("delete from StaticAuction a").executeUpdate();
        em.createQuery("delete from StaticUser u").executeUpdate();
        em.getTransaction().commit();
        factory.closePersistenceContext("auction-static");
    }
    
    @Test
    public void testCreateAndDelete() {
        StaticUser user = new StaticUser();;
        user.setName("Jim");
        user.setId(1);
        persistenceContext.create(null, user);
        user = (StaticUser)persistenceContext.find("StaticUser", user.getId());
        
        assertNotNull("Entity was note persisted", user);
        assertTrue("Entity Name was incorrect", user.getName().equals("Jim"));
        
        persistenceContext.delete(null, "StaticUser", user.getId());
        
        user = (StaticUser)persistenceContext.find("StaticUser",user.getId());
        
        assertNull("Entity was not deleted", user);
    }

    
    @Test
    @SuppressWarnings("unchecked")
    public void testQuery(){
        StaticUser user = new StaticUser();
        user.setName("Jill");
        user.setId(2);
        persistenceContext.create(null, user);
        
        user = new StaticUser();
        user.setName("Arthur");
        user.setId(3);
        persistenceContext.create(null, user);
        
        user = new StaticUser();
        user.setName("Judy");
        user.setId(4);
        persistenceContext.create(null, user);
        
        List<StaticUser> users = (List<StaticUser>)persistenceContext.query("User.all", null);
        assertTrue(users.size() == 3);
    }
    
    @Test
    public void testUpdate(){
        StaticUser user = new StaticUser();
        user.setName("Tom");
        user.setId(5);
        persistenceContext.create(null, user);
        user = (StaticUser)persistenceContext.find("StaticUser", user.getId());
        user.setName("Thomas");
        persistenceContext.merge(null, user);
        user = (StaticUser)persistenceContext.find("StaticUser", user.getId());
        assertTrue("Entity name was not correctly updated.", user.getName().equals("Thomas"));
        
    }

}
