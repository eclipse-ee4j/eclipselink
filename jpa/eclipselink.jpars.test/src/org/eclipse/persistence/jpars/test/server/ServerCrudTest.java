/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.server;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpars.test.model.auction.StaticAddress;
import org.eclipse.persistence.jpars.test.model.auction.StaticAuction;
import org.eclipse.persistence.jpars.test.model.auction.StaticBid;
import org.eclipse.persistence.jpars.test.model.auction.StaticUser;
import org.eclipse.persistence.jpars.test.model.multitenant.Account;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.eclipse.persistence.jpars.test.util.StaticModelDatabasePopulator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

public class ServerCrudTest {
    private static final String DEFAULT_PU = "jpars_auction-static";
    private static Client client = null;
    private static PersistenceContext context = null;

    /**
     * Setup.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @BeforeClass
    public static void setup() throws URISyntaxException {
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties);
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));

        PersistenceFactoryBase factory = new PersistenceFactoryBase();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(DEFAULT_PU, properties);
        context = factory.bootstrapPersistenceContext(DEFAULT_PU, emf, RestUtils.getServerURI(), null, false);

        StaticModelDatabasePopulator.populateDB(emf);
        client = Client.create();
    }

    /**
     * Teardown.
     */
    @AfterClass
    public static void teardown() {
        StaticModelDatabasePopulator.cleanupDB(context.getEmf());
    }

    /**
     * Test read.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testRead() throws RestCallFailedException, URISyntaxException {
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class);
        StaticBid bid2 = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong bid in DB.", bid.getAmount() == bid2.getAmount());
    }

    /**
     * Test read xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testReadXML() throws RestCallFailedException, URISyntaxException {
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE);
        StaticBid bid2 = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong bid in DB.", bid.getAmount() == bid2.getAmount());
    }

    /**
     * Test read non existant.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistant() throws RestCallFailedException, URISyntaxException {
        restRead(0, "StaticBid", StaticBid.class);
    }

    /**
     * Test read non existant type.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistantType() throws RestCallFailedException, URISyntaxException {
        restRead(1, "NonExistant", StaticBid.class);
    }

    /**
     * Test read non existant pu.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistantPU() throws RestCallFailedException, URISyntaxException {
        restRead(1, "StaticBid", StaticBid.class, "non-existant", null, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testUpdate() throws RestCallFailedException, URISyntaxException {
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class);
        bid.setAmount(120);
        bid = restUpdate(bid, "StaticBid", StaticBid.class, true);
        assertTrue("Wrong bid retrieved.", bid.getAmount() == 120);
        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong bid retrieved in db.", bid.getAmount() == 120);
        assertTrue("No auction for Bid in db", bid.getAuction() != null);
        bid.setAmount(110);
        bid = restUpdate(bid, "StaticBid", StaticBid.class, true);
    }

    /**
     * Test create delete.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testCreateDelete() throws RestCallFailedException, URISyntaxException {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(100);
        user = restCreate(user, "StaticUser", StaticUser.class);
        assertTrue("Wrong user retrieved.", user.getName().equals("Joe"));
        StaticUser dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("DB User not equal ", user.equals(dbUser));
        restDelete(user.getId(), "StaticUser", StaticUser.class);
        dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("User was not deleted.", dbUser == null);
    }

    /**
     * Test create xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testCreateXML() throws RestCallFailedException, URISyntaxException {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(101);
        user = restCreate(user, "StaticUser", StaticUser.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
        assertTrue("Wrong user retrieved.", user.getName().equals("Joe"));
        StaticUser dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("DB User not equal ", user.equals(dbUser));
        restDelete(user.getId(), "StaticUser", StaticUser.class);
        dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("User was not deleted.", dbUser == null);
    }

    /**
     * Test create sequenced.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testCreateSequenced() throws RestCallFailedException, URISyntaxException {
        StaticAuction auction = new StaticAuction();
        auction.setName("Laptop");
        auction = restCreate(auction, "StaticAuction", StaticAuction.class);
    }

    /**
     * Test create non existant.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testCreateNonExistant() throws RestCallFailedException, URISyntaxException {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(102);
        user = restCreate(user, "NonExistant", StaticUser.class);
    }

    /**
     * Test create non existant persistence unit.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testCreateNonExistantPersistenceUnit() throws RestCallFailedException, URISyntaxException {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(103);
        user = restCreate(user, "StaticUser", StaticUser.class, "non-existant", null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test create garbage.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the uRI syntax exception
     */

    @Test
    public void testCreateGarbage() throws IOException, URISyntaxException {
        WebResource webResource = client.resource(RestUtils.getServerURI() + DEFAULT_PU + "/entity/" + "StaticUser");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] b = "Garbage".getBytes();
        os.write(b);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        assertTrue("Wrong exception garbage write. " + status, status.equals(Status.BAD_REQUEST));
    }

    /**
     * Test update xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testUpdateXML() throws RestCallFailedException, URISyntaxException {
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE);
        bid.setAmount(120);
        bid = restUpdate(bid, "StaticBid", StaticBid.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE, true);
        assertTrue("Wrong bid retrieved.", bid.getAmount() == 120);
        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong bid retrieved in db.", bid.getAmount() == 120);
        bid.setAmount(110);
        bid = restUpdate(bid, "StaticBid", StaticBid.class, true);
    }

    /**
     * Test post new entity.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testPostNewEntity() throws RestCallFailedException, URISyntaxException {
        StaticAuction auction = new StaticAuction();
        auction.setName("Computer");
        auction = restUpdate(auction, "StaticAuction", StaticAuction.class, true);
        assertTrue("Wrong Auction returned.", auction.getName().equals("Computer"));
        assertTrue("Auction not sequenced.", auction.getId() > 0);
        StaticAuction dbAuction = dbRead(auction.getId(), StaticAuction.class);
        assertTrue("Wrong user retrieved in db.", auction.getName().equals(dbAuction.getName()));
        restDelete(auction.getId(), "StaticAuction", StaticAuction.class);
    }

    /**
     * Test update non existant.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testUpdateNonExistant() throws RestCallFailedException, URISyntaxException {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user = restUpdate(user, "NonExistant", StaticUser.class, true);
    }

    /**
     * Test update non existant persistence unit.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testUpdateNonExistantPersistenceUnit() throws RestCallFailedException, URISyntaxException {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user = restUpdate(user, "StaticUser", StaticUser.class, "non-existant", null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE, true);
    }

    /**
     * Test update garbage.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testUpdateGarbage() throws IOException, URISyntaxException {
        WebResource webResource = client.resource(RestUtils.getServerURI() + DEFAULT_PU + "/entity/" + "StaticUser");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] b = "Garbage".getBytes();
        os.write(b);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        assertTrue("Wrong exception garbage write. " + status, status.equals(Status.BAD_REQUEST));
    }

    /**
     * Test update relationship.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testUpdateRelationship() throws RestCallFailedException, URISyntaxException {
        StaticBid bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        StaticUser user = bid.getUser();
        StaticUser newUser = new StaticUser();
        newUser.setName("Mark");
        bid = restUpdateRelationship(String.valueOf(StaticModelDatabasePopulator.BID1_ID), "StaticBid", "user", newUser, StaticBid.class, "jpars_auction-static", MediaType.APPLICATION_JSON_TYPE,
                MediaType.APPLICATION_JSON_TYPE);

        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong user.", bid.getUser().getName().equals("Mark"));
        newUser = bid.getUser();
        bid = restUpdateRelationship(String.valueOf(StaticModelDatabasePopulator.BID1_ID), "StaticBid", "user", user, StaticBid.class, "jpars_auction-static", MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);

        StaticBid dbBid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong user.", dbBid.getUser().getName().equals(bid.getUser().getName()));

        dbDelete(newUser);
    }

    /**
     * Test delete non existant.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testDeleteNonExistant() throws RestCallFailedException, URISyntaxException {
        restDelete(1000, "StaticUser", StaticUser.class);
    }

    /**
     * Test delete non existant type.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testDeleteNonExistantType() throws RestCallFailedException, URISyntaxException {
        restDelete(1000, "NonExistant", StaticUser.class);
    }

    /**
     * Test delete non existant persistence unit.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testDeleteNonExistantPersistenceUnit() throws RestCallFailedException, URISyntaxException {
        restDelete(1000, "StaticUser", StaticUser.class, "non-existant", null);
    }

    /**
     * Test named query.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQuery() throws URISyntaxException {
        List<StaticUser> users = (List<StaticUser>) restNamedQuery("User.all", "StaticUser", null, null);
        assertTrue("Incorrect Number of users found.", users.size() == 3);
    }

    /**
     * Test named query parameter.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQueryParameter() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        List<StaticUser> users = (List<StaticUser>) restNamedQuery("User.byId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 1);
        assertTrue("Wrong user returned", users.get(0).getId() == StaticModelDatabasePopulator.USER1_ID);
    }

    /**
     * Test named query parameters.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQueryParameters() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        parameters.put("name", "user2");
        List<StaticUser> users = (List<StaticUser>) restNamedQuery("User.byNameOrId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 2);
    }

    /**
     * Test named query wrong parameter.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testNamedQueryWrongParameter() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("wrong", StaticModelDatabasePopulator.USER1_ID);
        restNamedQuery("User.byId", "StaticUser", parameters, null);
    }

    /**
     * Test named query wrong number of parameters.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testNamedQueryWrongNumberOfParameters() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        restNamedQuery("User.byNameOrId", "StaticUser", parameters, null);
    }

    /**
     * Test named query no results.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQueryNoResults() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", 0);
        List<StaticUser> users = (List<StaticUser>) restNamedQuery("User.byId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 0);
    }

    /**
     * Test non existant named query.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testNonExistantNamedQuery() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        restNamedQuery("User.nonExistant", "StaticUser", parameters, null);
    }

    /**
     * Test non existant persistence unit named query.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testNonExistantPersistenceUnitNamedQuery() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        restNamedQuery("User.all", "StatisUser", "nonExistant", parameters, null, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test named query hint.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQueryHint() throws URISyntaxException {
        // load the cache
        Map<String, String> hints = new HashMap<String, String>();
        hints.put(QueryHints.REFRESH, "true");
        List<StaticUser> users = (List<StaticUser>) restNamedQuery("User.all", "StaticUser", null, hints);
        assertTrue("Incorrect Number of users found.", users.size() == 3);
        StaticUser user1 = users.get(0);
        String oldName = user1.getName();
        user1.setName("Changed");
        dbUpdate(user1);
        users = (List<StaticUser>) restNamedQuery("User.all", "StaticUser", null, hints);
        for (StaticUser user : users) {
            if (user.getId() == user1.getId()) {
                assertTrue("User was not refreshed", user.getName().equals(user1.getName()));
            }
        }
        user1 = dbRead(user1.getId(), StaticUser.class);
        user1.setName(oldName);
        dbUpdate(user1);
        // refresh cache
        hints = new HashMap<String, String>();
        hints.put(QueryHints.REFRESH, "true");
        users = (List<StaticUser>) restNamedQuery("User.all", "StaticUser", null, hints);
    }

    /**
     * Test named query parameter hint.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNamedQueryParameterHint() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        // load the cache
        List<StaticUser> users = (List<StaticUser>) restNamedQuery("User.byId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 1);
        assertTrue("Wrong user returned", users.get(0).getId() == StaticModelDatabasePopulator.USER1_ID);
        StaticUser user1 = users.get(0);
        String oldName = user1.getName();
        user1.setName("Changed2");
        dbUpdate(user1);
        Map<String, String> hints = new HashMap<String, String>();
        hints.put(QueryHints.REFRESH, "true");
        users = (List<StaticUser>) restNamedQuery("User.byId", "StaticUser", parameters, hints);
        assertTrue("User was not refreshed", users.get(0).getName().equals(user1.getName()));
        user1 = dbRead(user1.getId(), StaticUser.class);
        user1.setName(oldName);
        dbUpdate(user1);

        // refresh cache
        hints = new HashMap<String, String>();
        hints.put(QueryHints.REFRESH, "true");
        users = (List<StaticUser>) restNamedQuery("User.all", "StaticUser", null, hints);
    }

    /**
     * Test named query single result.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testNamedQuerySingleResult() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        StaticUser user = (StaticUser) restNamedSingleResultQuery("User.byId", "StaticUser", DEFAULT_PU, parameters, null, MediaType.APPLICATION_JSON_TYPE);
        assertTrue("user was not returned", user != null);
        assertTrue("incorrect user returned", user.getName().equals("user1"));
    }

    /**
     * Test named query single result no result.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testNamedQuerySingleResultNoResult() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", 0);
        StaticUser user = (StaticUser) restNamedSingleResultQuery("User.byId", "StaticUser", DEFAULT_PU, parameters, null, MediaType.APPLICATION_JSON_TYPE);
        assertTrue("user should not have been returned", user == null);
    }

    /**
     * Test update query.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testUpdateQuery() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        parameters.put("name", "newName");

        String result = RestUtils.restUpdateQuery(context, "User.updateName", "StaticUser", parameters, null, MediaType.APPLICATION_JSON_TYPE);
        assertTrue(result.contains("{\"value\":1}"));
        StaticUser user1 = dbRead(StaticModelDatabasePopulator.USER1_ID, StaticUser.class);
        assertTrue(user1.getName().equals("newName"));
        user1 = dbRead(user1.getId(), StaticUser.class);
        user1.setName(StaticModelDatabasePopulator.user1().getName());
        dbUpdate(user1);
    }

    /**
     * Test multitenant.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testMultitenant() throws RestCallFailedException, URISyntaxException {
        Account account = new Account();
        account.setAccoutNumber("AAA1");
        Map<String, String> tenantId = new HashMap<String, String>();
        tenantId.put("tenant.id", "AcctHolder1");
        account = restUpdate(account, "Account", Account.class, DEFAULT_PU, tenantId, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, true);

        assertTrue("account not created.", account != null);

        account = restRead(account.getId(), "Account", Account.class, DEFAULT_PU, tenantId, MediaType.APPLICATION_JSON_TYPE);

        assertTrue("account not read.", account != null);
        assertTrue("account not completely read.", account.getAccoutNumber().equals("AAA1"));

        Map<String, String> tenantId2 = new HashMap<String, String>();
        tenantId2.put("tenant.id", "AcctHolder2");
        try {
            restRead(account.getId(), "Account", Account.class, DEFAULT_PU, tenantId2, MediaType.APPLICATION_JSON_TYPE);
        } finally {
            restDelete(account.getId(), "Account", Account.class, DEFAULT_PU, tenantId);
        }
    }

    /**
     * Test create object graph put.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws JAXBException the jAXB exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testCreateObjectGraphPut() throws RestCallFailedException, JAXBException, URISyntaxException {
        // Create a bid without auction and user first
        StaticBid bid = new StaticBid();
        bid.setId(777);
        bid.setAmount(510);
        bid.setTime(System.currentTimeMillis());
        bid = restCreate(bid, "StaticBid", StaticBid.class);

        // Create an auction
        StaticAuction auction = new StaticAuction();
        auction.setId(13012);
        auction.setName("Lego");
        auction.setStartPrice(500);
        auction.setImage("Starwars.jpg");
        auction.setEndPrice(1000);
        auction.setDescription("Lego auction");
        auction = restCreate(auction, "StaticAuction", StaticAuction.class);

        // Create address 
        StaticAddress address = new StaticAddress();
        address.setCity("Ottawa");
        address.setId(123456);
        address.setStreet("Main Street");
        address.setPostalCode("K1P 1A4");
        address.setType("Business");
        address = restCreate(address, "StaticAddress", StaticAddress.class);

        // Create a user
        StaticUser user = new StaticUser();
        user.setId(466);
        user.setName("LegoLover");
        user = restCreate(user, "StaticUser", StaticUser.class);

        // Update user with address
        restUpdateBidirectionalRelationship(String.valueOf(user.getId()), "StaticUser", "address", address, DEFAULT_PU,
                MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, "user", true);

        // read user again, because we will update the bid with user
        user = restRead(String.valueOf(user.getId()), "StaticUser", StaticUser.class, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE);

        // Update bid with the auction
        restUpdateBidirectionalRelationship(String.valueOf(777), "StaticBid",
                "auction", auction, DEFAULT_PU, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, "bids", true);

        // update bid with the user
        String bidJsonResponse = restUpdateBidirectionalRelationship(
                String.valueOf(777), "StaticBid", "user", user, DEFAULT_PU, MediaType.APPLICATION_JSON_TYPE,
                MediaType.APPLICATION_JSON_TYPE, null, true);

        String expectedAuctionLink = RestUtils.getServerURI() + DEFAULT_PU + "/entity/StaticBid/777/auction";
        String expectedUserLink = RestUtils.getServerURI() + DEFAULT_PU + "/entity/StaticBid/777/user";

        assertTrue(bidJsonResponse.toUpperCase().indexOf(expectedAuctionLink.toUpperCase()) != -1);

        assertTrue(bidJsonResponse.toUpperCase().indexOf(expectedUserLink.toUpperCase()) != -1);

        // Read auction, using the link provided in the newly created bid above
        StaticAuction auctionByLink = restReadByHref(expectedAuctionLink, "StaticAuction", MediaType.APPLICATION_JSON_TYPE);

        // Read user, using the link provided in the newly created bid above
        StaticUser userByLink = restReadByHref(expectedUserLink, "StaticUser", MediaType.APPLICATION_JSON_TYPE);

        assertTrue("Wrong user, could not update bid with a user.", userByLink.getName().equals(user.getName()));
        assertTrue("Wrong auction, could not update bid with an auction.", auctionByLink.getName().equals(auction.getName()));

        dbDelete(bid);
        dbDelete(user);
        dbDelete(address);
        dbDelete(auction);
    }

    /**
     * Test create object graph post.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws JAXBException the jAXB exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testCreateObjectGraphPost() throws RestCallFailedException, JAXBException, URISyntaxException {
        // Create a bid without auction and user first (no id)
        StaticBid bid = new StaticBid();
        bid.setAmount(810);
        bid.setTime(System.currentTimeMillis());
        bid = restUpdate(bid, "StaticBid", StaticBid.class, false);

        // Create an auction (no id)
        StaticAuction auction = new StaticAuction();
        auction.setName("PlayStation");
        auction.setStartPrice(900);
        auction.setImage("Starwars2.jpg");
        auction.setEndPrice(1000);
        auction.setDescription("PlayStation auction");
        auction = restUpdate(auction, "StaticAuction", StaticAuction.class, false);

        // Create a user (no id) 
        StaticUser user = new StaticUser();
        user.setName("LegoLover");
        user = restUpdate(user, "StaticUser", StaticUser.class, false);

        // Update bid with the auction
        restUpdateBidirectionalRelationship(String.valueOf(bid.getId()), "StaticBid", "auction", auction, DEFAULT_PU,
                MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, "bids", true);

        // update bid with the user
        String bidJsonResponse = restUpdateBidirectionalRelationship(String.valueOf(bid.getId()), "StaticBid", "user", user,
                DEFAULT_PU, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, null, true);

        String expectedAuctionLink = RestUtils.getServerURI() + DEFAULT_PU + "/entity/StaticBid/" + bid.getId() + "/auction";
        String expectedUserLink = RestUtils.getServerURI() + DEFAULT_PU + "/entity/StaticBid/" + bid.getId() + "/user";

        assertTrue(bidJsonResponse.toUpperCase().indexOf(expectedAuctionLink.toUpperCase()) != -1);
        assertTrue(bidJsonResponse.toUpperCase().indexOf(expectedUserLink.toUpperCase()) != -1);

        // Read auction, using the link provided in the newly created bid above
        StaticAuction auctionByLink = restReadByHref(expectedAuctionLink, "StaticAuction", MediaType.APPLICATION_JSON_TYPE);

        // Read user, using the link provided in the newly created bid above
        StaticUser userByLink = restReadByHref(expectedUserLink, "StaticUser", MediaType.APPLICATION_JSON_TYPE);

        assertTrue("Wrong user, could not update bid with a user.", userByLink.getName().equals(user.getName()));
        assertTrue("Wrong auction, could not update bid with an auction.", auctionByLink.getName().equals(auction.getName()));

        dbDelete(bid);
        dbDelete(user);
        dbDelete(auction);
    }

    /**
     * Test read composite pk.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testReadCompositePK() throws RestCallFailedException, URISyntaxException {
        StaticAddress address = new StaticAddress();
        address.setId(10012090);
        address.setType("home");
        address.setCity("Ottawa");
        dbCreate(address);

        String id = "10012090+home";
        StaticAddress addr = restRead(id, "StaticAddress", StaticAddress.class);

        assertTrue("Wrong address", addr.getId() == 10012090);

        dbDelete(address);
    }

    /**
     * Test read non existing composite pk.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistingCompositePK() throws RestCallFailedException, URISyntaxException {
        String id = "home+10012090";
        restRead(id, "StaticAddress", StaticAddress.class);
    }

    /**
     * Test get contexts xml.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testGetContextsXML() throws URISyntaxException {
        StringBuffer uri = new StringBuffer();
        uri.append(RestUtils.getServerURI());
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }

        String result = response.getEntity(String.class);

        assertTrue(result != null);

        assertTrue(result.contains(RestUtils.getServerURI() + "jpars_auction/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI() + "jpars_auction-static-local/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI() + "jpars_auction-static/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI() + "jpars_employee-static/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI() + "jpars_phonebook/metadata"));
    }

    /**
     * Test get contexts json.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testGetContextsJSON() throws URISyntaxException {
        StringBuffer uri = new StringBuffer();
        uri.append(RestUtils.getServerURI());
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }

        String result = response.getEntity(String.class);

        assertTrue(result != null);
        assertTrue(result.contains(RestUtils.getServerURI() + "jpars_auction/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI() + "jpars_auction-static-local/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI() + "jpars_auction-static/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI() + "jpars_employee-static/metadata"));
        assertTrue(result.contains(RestUtils.getServerURI() + "jpars_phonebook/metadata"));
    }

    /**
     * Test get types xml.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testGetTypesXML() throws URISyntaxException {
        StringBuffer uri = new StringBuffer();
        uri.append(RestUtils.getServerURI() + DEFAULT_PU + "/metadata");
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }

        String result = response.getEntity(String.class);

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
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testGetTypesJSON() throws URISyntaxException {
        StringBuffer uri = new StringBuffer();
        uri.append(RestUtils.getServerURI() + DEFAULT_PU + "/metadata");
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }

        String result = response.getEntity(String.class);

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
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testRemoveRelationshipNonCollection() throws RestCallFailedException, URISyntaxException, JAXBException {
        StaticBid bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        StaticUser origUser = bid.getUser();

        StaticUser newUser = new StaticUser();
        newUser.setName("Mark");

        // add a user to bid
        bid = restUpdateRelationship(String.valueOf(StaticModelDatabasePopulator.BID1_ID), "StaticBid", "user", newUser, StaticBid.class, "jpars_auction-static", MediaType.APPLICATION_JSON_TYPE,
                MediaType.APPLICATION_JSON_TYPE);
        assertTrue("Wrong user.", bid.getUser().getName().equals("Mark"));

        // remove relationship between bid and the new user
        String userRemoved = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(bid.getId()), StaticBid.class.getSimpleName(), "user", MediaType.APPLICATION_JSON_TYPE, null, null);
        assertTrue(userRemoved != null);
        dbDelete(newUser);

        // Put the original user back
        bid = restUpdateRelationship(String.valueOf(StaticModelDatabasePopulator.BID1_ID), "StaticBid", "user", origUser, StaticBid.class, "jpars_auction-static", MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
        assertTrue("Wrong user.", bid.getUser().getName().equals("user1"));
    }

    /**
     * Test create employee with phone numbers non idempotent.
     *
     * @throws Exception the exception
     */
    @Test(expected = RestCallFailedException.class)
    public void testCreateEmployeeWithPhoneNumbersNonIdempotent() throws Exception {
        String auction = RestUtils.getJSONMessage("auction-bidsByValueNoId.json");
        // The bid contained by the auction object has generated id field, and create is idempotent.
        // So, create operation on auction with bid list should fail.
        RestUtils.restCreateWithSequence(context, auction, StaticAuction.class.getSimpleName(), null, MediaType.APPLICATION_JSON_TYPE);
    }

    private static void dbCreate(Object object) {
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.persist(object);
        em.getTransaction().commit();
    }

    private static <T> T dbRead(Object id, Class<T> resultClass) {
        context.getEmf().getCache().evictAll();
        EntityManager em = context.getEmf().createEntityManager();
        return em.find(resultClass, id);
    }

    private static void dbUpdate(Object object) {
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.merge(object);
        em.getTransaction().commit();
    }

    private static void dbDelete(Object object) {
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        Object merged = em.merge(object);
        em.remove(merged);
        em.getTransaction().commit();
    }

    private static <T> T restCreate(Object object, String type, Class<T> resultClass) throws RestCallFailedException, URISyntaxException {
        return restCreate(object, type, resultClass, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    }

    private static <T> T restCreate(Object object, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException, URISyntaxException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            context.marshall(object, inputMediaType, os, false);
        } catch (JAXBException e) {
            fail("Exception thrown unmarshalling: " + e);
        }
        return restCreate(os, type, resultClass, persistenceUnit, null, inputMediaType, outputMediaType);
    }

    @SuppressWarnings("unchecked")
    private static <T> T restCreate(OutputStream os, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException, URISyntaxException {
        StringBuffer uri = new StringBuffer();
        uri.append(RestUtils.getServerURI() + persistenceUnit);
        if (tenantId != null) {
            for (String key : tenantId.keySet()) {
                uri.append(";" + key + "=" + tenantId.get(key));
            }
        }
        uri.append("/entity/" + type);
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.type(inputMediaType).accept(outputMediaType).put(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        if (status != Status.CREATED) {
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        T resultObject = null;
        try {
            resultObject = (T) context.unmarshalEntity(type, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e) {
            fail("Exception thrown unmarshalling: " + e);
        }
        return resultObject;
    }

    private static <T> void restDelete(Object id, String type, Class<T> resultClass) throws RestCallFailedException, URISyntaxException {
        restDelete(id, type, resultClass, DEFAULT_PU, null);
    }

    private static <T> void restDelete(Object id, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId) throws RestCallFailedException, URISyntaxException {
        StringBuffer uri = new StringBuffer();
        uri.append(RestUtils.getServerURI() + persistenceUnit);
        if (tenantId != null) {
            for (String key : tenantId.keySet()) {
                uri.append(";" + key + "=" + tenantId.get(key));
            }
        }
        uri.append("/entity/" + type + "/" + id);
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }
    }

    private static Object restNamedQuery(String queryName, String returnType, Map<String, Object> parameters, Map<String, String> hints) throws URISyntaxException {
        return restNamedQuery(queryName, returnType, DEFAULT_PU, parameters, hints, MediaType.APPLICATION_JSON_TYPE);
    }

    private static Object restNamedQuery(String queryName, String returnType, String persistenceUnit, Map<String, Object> parameters, Map<String, String> hints, MediaType outputMediaType) throws URISyntaxException {
        StringBuffer resourceURL = new StringBuffer();
        resourceURL.append(RestUtils.getServerURI() + persistenceUnit + "/query/" + queryName);
        appendParametersAndHints(resourceURL, parameters, hints);
        WebResource webResource = client.resource(resourceURL.toString());
        ClientResponse response = webResource.accept(outputMediaType).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        try {
            return context.unmarshalEntity(returnType, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e) {
            fail("Exception thrown unmarshalling: " + e);
        }
        return null;
    }

    private static Object restNamedSingleResultQuery(String queryName, String returnType, String persistenceUnit, Map<String, Object> parameters, Map<String, String> hints, MediaType outputMediaType) throws URISyntaxException {
        StringBuffer resourceURL = new StringBuffer();
        resourceURL.append(RestUtils.getServerURI() + persistenceUnit + "/singleResultQuery/" + queryName);
        appendParametersAndHints(resourceURL, parameters, hints);

        WebResource webResource = client.resource(resourceURL.toString());
        ClientResponse response = webResource.accept(outputMediaType).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        try {
            return context.unmarshalEntity(returnType, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e) {
            fail("Exception thrown unmarshalling: " + e);
        }
        return null;
    }

    private static void appendParametersAndHints(StringBuffer resourceURL, Map<String, Object> parameters, Map<String, String> hints) {
        if (parameters != null && !parameters.isEmpty()) {
            for (String key : parameters.keySet()) {
                resourceURL.append(";" + key + "=" + parameters.get(key));
            }
        }
        if (hints != null && !hints.isEmpty()) {
            boolean firstElement = true;

            for (String key : hints.keySet()) {
                if (firstElement) {
                    resourceURL.append("?");
                } else {
                    resourceURL.append("&");
                }
                resourceURL.append(key + "=" + hints.get(key));
            }
        }
    }

    private static <T> T restRead(Object id, String type, Class<T> resultClass) throws RestCallFailedException, URISyntaxException {
        return restRead(id, type, resultClass, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE);
    }

    @SuppressWarnings("unchecked")
    private static <T> T restRead(Object id, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType outputMediaType) throws RestCallFailedException, URISyntaxException {
        StringBuffer uri = new StringBuffer();
        uri.append(RestUtils.getServerURI() + persistenceUnit);
        if (tenantId != null) {
            for (String key : tenantId.keySet()) {
                uri.append(";" + key + "=" + tenantId.get(key));
            }
        }
        uri.append("/entity/" + type + "/" + id);
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.accept(outputMediaType).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        T resultObject = null;
        try {
            resultObject = (T) context.unmarshalEntity(type, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e) {
            fail("Exception thrown unmarshalling: " + e);
        }
        return resultObject;
    }

    private static <T> T restUpdate(Object object, String type, Class<T> resultClass, boolean sendLinks) throws RestCallFailedException, URISyntaxException {
        return restUpdate(object, type, resultClass, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, sendLinks);
    }

    private static <T> T restUpdate(Object object, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType inputMediaType, MediaType outputMediaType, boolean sendLinks) throws RestCallFailedException, URISyntaxException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            if (sendLinks) {
                context.marshallEntity(object, inputMediaType, os);
            } else {
                context.marshall(object, inputMediaType, os, false);
            }
        } catch (JAXBException e) {
            fail("Exception thrown unmarshalling: " + e);
        }
        return restUpdate(os, type, resultClass, persistenceUnit, tenantId, inputMediaType, outputMediaType, sendLinks);
    }

    @SuppressWarnings("unchecked")
    private static <T> T restUpdate(OutputStream os, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType inputMediaType, MediaType outputMediaType, boolean sendLinks) throws RestCallFailedException, URISyntaxException {
        StringBuffer uri = new StringBuffer();
        uri.append(RestUtils.getServerURI() + persistenceUnit);
        if (tenantId != null) {
            for (String key : tenantId.keySet()) {
                uri.append(";" + key + "=" + tenantId.get(key));
            }
        }
        uri.append("/entity/" + type);
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.type(inputMediaType).accept(outputMediaType).post(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);

        T resultObject = null;
        try {
            resultObject = (T) context.unmarshalEntity(type, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e) {
            fail("Exception thrown unmarshalling: " + e);
        }
        return resultObject;
    }

    @SuppressWarnings("unchecked")
    private static <T> T restUpdateRelationship(String objectId, String type, String relationshipName, Object newValue, Class<T> resultClass, String persistenceUnit, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException, URISyntaxException {
        WebResource webResource = client.resource(RestUtils.getServerURI() + persistenceUnit + "/entity/" + type + "/" + objectId + "/" + relationshipName);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            context.marshallEntity(newValue, inputMediaType, os);

        } catch (JAXBException e) {
            fail("Exception thrown unmarshalling: " + e);
        }
        ClientResponse response = webResource.type(inputMediaType).accept(outputMediaType).post(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);

        T resultObject = null;
        try {
            resultObject = (T) context.unmarshalEntity(type, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e) {
            fail("Exception thrown unmarshalling: " + e);
        }
        return resultObject;
    }

    @SuppressWarnings("unchecked")
    private static <T> T restReadByHref(String href, String type, MediaType outputMediaType) throws RestCallFailedException, JAXBException {
        WebResource webResource = client.resource(href);
        ClientResponse response = webResource.accept(outputMediaType).get(
                ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        return (T) context.unmarshalEntity(type, outputMediaType, new ByteArrayInputStream(result.getBytes()));
    }

    private static String restUpdateBidirectionalRelationship(String objectId,
            String type, String relationshipName, Object newValue,
            String persistenceUnit, MediaType inputMediaType,
            MediaType outputMediaType, String partner, boolean sendLinks)
            throws RestCallFailedException, JAXBException, URISyntaxException {

        String url = RestUtils.getServerURI() + persistenceUnit + "/entity/" + type + "/"
                + objectId + "/" + relationshipName;
        if (partner != null) {
            url += "?" + QueryParameters.JPARS_RELATIONSHIP_PARTNER + "=" + partner;
        }

        WebResource webResource = client.resource(url);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        context.marshall(newValue, inputMediaType, os, sendLinks);
        ClientResponse response = webResource.type(inputMediaType)
                .accept(outputMediaType)
                .post(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }
        return response.getEntity(String.class);
    }
}
