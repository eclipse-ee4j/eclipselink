package org.eclipse.persistence.jpars.test.server;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
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
import org.eclipse.persistence.jpars.test.model.StaticAddress;
import org.eclipse.persistence.jpars.test.model.StaticAuction;
import org.eclipse.persistence.jpars.test.model.StaticBid;
import org.eclipse.persistence.jpars.test.model.StaticUser;
import org.eclipse.persistence.jpars.test.model.multitenant.Account;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.StaticModelDatabasePopulator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

public class ServerCrudTest {
    
    private static final String SERVER_URI_BASE = "server.uri.base";
    private static final String DEFAULT_SERVER_URI_BASE = "http://localhost:8080";
    private static final String APPLICATION_LOCATION = "/eclipselink.jpars.test/persistence/";
    private static final String DEFAULT_PU = "auction-static";
    private static Client client = null;
    private static PersistenceContext context = null;
    
    public static ByteArrayOutputStream sampleUserJSONForId(Integer userId, Integer addressId) throws IOException{
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        StringBuffer json = new StringBuffer();
        json.append("{\"address\":" + 
                "{\"city\":\"Ottawa\",");
        if (addressId != null){
            json.append("\"id\":" + addressId + ",");
        }
        json.append("\"postalCode\":\"K1P 1A4\",\"street\":\"Main Street\",\"type\":\"Business\"," + 
                "\"_relationships\":[{\"href\":\"http://localhost:8080/JPA-RS/auction-static/entity/StaticAddress/123456+Business/user\",\"rel\":\"user\"}]},");
        if (userId != null){
            json.append("\"id\":" + userId + ",");
        }
        json.append("\"name\":\"LegoLover\",\"version\":0," + 
                "\"_relationships\":[{\"href\":\"http://localhost:8080/JPA-RS/auction-static/entity/StaticUser/466/address\",\"rel\":\"address\"}]}");
        stream.write(json.toString().getBytes());
        return stream;
    }
    
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
        context = factory.bootstrapPersistenceContext("auction-static", emf, new URI(DEFAULT_SERVER_URI_BASE + "/JPA-RS/"), false);
        
        StaticModelDatabasePopulator.populateDB(emf);
        client = Client.create();
    }
    
    @AfterClass
    public static void teardown() {
        StaticModelDatabasePopulator.cleanupDB(context.getEmf());
    }
    
    @Test
    public void testRead(){
        // Create a user
        StaticUser user = new StaticUser();
        user.setId(466);
        user.setName("LegoLover");

        StaticAddress address = new StaticAddress();
        address.setCity("Ottawa");
        address.setId(123456);
        address.setStreet("Main Street");
        address.setPostalCode("K1P 1A4");
        address.setType("Business");

        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class);
        StaticBid bid2 = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong bid in DB.", bid.getAmount() == bid2.getAmount());
    }
    
    @Test
    public void testReadXML(){
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE);
        StaticBid bid2 = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong big in DB.", bid.getAmount() == bid2.getAmount());
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistant(){
        restRead(0, "StaticBid", StaticBid.class);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistantType(){
        restRead(1, "NonExistant", StaticBid.class);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testReadNonExistantPU(){
        restRead(1, "StaticBid", StaticBid.class, "non-existant", null, MediaType.APPLICATION_JSON_TYPE);
    }
    
    @Test
    public void testUpdate(){
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class);
        bid.setAmount(120);
        bid = restUpdate(bid, "StaticBid", StaticBid.class, true);
        assertTrue("Wrong big retrieved.", bid.getAmount() == 120);
        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong big retrieved in db.", bid.getAmount() == 120);
        assertTrue("No auction for Bid in db", bid.getAuction() != null);
        bid.setAmount(110);
        bid = restUpdate(bid, "StaticBid", StaticBid.class, true);
    }
    
    @Test
    public void testCreateDelete(){
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(100);
        user = restCreate(user, "StaticUser", StaticUser.class);
        assertTrue("Wrong big retrieved.", user.getName().equals("Joe"));
        StaticUser dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("DB User not equal ", user.equals(dbUser));
        restDelete(user.getId(), "StaticUser", StaticUser.class);
        dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("User was not deleted.", dbUser == null);
    }
    
    @Test
    public void testCreateXML(){
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(101);
        user = restCreate(user, "StaticUser", StaticUser.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
        assertTrue("Wrong big retrieved.", user.getName().equals("Joe"));
        StaticUser dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("DB User not equal ", user.equals(dbUser));
        restDelete(user.getId(), "StaticUser", StaticUser.class);
        dbUser = dbRead(user.getId(), StaticUser.class);
        assertTrue("User was not deleted.", dbUser == null);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testCreateSequenced(){
        StaticAuction auction = new StaticAuction();
        auction.setName("Laptop");
        auction = restCreate(auction, "StaticAuction", StaticAuction.class);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testCreateNonExistant(){
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(102);
        user = restCreate(user, "NonExistant", StaticUser.class);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testCreateNonExistantPersistenceUnit(){
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(103);
        user = restCreate(user, "StaticUser", StaticUser.class, "non-existant", null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
    }
   
    /**
     * Removed.  Requires a JSON parsing feature to detect incorrect data
     * @throws IOException 

    @Test
    public void testCreateWrongType(){
        
        WebResource webResource = client.resource(SERVER_URI + DEFAULT_PU + "/entity/" + "StaticUser");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            context.marshallEntity(new StaticBid(), MediaType.APPLICATION_JSON_TYPE, os);       
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        assertTrue("Wrong exception thrown for non-existant object read.", status.equals(Status.NOT_FOUND));
    }
    */
    
    @Test
    public void testCreateGarbage() throws IOException{
        WebResource webResource = client.resource(getServerURI() + DEFAULT_PU + "/entity/" + "StaticUser");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] b = "Garbage".getBytes();
        os.write(b);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        assertTrue("Wrong exception garbage write. " + status, status.equals(Status.BAD_REQUEST));
    }
    
    @Test
    public void testUpdateXML(){
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE);
        bid.setAmount(120);
        bid = restUpdate(bid, "StaticBid", StaticBid.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE, true);
        assertTrue("Wrong big retrieved.", bid.getAmount() == 120);
        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong big retrieved in db.", bid.getAmount() == 120);
        bid.setAmount(110);
        bid = restUpdate(bid, "StaticBid", StaticBid.class, true);
    }
    
    @Test
    public void testPostNewEntity(){
        StaticAuction auction = new StaticAuction();
        auction.setName("Computer");
        auction = restUpdate(auction, "StaticAuction", StaticAuction.class, true);
        assertTrue("Wrong Auction returned.", auction.getName().equals("Computer"));
        assertTrue("Auction not sequenced.", auction.getId() > 0);
        StaticAuction dbAuction = dbRead(auction.getId(), StaticAuction.class);
        assertTrue("Wrong user retrieved in db.", auction.getName().equals(dbAuction.getName()));
        restDelete(auction.getId(), "StaticAuction", StaticAuction.class);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testUpdateNonExistant() {
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user = restUpdate(user, "NonExistant", StaticUser.class, true);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testUpdateNonExistantPersistenceUnit(){
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user = restUpdate(user, "StaticUser", StaticUser.class, "non-existant", null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE, true);
    }
    
    @Test
    public void testUpdateGarbage() throws IOException{
        WebResource webResource = client.resource(getServerURI() + DEFAULT_PU + "/entity/" + "StaticUser");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] b = "Garbage".getBytes();
        os.write(b);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        assertTrue("Wrong exception garbage write. " + status, status.equals(Status.BAD_REQUEST));
    }
    
    @Test
    public void testUpdateRelationship(){
        StaticBid bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        StaticUser user = bid.getUser();
        StaticUser newUser = new StaticUser();
        newUser.setName("Mark");
        bid = restUpdateRelationship(String.valueOf(StaticModelDatabasePopulator.BID1_ID), "StaticBid", "user", newUser, StaticBid.class, "auction-static", MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);

        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong user.", bid.getUser().getName().equals("Mark"));
        newUser = bid.getUser();
        bid = restUpdateRelationship(String.valueOf(StaticModelDatabasePopulator.BID1_ID), "StaticBid", "user", user, StaticBid.class, "auction-static", MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong user.", bid.getUser().getName().equals(bid.getUser().getName()));
        dbDelete(newUser);
    }
    
    @Test
    public void testDeleteNonExistant(){
        restDelete(1000, "StaticUser", StaticUser.class);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testDeleteNonExistantType(){
        restDelete(1000, "NonExistant", StaticUser.class);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testDeleteNonExistantPersistenceUnit(){
        restDelete(1000, "StaticUser", StaticUser.class, "non-existant", null);
    }
    
    @Test
    public void testNamedQuery(){
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.all", "StaticUser", null, null);
        assertTrue("Incorrect Number of users found.", users.size() == 3);
    }
    
    @Test
    public void testNamedQueryParameter(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.byId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 1);
        assertTrue("Wrong user returned", users.get(0).getId() == StaticModelDatabasePopulator.USER1_ID);
    }
    
    @Test
    public void testNamedQueryParameters(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        parameters.put("name", "user2");
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.byNameOrId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 2);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testNamedQueryWrongParameter(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("wrong", StaticModelDatabasePopulator.USER1_ID);
        restNamedQuery("User.byId", "StaticUser", parameters, null);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testNamedQueryWrongNumberOfParameters(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        restNamedQuery("User.byNameOrId", "StaticUser", parameters, null);
    }
    
    @Test
    public void testNamedQueryNoResults(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", 0);
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.byId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 0);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testNonExistantNamedQuery(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        restNamedQuery("User.nonExistant", "StaticUser", parameters, null);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testNonExistantPersistenceUnitNamedQuery(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        restNamedQuery("User.all", "StatisUser", "nonExistant", parameters, null, MediaType.APPLICATION_JSON_TYPE);
    }
    
    @Test
    public void testNamedQueryHint(){
        // load the cache
        Map<String, String> hints = new HashMap<String, String>();
        hints.put(QueryHints.REFRESH, "true");
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.all", "StaticUser", null, hints);
        assertTrue("Incorrect Number of users found.", users.size() == 3);
        StaticUser user1 = users.get(0);
        String oldName = user1.getName();
        user1.setName("Changed");
        dbUpdate(user1);
        users = (List<StaticUser>)restNamedQuery("User.all", "StaticUser", null, hints);
        for (StaticUser user: users){
            if (user.getId() == user1.getId()){
                assertTrue("User was not refreshed", user.getName().equals(user1.getName()));
            }
        }
        user1 = dbRead(user1.getId(), StaticUser.class);
        user1.setName(oldName);
        dbUpdate(user1);        
        // refresh cache
        hints = new HashMap<String, String>();
        hints.put(QueryHints.REFRESH, "true");
        users = (List<StaticUser>)restNamedQuery("User.all", "StaticUser", null, hints);
    }
    
    @Test
    public void testNamedQueryParameterHint(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        // load the cache
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.byId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 1);
        assertTrue("Wrong user returned", users.get(0).getId() == StaticModelDatabasePopulator.USER1_ID);
        StaticUser user1 = users.get(0);
        String oldName = user1.getName();
        user1.setName("Changed2");
        dbUpdate(user1);
        Map<String, String> hints = new HashMap<String, String>();
        hints.put(QueryHints.REFRESH, "true");
        users = (List<StaticUser>)restNamedQuery("User.byId", "StaticUser", parameters, hints);
        assertTrue("User was not refreshed", users.get(0).getName().equals(user1.getName()));
        user1 = dbRead(user1.getId(), StaticUser.class);
        user1.setName(oldName);
        dbUpdate(user1);  
        
        // refresh cache
        hints = new HashMap<String, String>();
        hints.put(QueryHints.REFRESH, "true");
        users = (List<StaticUser>)restNamedQuery("User.all", "StaticUser", null, hints);
    }
    
    @Test
    public void testNamedQuerySingleResult(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        StaticUser user = (StaticUser)restNamedSingleResultQuery("User.byId", "StaticUser", DEFAULT_PU, parameters, null, MediaType.APPLICATION_JSON_TYPE);
        assertTrue("user was not returned", user != null);
        assertTrue("incorrect user returned", user.getName().equals("user1"));
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testNamedQuerySingleResultNoResult(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", 0);
        StaticUser user = (StaticUser)restNamedSingleResultQuery("User.byId", "StaticUser", DEFAULT_PU, parameters, null, MediaType.APPLICATION_JSON_TYPE);
        assertTrue("user shoudl not have been returned", user == null);
    }
    
    @Test
    public void testUpdateQuery(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
        parameters.put("name", "newName");
        Object result = restUpdateQuery("User.updateName", "StaticUser", DEFAULT_PU, parameters, null);
        assertTrue(result.equals("1"));
        StaticUser user1 = dbRead(StaticModelDatabasePopulator.USER1_ID, StaticUser.class);
        assertTrue(user1.getName().equals("newName"));
        user1 = dbRead(user1.getId(), StaticUser.class);
        user1.setName(StaticModelDatabasePopulator.user1().getName());
        dbUpdate(user1);
    }
    
    @Test(expected = RestCallFailedException.class)
    public void testMultitenant(){
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
        try{
            restRead(account.getId(), "Account", Account.class, DEFAULT_PU, tenantId2, MediaType.APPLICATION_JSON_TYPE);
        } finally {
            restDelete(account.getId(), "Account", Account.class, DEFAULT_PU, tenantId);
        }
    }
    
    @Test
    public void testCreateObjectGraphPut() throws RestCallFailedException,
            JAXBException {
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
        
        StaticUser user = null;
        try{
            user = restCreate(sampleUserJSONForId(466, 123456), "StaticUser", StaticUser.class, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
        } catch (IOException exc){
            fail(exc.toString());
        }

        // Update bid with the auction
        restUpdateBidirectionalRelationship(String.valueOf(777), "StaticBid",
                "auction", auction, "auction-static", MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, "bids", true);

        // update bid with the user
        String bidJsonResponse = restUpdateBidirectionalRelationship(
                String.valueOf(777), "StaticBid", "user", user, "auction-static", MediaType.APPLICATION_JSON_TYPE,
                MediaType.APPLICATION_JSON_TYPE, null, true);

        String expectedAuctionLink = getServerURI() + DEFAULT_PU + "/entity/StaticBid/777/auction";
        String expectedUserLink = getServerURI() + DEFAULT_PU + "/entity/StaticBid/777/user";

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

    @Test
    public void testCreateObjectGraphPost() throws RestCallFailedException,
            JAXBException {
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
        auction = restUpdate(auction, "StaticAuction", StaticAuction.class,
                false);

        StaticUser user = null;
        try{
            user = restUpdate(sampleUserJSONForId(null, null), "StaticUser", StaticUser.class, DEFAULT_PU, null,MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, true); 
        } catch (IOException exc){
            fail(exc.toString());
        }

        // Update bid with the auction
        restUpdateBidirectionalRelationship(String.valueOf(bid.getId()), "StaticBid", "auction", auction, "auction-static",
                MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, "bids", true);

        // update bid with the user
        String bidJsonResponse = restUpdateBidirectionalRelationship(String.valueOf(bid.getId()), "StaticBid", "user", user,
                "auction-static", MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, null, true);

        String expectedAuctionLink = getServerURI() + DEFAULT_PU + "/entity/StaticBid/" + bid.getId() + "/auction";
        String expectedUserLink = getServerURI() + DEFAULT_PU + "/entity/StaticBid/" + bid.getId() + "/user";

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
    
    private static <T> T dbRead(Object id, Class<T> resultClass){
        context.getEmf().getCache().evictAll();
        EntityManager em = context.getEmf().createEntityManager();
        return em.find(resultClass, id);
    }
    
    private static void dbUpdate(Object object){
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.merge(object);
        em.getTransaction().commit();
    }
    
    private static void dbDelete(Object object){
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        Object merged = em.merge(object);
        em.remove(merged);
        em.getTransaction().commit();
    }
    
    private static <T> T restCreate(Object object, String type, Class<T> resultClass) throws RestCallFailedException {
        return restCreate(object, type, resultClass, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    }
    
    private static <T> T restCreate(Object object, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            context.marshallEntity(object, inputMediaType, os, false);
        } catch (JAXBException e) {
            fail("Exception thrown unmarshalling: " + e);
        }
        return restCreate(os, type, resultClass, persistenceUnit, null, inputMediaType, outputMediaType);
    }

    
    private static <T> T restCreate(OutputStream os, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException {
        StringBuffer uri = new StringBuffer();
        uri.append(getServerURI() + persistenceUnit);
        if (tenantId != null){
            for (String key: tenantId.keySet()){
                uri.append(";" + key + "=" + tenantId.get(key));
            }
        }
        uri.append("/entity/" + type);
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.type(inputMediaType).accept(outputMediaType).put(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        if (status != Status.CREATED){
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        T resultObject = null;
        try {
            resultObject = (T)context.unmarshalEntity(type, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return resultObject;
    }
    
    private static <T> void restDelete(Object id, String type, Class<T> resultClass) throws RestCallFailedException {
        restDelete(id, type, resultClass, DEFAULT_PU, null);
    }
    
    private static <T> void restDelete(Object id, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId) throws RestCallFailedException {
        StringBuffer uri = new StringBuffer();
        uri.append(getServerURI() + persistenceUnit);
        if (tenantId != null){
            for (String key: tenantId.keySet()){
                uri.append(";" + key + "=" + tenantId.get(key));
            }
        }
        uri.append("/entity/" + type + "/" + id);
        WebResource webResource = client.resource(uri.toString());;
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK){
            throw new RestCallFailedException(status);
        }
    }
    
    private static Object restNamedQuery(String queryName, String returnType, Map<String, Object> parameters, Map<String, String> hints){
        return restNamedQuery(queryName, returnType, DEFAULT_PU, parameters, hints, MediaType.APPLICATION_JSON_TYPE);
    }
    
    private static Object restNamedQuery(String queryName, String returnType, String persistenceUnit, Map<String, Object> parameters, Map<String, String> hints, MediaType outputMediaType){
        StringBuffer resourceURL = new StringBuffer();
        resourceURL.append(getServerURI() + persistenceUnit + "/query/" + queryName);
        appendParametersAndHints(resourceURL, parameters, hints);
        WebResource webResource = client.resource(resourceURL.toString());
        ClientResponse response = webResource.accept(outputMediaType).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK){
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        try {
            return context.unmarshalEntity(returnType, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return null;
    }
    
    private static Object restNamedSingleResultQuery(String queryName, String returnType, String persistenceUnit, Map<String, Object> parameters, Map<String, String> hints, MediaType outputMediaType){
        StringBuffer resourceURL = new StringBuffer();
        resourceURL.append(getServerURI() + persistenceUnit + "/singleResultQuery/" + queryName);
        appendParametersAndHints(resourceURL, parameters, hints);
        
        WebResource webResource = client.resource(resourceURL.toString());
        ClientResponse response = webResource.accept(outputMediaType).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK){
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        try {
            return context.unmarshalEntity(returnType, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return null;
    }
    
    private static Object restUpdateQuery(String queryName, String returnType, String persistenceUnit, Map<String, Object> parameters, Map<String, String> hints){
        StringBuffer resourceURL = new StringBuffer();
        resourceURL.append(getServerURI() + persistenceUnit + "/query/" + queryName);
        appendParametersAndHints(resourceURL, parameters, hints);
        
        WebResource webResource = client.resource(resourceURL.toString());
        ClientResponse response = webResource.post(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK){
            throw new RestCallFailedException(status);
        }
        return response.getEntity(String.class);
    }
    
    private static void appendParametersAndHints(StringBuffer resourceURL, Map<String, Object> parameters, Map<String, String> hints){
        if (parameters != null && !parameters.isEmpty()){
            for (String key: parameters.keySet()){
                resourceURL.append(";" + key + "=" + parameters.get(key));
            }
        }
        if (hints != null && !hints.isEmpty()){
            boolean firstElement = true;
                
            for (String key: hints.keySet()){
                if (firstElement){
                    resourceURL.append("?"); 
                } else {
                    resourceURL.append("&");
                }
                resourceURL.append(key + "=" + hints.get(key));
            }
        }
    }
    
    private static <T> T restRead(Object id, String type, Class<T> resultClass) throws RestCallFailedException {
        return restRead(id, type, resultClass, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE);
    }
    
    private static <T> T restRead(Object id, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType outputMediaType) throws RestCallFailedException {
        StringBuffer uri = new StringBuffer();
        uri.append(getServerURI() + persistenceUnit);
        if (tenantId != null){
            for (String key: tenantId.keySet()){
                uri.append(";" + key + "=" + tenantId.get(key));
            }
        }
        uri.append("/entity/" + type + "/" + id);
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.accept(outputMediaType).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK){
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        T resultObject = null;
        try {
            resultObject = (T)context.unmarshalEntity(type, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return resultObject;
    }
    
    
    private static <T> T restUpdate(Object object, String type, Class<T> resultClass, boolean sendLinks) throws RestCallFailedException {
        return restUpdate(object, type, resultClass, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE, sendLinks);
    }

    private static <T> T restUpdate(Object object, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType inputMediaType, MediaType outputMediaType, boolean sendLinks) throws RestCallFailedException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            if (sendLinks) {
                context.marshallEntity(object, inputMediaType, os);
            } else {
                context.marshallEntity(object, inputMediaType, os, false);
            }
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return restUpdate(os, type, resultClass, persistenceUnit, tenantId, inputMediaType, outputMediaType, sendLinks);
    }

    
    private static <T> T restUpdate(OutputStream os, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType inputMediaType, MediaType outputMediaType, boolean sendLinks) throws RestCallFailedException {
        StringBuffer uri = new StringBuffer();
        uri.append(getServerURI() + persistenceUnit);
        if (tenantId != null){
            for (String key: tenantId.keySet()){
                uri.append(";" + key + "=" + tenantId.get(key));
            }
        }
        uri.append("/entity/" + type);
        WebResource webResource = client.resource(uri.toString());
        ClientResponse response = webResource.type(inputMediaType).accept(outputMediaType).post(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        if (status != Status.OK){
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        
        T resultObject = null;
        try {
            resultObject = (T)context.unmarshalEntity(type, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return resultObject;
    }
    
    private static <T> T restUpdateRelationship(String objectId, String type, String relationshipName, Object newValue, Class<T> resultClass, String persistenceUnit, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException {
        WebResource webResource = client.resource(getServerURI() + persistenceUnit + "/entity/" + type + "/" + objectId + "/" + relationshipName);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            context.marshallEntity(newValue, inputMediaType, os);
        
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        ClientResponse response = webResource.type(inputMediaType).accept(outputMediaType).post(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        if (status != Status.OK){
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        
        T resultObject = null;
        try {
            resultObject = (T)context.unmarshalEntity(type, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return resultObject;
    }
    
    private static <T> T restReadByHref(String href, String type, MediaType outputMediaType) throws RestCallFailedException, JAXBException {
        WebResource webResource = client.resource(href);
        ClientResponse response = webResource.accept(outputMediaType).get(
                ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        return (T) context.unmarshalEntity(type, outputMediaType,
                new ByteArrayInputStream(result.getBytes()));
    }

    private static String restUpdateBidirectionalRelationship(String objectId,
            String type, String relationshipName, Object newValue,
            String persistenceUnit, MediaType inputMediaType,
            MediaType outputMediaType, String partner, boolean sendLinks)
            throws RestCallFailedException, JAXBException {

        String url = getServerURI() + persistenceUnit + "/entity/" + type + "/"
                + objectId + "/" + relationshipName;
        if (partner != null) {
            url += ";partner=" + partner;
        }

        WebResource webResource = client.resource(url);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        context.marshallEntity(newValue, inputMediaType, os, sendLinks);
        ClientResponse response = webResource.type(inputMediaType)
                .accept(outputMediaType)
                .post(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        if (status != Status.OK) {
            throw new RestCallFailedException(status);
        }
        return response.getEntity(String.class);
    }
    private static String getServerURI(){
        String serverURIBase = System.getProperty(SERVER_URI_BASE, DEFAULT_SERVER_URI_BASE);
        return serverURIBase + APPLICATION_LOCATION;
    }

}

