/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.marshal.json;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpars.test.model.auction.StaticAuction;
import org.eclipse.persistence.jpars.test.model.auction.StaticBid;
import org.eclipse.persistence.jpars.test.model.auction.StaticUser;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestMarshallJSON {

    private static final String DEFAULT_SERVER_URI_BASE = "http://localhost:9090";
    private static final String DEFAULT_PU = "jpars_auction-static";
    private static PersistenceContext context = null;
    
    @BeforeClass
    public static void setup() throws URISyntaxException{
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties); 
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));

        PersistenceFactoryBase factory = new PersistenceFactoryBase();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(DEFAULT_PU, properties);
        context = factory.bootstrapPersistenceContext(DEFAULT_PU, emf, new URI(DEFAULT_SERVER_URI_BASE + "/JPA-RS/"), null, false);
    }
    
    @Test
    public void testMarshalOneToOne()throws JAXBException {
        StaticBid bid = new StaticBid();
        bid.setId(1);
        bid.setTime(System.currentTimeMillis());
        bid.setAmount(100);
        StaticUser user = new StaticUser();
        user.setId(11);
        user.setName("Lukas");
        bid.setUser(user);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        context.marshallEntity(bid, MediaType.APPLICATION_JSON_TYPE, baos);
        
        System.out.println(baos.toString());
    }
    
    @Test
    public void testMarshalBid()throws JAXBException {
        StaticAuction auction = new StaticAuction();
        auction.setDescription("Auction 1");
        auction.setImage("auction1.jpg");
        auction.setName("A1");
        auction.setStartPrice(100);
        
        StaticUser user = new StaticUser();
        user.setId(11);
        user.setName("user1");
        
        StaticBid bid = new StaticBid();
        bid.setAmount(110);
        bid.setTime(System.currentTimeMillis());
        bid.setAuction(auction);
        auction.getBids().add(bid);
        bid.setUser(user);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        context.marshallEntity(auction, MediaType.APPLICATION_JSON_TYPE, baos);
        
       // ByteArrayOutputStream baos = new ByteArrayOutputStream();
       // context.marshallEntity(bid, MediaType.APPLICATION_JSON_TYPE, baos);
        
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        StaticBid bid1 = (StaticBid)context.unmarshalEntity("StaticBid", MediaType.APPLICATION_JSON_TYPE, bais);
       // EntityManager em = context.getEmf().createEntityManager();
     //   bid1 = em.merge(bid1);
        StaticAuction auction1 = bid1.getAuction();
    }
}
