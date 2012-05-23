/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpars.test.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpars.test.model.StaticAddress;
import org.eclipse.persistence.jpars.test.model.StaticAuction;
import org.eclipse.persistence.jpars.test.model.StaticBid;
import org.eclipse.persistence.jpars.test.model.StaticUser;

public class StaticModelDatabasePopulator {

    
    public static int USER1_ID;
    public static int USER2_ID;
    public static int USER3_ID;
    
    public static int AUCTION1_ID;
    public static int AUCTION2_ID;
    public static int AUCTION3_ID;
    
    public static int BID1_ID;;
    public static int BID2_ID;
    public static int BID3_ID;
    
    public static int ADDRESS1_ID;
    
    public static void populateDB(EntityManagerFactory emf){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        StaticUser user1 = user1();
        em.persist(user1);
        StaticAddress address1 = address1();
        em.persist(address1);
        user1.setAddress(address1);
        StaticUser user2 = user2();
        em.persist(user2);
        StaticUser user3 = user3();
        em.persist(user3);
        StaticAuction auction1 = auction1();
        em.persist(auction1);
        StaticAuction auction2 = auction1();
        em.persist(auction2);
        StaticAuction auction3 = auction1();
        em.persist(auction3);
        
        StaticBid bid1 = new StaticBid();
        bid1.setBid(110);
        bid1.setTime(System.currentTimeMillis());
        bid1.setAuction(auction1);
        bid1.setUser(user1);
        em.persist(bid1);
        
        StaticBid bid2 = new StaticBid();
        bid2.setBid(111);
        bid2.setTime(System.currentTimeMillis());
        bid2.setAuction(auction1);
        bid2.setUser(user2);
        em.persist(bid2);
        
        StaticBid bid3 = new StaticBid();
        bid3.setBid(1100);
        bid3.setTime(System.currentTimeMillis());
        bid3.setAuction(auction2);
        bid3.setUser(user2);
        em.persist(bid3);
        
        em.getTransaction().commit();
        USER1_ID = user1.getId();
        USER2_ID = user2.getId();
        USER3_ID = user3.getId();
        
        AUCTION1_ID = auction1.getId();
        AUCTION2_ID = auction2.getId();
        AUCTION3_ID = auction3.getId();
        
        BID1_ID = bid1.getId();
        BID2_ID = bid2.getId();
        BID3_ID = bid3.getId();
        
        ADDRESS1_ID = address1.getId();
    }
    
    public static StaticUser user1(){
        StaticUser user = new StaticUser();
        user.setId(11);
        user.setName("user1");
        return user;
    }
    
    public static StaticUser user2(){
        StaticUser user = new StaticUser();
        user.setId(22);
        user.setName("user2");
        return user;
    }
    
    public static StaticUser user3(){
        StaticUser user = new StaticUser();
        user.setId(33);
        user.setName("user3");
        return user;
    }
    
    public static StaticAuction auction1(){
        StaticAuction auction = new StaticAuction();
        auction.setDescription("Auction 1");
        auction.setImage("auction1.jpg");
        auction.setName("A1");
        auction.setStartPrice(100);
        return auction;
    }
    
    public static StaticAuction auction2(){
        StaticAuction auction = new StaticAuction();
        auction.setDescription("Auction 2");
        auction.setImage("auction2.jpg");
        auction.setName("A2");
        auction.setStartPrice(1000);
        return auction;
    }
    
    public static StaticAuction auction3(){
        StaticAuction auction = new StaticAuction();
        auction.setDescription("Auction 3");
        auction.setImage("auction3.jpg");
        auction.setName("A3");
        auction.setStartPrice(1010);
        return auction;
    }
    
    public static StaticAddress address1(){
        StaticAddress address = new StaticAddress();
        address.setCity("Ottawa");
        address.setPostalCode("K1A1A1");
        address.setStreet("Rembrandt Rd.");
        address.setType("home");
        return address;
    }
    
}
