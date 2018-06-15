/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware/gonural - Initial implementation
//      Dmitry Kornilov - Moved to another package, upgrade to Jersey 2.x
package org.eclipse.persistence.jpars.test.server.noversion;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpars.test.BaseJparsTest;
import org.eclipse.persistence.jpars.test.model.auction.StaticAddress;
import org.eclipse.persistence.jpars.test.model.auction.StaticAuction;
import org.eclipse.persistence.jpars.test.model.auction.StaticBid;
import org.eclipse.persistence.jpars.test.model.auction.StaticUser;
import org.eclipse.persistence.jpars.test.model.multitenant.Account;
import org.eclipse.persistence.jpars.test.server.RestCallFailedException;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.eclipse.persistence.jpars.test.util.StaticModelDatabasePopulator;
import org.eclipse.persistence.jpars.test.util.UriBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Server CRUD tests.
 */
public class ServerCrudTest extends BaseJparsTest {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_auction-static", null);
        StaticModelDatabasePopulator.populateDB(emf);
    }

    @AfterClass
    public static void teardown() {
        StaticModelDatabasePopulator.cleanupDB(context.getEmf());
    }

    /**
     * Test read.
     */
    @Test
    public void testRead() throws Exception {
        StaticBid bid = RestUtils.restReadObject(context, StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        StaticBid bid2 = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong bid in DB.", bid.getAmount() == bid2.getAmount());
    }

    /**
     * Test read xml.
     */
    @Test
    public void testReadXML() throws Exception {
        StaticBid bid = RestUtils.restReadObject(context, StaticModelDatabasePopulator.BID1_ID, StaticBid.class, MediaType.APPLICATION_XML_TYPE);
        StaticBid bid2 = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong bid in DB.", bid.getAmount() == bid2.getAmount());
    }

    /**
     * Test read non-existent.
     */
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistent() throws Exception {
        RestUtils.restReadObject(context, 0, StaticBid.class);
    }

    /**
     * Test read non-existent type.
     */
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistentType() throws Exception {
        String uri = new UriBuilder(context).addEntity("NonExistent", 1).toString();
        RestUtils.doGet(context, uri, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test read non-existent pu.
     */
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistentPU() throws Exception {
        String uri = new UriBuilder(context).addPu("non-existent").addEntity("StaticBid", 1).toString();
        RestUtils.doGet(context, uri, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update.
     */
    @Test
    public void testUpdate() throws Exception {
        StaticBid bid = RestUtils.restReadObject(context, StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        bid.setAmount(120);
        bid = RestUtils.restUpdate(context, bid, StaticBid.class);
        assertTrue("Wrong bid retrieved.", bid.getAmount() == 120);
        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong bid retrieved in db.", bid.getAmount() == 120);
        assertTrue("No auction for Bid in db", bid.getAuction() != null);
        bid.setAmount(110);
        RestUtils.restUpdate(context, bid, StaticBid.class);
    }

    /**
     * Test create delete.
     */
    @Test
    public void testCreateDelete() throws Exception {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(100);
        user = RestUtils.restCreate(context, user,StaticUser.class);
        assertTrue("Wrong user retrieved.", user.getName().equals("Joe"));
        StaticUser dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("DB User not equal ", user.equals(dbUser));
        RestUtils.restDelete(context, user.getId(), StaticUser.class);
        dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("User was not deleted.", dbUser == null);
    }

    /**
     * Test create xml.
     */
    @Test
    public void testCreateXML() throws Exception {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(101);
        user = RestUtils.restCreate(context, user, StaticUser.class, MediaType.APPLICATION_XML_TYPE);
        assertTrue("Wrong user retrieved.", user.getName().equals("Joe"));
        StaticUser dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("DB User not equal ", user.equals(dbUser));
        RestUtils.restDelete(context, user.getId(), StaticUser.class);
        dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("User was not deleted.", dbUser == null);
    }

    /**
     * Test create sequenced.
     */
    @Test(expected = RestCallFailedException.class)
    public void testCreateSequenced() throws Exception {
        StaticAuction auction = new StaticAuction();
        auction.setName("Laptop");
        RestUtils.restCreate(context, auction, StaticAuction.class);
    }

    /**
     * Test create non-existent.
     */
    @Test(expected = RestCallFailedException.class)
    public void testCreateNonExistent() throws Exception {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(102);

        String userJson = RestUtils.marshal(context, user, MediaType.APPLICATION_JSON_TYPE);
        String uri = new UriBuilder(context).addEntity("NonExistent").toString();
        RestUtils.doPut(context, uri, Entity.entity(userJson, MediaType.APPLICATION_JSON_TYPE), MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test create non existent persistence unit.
     */
    @Test(expected = RestCallFailedException.class)
    public void testCreateNonExistentPersistenceUnit() throws Exception {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(103);

        String userJson = RestUtils.marshal(context, user, MediaType.APPLICATION_JSON_TYPE);
        String uri = new UriBuilder(context).addPu("not-existent").addEntity("StaticUser").toString();
        RestUtils.doPut(context, uri, Entity.entity(userJson, MediaType.APPLICATION_JSON_TYPE), MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test create garbage.
     */
    @Test
    public void testCreateGarbage() throws Exception {
        String uri = new UriBuilder(context).addEntity("StaticUser").toString();

        try {
            RestUtils.doPut(context, uri, Entity.entity("Garbage", MediaType.APPLICATION_JSON_TYPE), MediaType.APPLICATION_JSON_TYPE);
        } catch (RestCallFailedException ex) {
            // This is OK that exception is thrown. We just need to check status.
            assertTrue("Wrong exception garbage write: " + ex.getHttpStatus(), ex.getHttpStatus() == Response.Status.BAD_REQUEST.getStatusCode());
            return;
        }

        fail();
    }

    /**
     * Test update xml.
     */
    @Test
    public void testUpdateXML() throws Exception {
        StaticBid bid = RestUtils.restReadObject(context, StaticModelDatabasePopulator.BID1_ID, StaticBid.class, MediaType.APPLICATION_XML_TYPE);
        bid.setAmount(120);
        bid = RestUtils.restUpdate(context, bid, StaticBid.class, null, MediaType.APPLICATION_XML_TYPE, true);
        assertTrue("Wrong bid retrieved.", bid.getAmount() == 120);
        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong bid retrieved in db.", bid.getAmount() == 120);
        bid.setAmount(110);
        RestUtils.restUpdate(context, bid, StaticBid.class);
    }

    /**
     * Test post new entity.
     */
    @Test
    public void testPostNewEntity() throws Exception {
        StaticAuction auction = new StaticAuction();
        auction.setName("Computer");
        auction = RestUtils.restUpdate(context, auction, StaticAuction.class);
        assertTrue("Wrong Auction returned.", auction.getName().equals("Computer"));
        assertTrue("Auction not sequenced.", auction.getId() > 0);
        StaticAuction dbAuction = dbRead(auction.getId(), StaticAuction.class);
        assertTrue("Wrong user retrieved in db.", auction.getName().equals(dbAuction.getName()));
        RestUtils.restDelete(context, auction.getId(), StaticAuction.class);
    }

    /**
     * Test update non-existent.
     */
    @Test(expected = RestCallFailedException.class)
    public void testUpdateNonExistent() throws Exception {
        StaticUser user = new StaticUser();
        user.setName("Joe");

        String userJson = RestUtils.marshal(context, user, MediaType.APPLICATION_JSON_TYPE);
        String uri = new UriBuilder(context).addEntity("NonExistent").toString();
        RestUtils.doPost(context, uri, Entity.entity(userJson, MediaType.APPLICATION_JSON_TYPE), MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update non-existent persistence unit.
     */
    @Test(expected = RestCallFailedException.class)
    public void testUpdateNonExistentPersistenceUnit() throws Exception {
        StaticUser user = new StaticUser();
        user.setName("Joe");

        String userJson = RestUtils.marshal(context, user, MediaType.APPLICATION_JSON_TYPE);
        String uri = new UriBuilder(context).addPu("not-existent").addEntity("StaticUser").toString();
        RestUtils.doPost(context, uri, Entity.entity(userJson, MediaType.APPLICATION_JSON_TYPE), MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update garbage.
     */
    @Test
    public void testUpdateGarbage() throws Exception {
        String uri = new UriBuilder(context).addEntity("StaticUser").toString();

        try {
            RestUtils.doPost(context, uri, Entity.entity("Garbage", MediaType.APPLICATION_JSON_TYPE), MediaType.APPLICATION_JSON_TYPE);
        } catch (RestCallFailedException ex) {
            // This is OK that exception is thrown. We just need to check status.
            assertTrue("Wrong exception garbage write: " + ex.getHttpStatus(), ex.getHttpStatus() == Response.Status.BAD_REQUEST.getStatusCode());
            return;
        }

        fail();
    }

    /**
     * Test update relationship.
     */
    @Test
    public void testUpdateRelationship() throws Exception {
        StaticBid bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        StaticUser user = bid.getUser();
        StaticUser newUser = new StaticUser();
        newUser.setName("Mark");
        RestUtils.restUpdateRelationship(context, String.valueOf(StaticModelDatabasePopulator.BID1_ID), "user", newUser, StaticBid.class, MediaType.APPLICATION_JSON_TYPE);

        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong user.", bid.getUser().getName().equals("Mark"));
        newUser = bid.getUser();
        bid = RestUtils.restUpdateRelationship(context, String.valueOf(StaticModelDatabasePopulator.BID1_ID), "user", user, StaticBid.class, MediaType.APPLICATION_JSON_TYPE);

        StaticBid dbBid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong user.", dbBid.getUser().getName().equals(bid.getUser().getName()));

        dbDelete(newUser);
    }

    /**
     * Test delete non existent.
     */
    @Test
    public void testDeleteNonExistent() throws Exception {
        RestUtils.restDelete(context, 1000, StaticUser.class);
    }

    /**
     * Test delete non existent type.
     */
    @Test(expected = RestCallFailedException.class)
    public void testDeleteNonExistentType() throws Exception {
        String uri = new UriBuilder(context).addEntity("non-existent", 1000).toString();
        RestUtils.doDelete(context, uri, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test delete non existent persistence unit.
     */
    @Test(expected = RestCallFailedException.class)
    public void testDeleteNonExistentPersistenceUnit() throws Exception {
        String uri = new UriBuilder(context).addPu("non-existent").addEntity("StaticUser", 1000).toString();
        RestUtils.doDelete(context, uri, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test named query.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQuery() throws Exception {
        List<StaticUser> users = RestUtils.restNamedMultiResultQuery(context, "User.all", StaticUser.class, null, null);
        assertTrue("Incorrect Number of users found.", users.size() == 3);
    }

    /**
     * Test named query parameter.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQueryParameter() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        List<StaticUser> users = RestUtils.restNamedMultiResultQuery(context, "User.byId", StaticUser.class, parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 1);
        assertTrue("Wrong user returned", users.get(0).getId() == StaticModelDatabasePopulator.USER1_ID);
    }

    /**
     * Test named query parameters.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQueryParameters() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        parameters.put("name", "user2");
        List<StaticUser> users = RestUtils.restNamedMultiResultQuery(context, "User.byNameOrId", StaticUser.class, parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 2);
    }

    /**
     * Test named query wrong parameter.
     */
    @Test(expected = RestCallFailedException.class)
    public void testNamedQueryWrongParameter() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("wrong", StaticModelDatabasePopulator.USER1_ID);
        RestUtils.restNamedMultiResultQuery(context, "User.byId", StaticUser.class, parameters, null);
    }

    /**
     * Test named query wrong number of parameters.
     */
    @Test(expected = RestCallFailedException.class)
    public void testNamedQueryWrongNumberOfParameters() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        RestUtils.restNamedMultiResultQuery(context, "User.byNameOrId", StaticUser.class, parameters, null);
    }

    /**
     * Test named query no results.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQueryNoResults() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 0);
        List<StaticUser> users = RestUtils.restNamedMultiResultQuery(context, "User.byId", StaticUser.class, parameters, null, MediaType.APPLICATION_XML_TYPE);
        assertTrue("Incorrect Number of users found.", users.size() == 0);
    }

    /**
     * Test non-existent named query.
     */
    @Test(expected = RestCallFailedException.class)
    public void testNonExistentNamedQuery() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        RestUtils.restNamedMultiResultQuery(context, "User.nonExistent", StaticUser.class, parameters, null);
    }

    /**
     * Test non-existent persistence unit named query.
     */
    @Test(expected = RestCallFailedException.class)
    public void testNonExistentPersistenceUnitNamedQuery() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);

        String uri = new UriBuilder(context).addPu("nonExistent").addQuery("User.all").addParameters(parameters).toString();
        RestUtils.doGet(context, uri, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test named query hint.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQueryHint() throws Exception {
        // load the cache
        Map<String, String> hints = new HashMap<>();
        hints.put(QueryHints.REFRESH, "true");
        List<StaticUser> users = RestUtils.restNamedMultiResultQuery(context, "User.all", StaticUser.class, null, hints);
        assertTrue("Incorrect Number of users found.", users.size() == 3);
        StaticUser user1 = users.get(0);
        String oldName = user1.getName();
        user1.setName("Changed");
        dbUpdate(user1);
        users = RestUtils.restNamedMultiResultQuery(context, "User.all", StaticUser.class, null, hints);
        for (StaticUser user : users) {
            if (user.getId() == user1.getId()) {
                assertTrue("User was not refreshed", user.getName().equals(user1.getName()));
            }
        }
        user1 = dbRead(user1.getId(), StaticUser.class);
        user1.setName(oldName);
        dbUpdate(user1);
        // refresh cache
        hints = new HashMap<>();
        hints.put(QueryHints.REFRESH, "true");
        RestUtils.restNamedMultiResultQuery(context, "User.all", StaticUser.class, null, hints);
    }

    /**
     * Test named query parameter hint.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQueryParameterHint() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        // load the cache
        List<StaticUser> users = RestUtils.restNamedMultiResultQuery(context, "User.byId", StaticUser.class, parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 1);
        assertTrue("Wrong user returned", users.get(0).getId() == StaticModelDatabasePopulator.USER1_ID);
        StaticUser user1 = users.get(0);
        String oldName = user1.getName();
        user1.setName("Changed2");
        dbUpdate(user1);
        Map<String, String> hints = new HashMap<>();
        hints.put(QueryHints.REFRESH, "true");
        users = RestUtils.restNamedMultiResultQuery(context, "User.byId", StaticUser.class, parameters, hints);
        assertTrue("User was not refreshed", users.get(0).getName().equals(user1.getName()));
        user1 = dbRead(user1.getId(), StaticUser.class);
        user1.setName(oldName);
        dbUpdate(user1);

        // refresh cache
        hints = new HashMap<>();
        hints.put(QueryHints.REFRESH, "true");
        RestUtils.restNamedMultiResultQuery(context, "User.all", StaticUser.class, null, hints);
    }

    /**
     * Test named query single result.
     */
    @Test
    public void testNamedQuerySingleResult() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        StaticUser user = (StaticUser) RestUtils.restNamedSingleResultQuery(context, "User.byId", "StaticUser", parameters, null);
        assertTrue("user was not returned", user != null);
        assertTrue("incorrect user returned", user.getName().equals("user1"));
    }

    /**
     * Test named query single result no result.
     */
    @Test(expected = RestCallFailedException.class)
    public void testNamedQuerySingleResultNoResult() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 0);
        StaticUser user = (StaticUser) RestUtils.restNamedSingleResultQuery(context, "User.byId", "StaticUser", parameters, null);
        assertTrue("user should not have been returned", user == null);
    }

    /**
     * Test update query.
     */
    @Test
    public void testUpdateQuery() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        parameters.put("name", "newName");

        String result = RestUtils.restUpdateQuery(context, "User.updateName", parameters, null, MediaType.APPLICATION_JSON_TYPE);
        assertTrue(result.contains("{\"value\":1}"));
        StaticUser user1 = dbRead(StaticModelDatabasePopulator.USER1_ID, StaticUser.class);
        assertTrue(user1.getName().equals("newName"));
        user1 = dbRead(user1.getId(), StaticUser.class);
        user1.setName(StaticModelDatabasePopulator.user1().getName());
        dbUpdate(user1);
    }

    /**
     * Test multitenant.
     */
    @Test(expected = RestCallFailedException.class)
    public void testMultitenant() throws Exception {
        Account account = new Account();
        account.setAccountNumber("AAA1");
        Map<String, String> tenantId = new HashMap<>();
        tenantId.put("tenant.id", "AcctHolder1");
        account = RestUtils.restUpdate(context, account, Account.class, tenantId, MediaType.APPLICATION_JSON_TYPE, true);

        assertTrue("account not created.", account != null);

        account = RestUtils.restReadObject(context, account.getId(), Account.class, MediaType.APPLICATION_JSON_TYPE, tenantId);

        assertTrue("account not read.", account != null);
        assertTrue("account not completely read.", account.getAccountNumber().equals("AAA1"));

        Map<String, String> tenantId2 = new HashMap<>();
        tenantId2.put("tenant.id", "AcctHolder2");
        try {
            RestUtils.restReadObject(context, account.getId(), Account.class, MediaType.APPLICATION_JSON_TYPE, tenantId2);
        } finally {
            RestUtils.restDelete(context, account.getId(), Account.class, tenantId, MediaType.APPLICATION_JSON_TYPE);
        }
    }

    /**
     * Test create object graph put.
     */
    @Test
    public void testCreateObjectGraphPut() throws Exception {
        // Create a bid without auction and user first
        StaticBid bid = new StaticBid();
        bid.setId(777);
        bid.setAmount(510);
        bid.setTime(System.currentTimeMillis());
        bid = RestUtils.restCreate(context, bid, StaticBid.class);

        // Create an auction
        StaticAuction auction = new StaticAuction();
        auction.setId(13012);
        auction.setName("Lego");
        auction.setStartPrice(500);
        auction.setImage("Starwars.jpg");
        auction.setEndPrice(1000);
        auction.setDescription("Lego auction");
        auction = RestUtils.restCreate(context, auction, StaticAuction.class);

        // Create address
        StaticAddress address = new StaticAddress();
        address.setCity("Ottawa");
        address.setId(123456);
        address.setStreet("Main Street");
        address.setPostalCode("K1P 1A4");
        address.setType("Business");
        address = RestUtils.restCreate(context, address, StaticAddress.class);

        // Create a user
        StaticUser user = new StaticUser();
        user.setId(466);
        user.setName("LegoLover");
        user = RestUtils.restCreate(context, user, StaticUser.class);

        // Update user with address
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(user.getId()), StaticUser.class,
                "address", address, MediaType.APPLICATION_JSON_TYPE, "user", true);

        // read user again, because we will update the bid with user
        user = RestUtils.restReadObject(context, String.valueOf(user.getId()), StaticUser.class, MediaType.APPLICATION_JSON_TYPE);

        // Update bid with the auction
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(777), StaticBid.class,
                "auction", auction, MediaType.APPLICATION_JSON_TYPE, "bids", true);

        // update bid with the user
        String bidJsonResponse = RestUtils.restUpdateBidirectionalRelationship(context,
                String.valueOf(777), StaticBid.class, "user", user, MediaType.APPLICATION_JSON_TYPE, null, true);

        String expectedAuctionLink = getServerURI() + "/entity/StaticBid/777/auction";
        String expectedUserLink = getServerURI() + "/entity/StaticBid/777/user";

        assertTrue(bidJsonResponse.toUpperCase().contains(expectedAuctionLink.toUpperCase()));

        assertTrue(bidJsonResponse.toUpperCase().contains(expectedUserLink.toUpperCase()));

        // Read auction, using the link provided in the newly created bid above
        StaticAuction auctionByLink = RestUtils.restReadByHref(context, expectedAuctionLink, StaticAuction.class);

        // Read user, using the link provided in the newly created bid above
        StaticUser userByLink = RestUtils.restReadByHref(context, expectedUserLink, StaticUser.class);

        assertTrue("Wrong user, could not update bid with a user.", userByLink.getName().equals(user.getName()));
        assertTrue("Wrong auction, could not update bid with an auction.", auctionByLink.getName().equals(auction.getName()));

        dbDelete(bid);
        dbDelete(user);
        dbDelete(address);
        dbDelete(auction);
    }

    /**
     * Test create object graph post.
     */
    @Test
    public void testCreateObjectGraphPost() throws Exception {
        // Create a bid without auction and user first (no id)
        StaticBid bid = new StaticBid();
        bid.setAmount(810);
        bid.setTime(System.currentTimeMillis());
        bid = RestUtils.restUpdate(context, bid, StaticBid.class, false);

        // Create an auction (no id)
        StaticAuction auction = new StaticAuction();
        auction.setName("PlayStation");
        auction.setStartPrice(900);
        auction.setImage("Starwars2.jpg");
        auction.setEndPrice(1000);
        auction.setDescription("PlayStation auction");
        auction = RestUtils.restUpdate(context, auction, StaticAuction.class, false);

        // Create a user (no id)
        StaticUser user = new StaticUser();
        user.setName("LegoLover");
        user = RestUtils.restUpdate(context, user, StaticUser.class, false);

        // Update bid with the auction
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(bid.getId()), StaticBid.class, "auction",
                auction, MediaType.APPLICATION_JSON_TYPE, "bids", true);

        // update bid with the user
        String bidJsonResponse = RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(bid.getId()),
                StaticBid.class, "user", user, MediaType.APPLICATION_JSON_TYPE, null, true);

        String expectedAuctionLink = getServerURI() + "/entity/StaticBid/" + bid.getId() + "/auction";
        String expectedUserLink = getServerURI() + "/entity/StaticBid/" + bid.getId() + "/user";

        assertTrue(bidJsonResponse.toUpperCase().contains(expectedAuctionLink.toUpperCase()));
        assertTrue(bidJsonResponse.toUpperCase().contains(expectedUserLink.toUpperCase()));

        // Read auction, using the link provided in the newly created bid above
        StaticAuction auctionByLink = RestUtils.restReadByHref(context, expectedAuctionLink, StaticAuction.class);

        // Read user, using the link provided in the newly created bid above
        StaticUser userByLink = RestUtils.restReadByHref(context, expectedUserLink, StaticUser.class);

        assertTrue("Wrong user, could not update bid with a user.", userByLink.getName().equals(user.getName()));
        assertTrue("Wrong auction, could not update bid with an auction.", auctionByLink.getName().equals(auction.getName()));

        dbDelete(bid);
        dbDelete(user);
        dbDelete(auction);
    }

    /**
     * Test read composite pk.
     */
    @Test
    public void testReadCompositePK() throws Exception {
        StaticAddress address = new StaticAddress();
        address.setId(10012090);
        address.setType("home");
        address.setCity("Ottawa");
        dbCreate(address);

        String id = "10012090+home";
        StaticAddress addr = RestUtils.restReadObject(context, id, StaticAddress.class);

        assertTrue("Wrong address", addr.getId() == 10012090);

        dbDelete(address);
    }

    /**
     * Test read non existing composite pk.
     */
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistingCompositePK() throws Exception {
        String id = "home+10012090";
        RestUtils.restRead(context, id, StaticAddress.class);
    }

    /**
     * Test get contexts xml.
     */
    @Test
    public void testGetContextsXML() throws Exception {
        String uri = new UriBuilder(context).addPu(null).toString();
        Response response = RestUtils.doGet(context, uri, MediaType.APPLICATION_XML_TYPE);
        String result = response.readEntity(String.class);

        assertTrue(result != null);
        assertTrue(result.contains(RestUtils.getServerURI(context.getVersion()) + "jpars_auction/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI(context.getVersion()) + "jpars_auction-static-local/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI(context.getVersion()) + "jpars_auction-static/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI(context.getVersion()) + "jpars_employee-static/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI(context.getVersion()) + "jpars_phonebook/metadata"));
    }

    /**
     * Test get contexts json.
     */
    @Test
    public void testGetContextsJSON() throws Exception {
        String uri = new UriBuilder(context).addPu(null).toString();
        Response response = RestUtils.doGet(context, uri, MediaType.APPLICATION_JSON_TYPE);
        String result = response.readEntity(String.class);

        assertTrue(result != null);
        assertTrue(result.contains(RestUtils.getServerURI(context.getVersion()) + "jpars_auction/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI(context.getVersion()) + "jpars_auction-static-local/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI(context.getVersion()) + "jpars_auction-static/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI(context.getVersion()) + "jpars_employee-static/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI(context.getVersion()) + "jpars_phonebook/metadata"));
    }

    /**
     * Test get types xml.
     */
    @Test
    public void testGetTypesXML() throws Exception {
        String uri = new UriBuilder(context).toString() + "/metadata";
        Response response = RestUtils.doGet(context, uri, MediaType.APPLICATION_XML_TYPE);
        String result = response.readEntity(String.class);

        assertTrue(result != null);
        assertTrue(result.contains("<persistenceUnit><persistenceUnitName>jpars_auction-static"));
        assertTrue(result.contains("<types>"));
        assertTrue(result.contains("jpars_auction-static/metadata/entity/StaticAddress\" method=\"application/xml\" rel=\"StaticAddress\""));
        assertTrue(result.contains("jpars_auction-static/metadata/entity/StaticBid\" method=\"application/xml\" rel=\"StaticBid\""));
        assertTrue(result.contains("jpars_auction-static/metadata/entity/StaticUser\" method=\"application/xml\" rel=\"StaticUser\""));
        assertTrue(result.contains("jpars_auction-static/metadata/entity/Account\" method=\"application/xml\" rel=\"Account\""));
        assertTrue(result.contains("jpars_auction-static/metadata/entity/StaticAuction\" method=\"application/xml\" rel=\"StaticAuction\""));
    }

    /**
     * Test get types json.
     */
    @Test
    public void testGetTypesJSON() throws Exception {
        String uri = new UriBuilder(context).toString() + "/metadata";
        Response response = RestUtils.doGet(context, uri, MediaType.APPLICATION_JSON_TYPE);
        String result = response.readEntity(String.class);

        assertTrue(result != null);
        assertTrue(result.contains("{\"persistenceUnitName\":\"jpars_auction-static\",\"types\":[{\"_link\":{\"href\":\""));
        assertTrue(result.contains("jpars_auction-static/metadata/entity/StaticAddress\",\"method\":\"application/json\",\"rel\":\"StaticAddress\""));
        assertTrue(result.contains("jpars_auction-static/metadata/entity/StaticBid\",\"method\":\"application/json\",\"rel\":\"StaticBid\""));
        assertTrue(result.contains("jpars_auction-static/metadata/entity/StaticUser\",\"method\":\"application/json\",\"rel\":\"StaticUser\""));
        assertTrue(result.contains("jpars_auction-static/metadata/entity/Account\",\"method\":\"application/json\",\"rel\":\"Account\""));
        assertTrue(result.contains("jpars_auction-static/metadata/entity/StaticAuction\",\"method\":\"application/json\",\"rel\":\"StaticAuction\""));
    }

    /**
     * Test remove relationship non collection.
     */
    @Test
    public void testRemoveRelationshipNonCollection() throws Exception {
        StaticBid bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        StaticUser origUser = bid.getUser();

        StaticUser newUser = new StaticUser();
        newUser.setName("Mark");

        // add a user to bid
        bid = RestUtils.restUpdateRelationship(context, String.valueOf(StaticModelDatabasePopulator.BID1_ID), "user", newUser, StaticBid.class, MediaType.APPLICATION_JSON_TYPE);
        assertTrue("Wrong user.", bid.getUser().getName().equals("Mark"));

        // remove relationship between bid and the new user
        String userRemoved = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(bid.getId()), StaticBid.class, "user", MediaType.APPLICATION_JSON_TYPE, null, null);
        assertTrue(userRemoved != null);
        dbDelete(newUser);

        // Put the original user back
        bid = RestUtils.restUpdateRelationship(context, String.valueOf(StaticModelDatabasePopulator.BID1_ID), "user", origUser, StaticBid.class, MediaType.APPLICATION_JSON_TYPE);
        assertTrue("Wrong user.", bid.getUser().getName().equals("user1"));
    }

    /**
     * Test create employee with phone numbers non idempotent.
     */
    @Test(expected = RestCallFailedException.class)
    public void testCreateAuctionNonIdempotent() throws Exception {
        String auction = RestUtils.getJSONMessage("auction-bidsByValueNoId.json");
        // The bid contained by the auction object has generated id field, and create is idempotent.
        // So, create operation on auction with bid list should fail.
        RestUtils.restCreateWithSequence(context, auction, StaticAuction.class, MediaType.APPLICATION_JSON_TYPE);
    }

    protected static void dbCreate(Object object) {
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.persist(object);
        em.getTransaction().commit();
    }

    protected static <T> T dbRead(Object id, Class<T> resultClass) {
        context.getEmf().getCache().evictAll();
        EntityManager em = context.getEmf().createEntityManager();
        return em.find(resultClass, id);
    }

    protected static void dbUpdate(Object object) {
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.merge(object);
        em.getTransaction().commit();
    }

    protected static void dbDelete(Object object) {
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        Object merged = em.merge(object);
        em.remove(merged);
        em.getTransaction().commit();
    }
}
