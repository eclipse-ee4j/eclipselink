package org.eclipse.persistence.jpars.test.server;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.IOException;
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
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactory;
import org.eclipse.persistence.jpa.rs.metadata.DatabaseMetadataStore;
import org.eclipse.persistence.jpa.rs.metadata.model.Attribute;
import org.eclipse.persistence.jpa.rs.metadata.model.Descriptor;
import org.eclipse.persistence.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.jpa.rs.metadata.model.PersistenceUnit;
import org.eclipse.persistence.jpa.rs.metadata.model.Query;
import org.eclipse.persistence.jpa.rs.util.LinkAdapter;
import org.eclipse.persistence.jpars.test.model.StaticAuction;
import org.eclipse.persistence.jpars.test.model.StaticBid;
import org.eclipse.persistence.jpars.test.model.StaticUser;
import org.eclipse.persistence.jpars.test.model.multitenant.Account;
import org.eclipse.persistence.jpars.test.util.StaticModelDatabasePopulator;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

public class ServerCrudTest {
    
    public static final String SERVER_URI_BASE = "server.uri.base";
    public static final String DEFAULT_SERVER_URI_BASE = "http://localhost:8080";
    public static final String APPLICATION_LOCATION = "/eclipselink.jpars.test/persistence/";
    public static final String DEFAULT_PU = "auction-static";
    protected static Client client = null;
    protected static Unmarshaller unmarshaller = null;
    protected static PersistenceContext context = null;
    
    @BeforeClass
    public static void setup(){
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties); 
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);

        PersistenceFactory factory = new PersistenceFactory();
        factory.setMetadataStore(new DatabaseMetadataStore());
        factory.getMetadataStore().setProperties(properties);
        factory.getMetadataStore().clearMetadata();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(DEFAULT_PU, properties);
        try{
            context = factory.bootstrapPersistenceContext("auction-static", emf, new URI("http://localhost:8080/JPA-RS/"), false);
        } catch (URISyntaxException e){
            throw new RuntimeException("Setup Exception ", e);
        }
        
        StaticModelDatabasePopulator.populateDB(emf);
        client = Client.create();
    }
    
    
    @Test
    public void testRead(){
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class);
        StaticBid bid2 = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong big in DB.", bid.getBid() == bid2.getBid());
    }
    
    @Test
    public void testReadXML(){
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE);
        StaticBid bid2 = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong big in DB.", bid.getBid() == bid2.getBid());
    }
    
    @Test
    public void testReadNonExistant(){
        RestCallFailedException exc = null;
        try{
            restRead(0, "StaticBid", StaticBid.class);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object read.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object read.", exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
    
    @Test
    public void testReadNonExistantType(){
        RestCallFailedException exc = null;
        try{
            restRead(1, "NonExistant", StaticBid.class);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object read.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object read.", exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
    
    @Test
    public void testReadNonExistantPU(){
        RestCallFailedException exc = null;
        try{
            restRead(1, "StaticBid", StaticBid.class, "non-existant", null, MediaType.APPLICATION_JSON_TYPE);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object read.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object read.", exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
    
    @Test
    public void testUpdate(){
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class);
        bid.setBid(120);
        bid = restUpdate(bid, "StaticBid", StaticBid.class);
        assertTrue("Wrong big retrieved.", bid.getBid() == 120);
        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong big retrieved in db.", bid.getBid() == 120);
        assertTrue("No auction for Bid in db", bid.getAuction() != null);
        bid.setBid(110);
        bid = restUpdate(bid, "StaticBid", StaticBid.class);
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
    
    @Test
    public void testCreateSequenced(){
        StaticAuction auction = new StaticAuction();
        auction.setName("Laptop");
        RestCallFailedException exc = null;
        try{
            auction = restCreate(auction, "StaticAuction", StaticAuction.class);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for create with sequencing.", exc != null);
        assertTrue("Wrong exception thrown for create with sequencing. " + exc.getResponseStatus(), exc.getResponseStatus().equals(Status.BAD_REQUEST));

    }
    
    @Test
    public void testCreateNonExistant(){
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(102);
        RestCallFailedException exc = null;
        try{
            user = restCreate(user, "NonExistant", StaticUser.class);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object create.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object create. " + exc.getResponseStatus(), exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
    
    @Test
    public void testCreateNonExistantPersistenceUnit(){
        StaticUser user = new StaticUser();
        user.setName("Joe");
        user.setId(103);
        RestCallFailedException exc = null;
        try{
            user = restCreate(user, "StaticUser", StaticUser.class, "non-existant", null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object create.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object create.", exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
   
    /**
     * Removed.  Requires a JSON parsing feature to detect incorrect data

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
    public void testCreateGarbage(){
        
        WebResource webResource = client.resource(getServerURI() + DEFAULT_PU + "/entity/" + "StaticUser");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] b = "Garbage".getBytes();
        try{
            os.write(b);
        } catch (IOException e){
            fail("Error serializing data: "+ e);
        }
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        assertTrue("Wrong exception garbage write. " + status, status.equals(Status.BAD_REQUEST));
    }
    
    @Test
    public void testUpdateXML(){
        StaticBid bid = restRead(StaticModelDatabasePopulator.BID1_ID, "StaticBid", StaticBid.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE);
        bid.setBid(120);
        bid = restUpdate(bid, "StaticBid", StaticBid.class, DEFAULT_PU, null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
        assertTrue("Wrong big retrieved.", bid.getBid() == 120);
        bid = dbRead(StaticModelDatabasePopulator.BID1_ID, StaticBid.class);
        assertTrue("Wrong big retrieved in db.", bid.getBid() == 120);
        bid.setBid(110);
        bid = restUpdate(bid, "StaticBid", StaticBid.class);
    }
    
    @Test
    public void testPostNewEntity(){
        StaticAuction auction = new StaticAuction();
        auction.setName("Computer");
        auction = restUpdate(auction, "StaticAuction", StaticAuction.class);
        assertTrue("Wrong User returned.", auction.getName().equals("Computer"));
        assertTrue("User not sequenced.", auction.getId() > 0);
        StaticAuction dbAuction = dbRead(auction.getId(), StaticAuction.class);
        assertTrue("Wrong user retrieved in db.", auction.getName().equals(dbAuction.getName()));
        restDelete(auction.getId(), "StaticAuction", StaticAuction.class);
    }
    
    @Test
    public void testUpdateNonExistant(){
        StaticUser user = new StaticUser();
        user.setName("Joe");
        RestCallFailedException exc = null;
        try{
            user = restUpdate(user, "NonExistant", StaticUser.class);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object update.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object update. " + exc.getResponseStatus(), exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
    
    @Test
    public void testUpdateNonExistantPersistenceUnit(){
        StaticUser user = new StaticUser();
        user.setName("Joe");
        RestCallFailedException exc = null;
        try{
            user = restUpdate(user, "StaticUser", StaticUser.class, "non-existant", null, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object create.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object create.", exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
    
    @Test
    public void testUpdateGarbage(){
        WebResource webResource = client.resource(getServerURI() + DEFAULT_PU + "/entity/" + "StaticUser");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] b = "Garbage".getBytes();
        try{
            os.write(b);
        } catch (IOException e){
            fail("Error serializing data: "+ e);
        }
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
        try{
            restDelete(1000, "StaticUser", StaticUser.class);
        } catch (RestCallFailedException e){
            fail("Exception thrown for non-existant object delete.");
        }
    }
    
    @Test
    public void testDeleteNonExistantType(){
        RestCallFailedException exc = null;
        try{
            restDelete(1000, "NonExistant", StaticUser.class);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object delete.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object delete. " + exc.getResponseStatus(), exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
    
    @Test
    public void testDeleteNonExistantPersistenceUnit(){
        RestCallFailedException exc = null;
        try{
            restDelete(1000, "StaticUser", StaticUser.class, "non-existant", null);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object delete.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object delete.", exc.getResponseStatus().equals(Status.NOT_FOUND));
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
    
    @Test
    public void testNamedQueryWrongParameter(){
        RestCallFailedException exc = null;
        try{
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("wrong", StaticModelDatabasePopulator.USER1_ID);
            restNamedQuery("User.byId", "StaticUser", parameters, null);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for wrong paramter on query.", exc != null);
        assertTrue("Wrong exception for wrong paramter on query: " + exc.getResponseStatus(), exc.getResponseStatus().equals(Status.BAD_REQUEST));
    }
    
    @Test
    public void testNamedQueryWrongNumberOfParameters(){
        RestCallFailedException exc = null;
        try{
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
            restNamedQuery("User.byNameOrId", "StaticUser", parameters, null);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for wrong numbers of paramters on query.", exc != null);
        assertTrue("Wrong exception for wrong numbers of paramters on query: " + exc.getResponseStatus(), exc.getResponseStatus().equals(Status.BAD_REQUEST));
    }
    
    @Test
    public void testNamedQueryNoResults(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", 0);
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.byId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 0);
    }
    
    @Test
    public void testNonExistantNamedQuery(){
        RestCallFailedException exc = null;
        try{
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
            restNamedQuery("User.nonExistant", "StaticUser", parameters, null);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non existant query.", exc != null);
        assertTrue("Wrong exception for nonExistantQuery " + exc.getResponseStatus(), exc.getResponseStatus().equals(Status.BAD_REQUEST));
    }
    
    @Test
    public void testNonExistantPersistenceUnitNamedQuery(){
        RestCallFailedException exc = null;
        try{
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("id", StaticModelDatabasePopulator.USER1_ID);
            restNamedQuery("User.all", "StatisUser", "nonExistant", parameters, null, MediaType.APPLICATION_JSON_TYPE);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for query on non-existant persistence unit.", exc != null);
        assertTrue("Wrong exception for  query on non-existant persistence unit. " + exc.getResponseStatus(), exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
    
    @Test
    public void testNamedQueryHint(){
        // load the cache
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.all", "StaticUser", null, null);
        assertTrue("Incorrect Number of users found.", users.size() == 3);
        StaticUser user1 = users.get(0);
        String oldName = user1.getName();
        user1.setName("Changed");
        dbUpdate(user1);
        Map<String, String> hints = new HashMap<String, String>();
        hints.put(QueryHints.REFRESH, "true");
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
    
    @Test
    public void testNamedQuerySingleResultNoResult(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", 0);
        RestCallFailedException exc = null;
        try{
            StaticUser user = (StaticUser)restNamedSingleResultQuery("User.byId", "StaticUser", DEFAULT_PU, parameters, null, MediaType.APPLICATION_JSON_TYPE);
            assertTrue("user shoudl not have been returned", user == null);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for query with no result.", exc != null);
        assertTrue("Wrong exception for for query with no result " + exc.getResponseStatus(), exc.getResponseStatus().equals(Status.NOT_FOUND));

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
    
    @Test
    public void testMultitenant(){
        Account account = new Account();
        account.setAccoutNumber("AAA1");
        Map<String, String> tenantId = new HashMap<String, String>();
        tenantId.put("tenant.id", "AcctHolder1");
        account = restUpdate(account, "Account", Account.class, DEFAULT_PU, tenantId, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);

        assertTrue("account not created.", account != null);
        
        account = restRead(account.getId(), "Account", Account.class, DEFAULT_PU, tenantId, MediaType.APPLICATION_JSON_TYPE);
        
        assertTrue("account not read.", account != null);
        assertTrue("account not completely read.", account.getAccoutNumber().equals("AAA1"));
        
        Map<String, String> tenantId2 = new HashMap<String, String>();
        tenantId2.put("tenant.id", "AcctHolder2");
        RestCallFailedException rcfe = null;
        try{
            Account account2 = restRead(account.getId(), "Account", Account.class, DEFAULT_PU, tenantId2, MediaType.APPLICATION_JSON_TYPE);
        } catch (RestCallFailedException ex){
            rcfe = ex;
        }
        assertTrue("No exception on read from wrong tenant.", rcfe != null);
        
        restDelete(account.getId(), "Account", Account.class, DEFAULT_PU, tenantId);
    }
    
    public static <T> T dbRead(Object id, Class<T> resultClass){
        context.getEmf().getCache().evictAll();
        EntityManager em = context.getEmf().createEntityManager();
        return em.find(resultClass, id);
    }
    
    public static void dbUpdate(Object object){
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        em.merge(object);
        em.getTransaction().commit();
    }
    
    public static void dbDelete(Object object){
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        Object merged = em.merge(object);
        em.remove(merged);
        em.getTransaction().commit();
    }
    
    public static <T> T restCreate(Object object, String type, Class<T> resultClass) throws RestCallFailedException {
        return restCreate(object, type, resultClass, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    }
    
    public static <T> T restCreate(Object object, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException {
        StringBuffer uri = new StringBuffer();
        uri.append(getServerURI() + persistenceUnit);
        if (tenantId != null){
            for (String key: tenantId.keySet()){
                uri.append(";" + key + "=" + tenantId.get(key));
            }
        }
        uri.append("/entity/" + type);
        WebResource webResource = client.resource(uri.toString());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            context.marshallEntity(object, inputMediaType, os);       
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
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
    public static <T> void restDelete(Object id, String type, Class<T> resultClass) throws RestCallFailedException {
        restDelete(id, type, resultClass, DEFAULT_PU, null);
    }
    
    public static <T> void restDelete(Object id, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId) throws RestCallFailedException {
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
    
    public static Object restNamedQuery(String queryName, String returnType, Map<String, Object> parameters, Map<String, String> hints){
        return restNamedQuery(queryName, returnType, DEFAULT_PU, parameters, hints, MediaType.APPLICATION_JSON_TYPE);
    }
    
    public static Object restNamedQuery(String queryName, String returnType, String persistenceUnit, Map<String, Object> parameters, Map<String, String> hints, MediaType outputMediaType){
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
    
    public static Object restNamedSingleResultQuery(String queryName, String returnType, String persistenceUnit, Map<String, Object> parameters, Map<String, String> hints, MediaType outputMediaType){
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
    
    public static Object restUpdateQuery(String queryName, String returnType, String persistenceUnit, Map<String, Object> parameters, Map<String, String> hints){
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
    
    public static <T> T restRead(Object id, String type, Class<T> resultClass) throws RestCallFailedException {
        return restRead(id, type, resultClass, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE);
    }
    
    public static <T> T restRead(Object id, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType outputMediaType) throws RestCallFailedException {
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
    
    
    public static <T> T restUpdate(Object object, String type, Class<T> resultClass) throws RestCallFailedException {
        return restUpdate(object, type, resultClass, DEFAULT_PU, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    }
    
    public static <T> T restUpdate(Object object, String type, Class<T> resultClass, String persistenceUnit, Map<String, String> tenantId, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException {
        StringBuffer uri = new StringBuffer();
        uri.append(getServerURI() + persistenceUnit);
        if (tenantId != null){
            for (String key: tenantId.keySet()){
                uri.append(";" + key + "=" + tenantId.get(key));
            }
        }
        uri.append("/entity/" + type);
        WebResource webResource = client.resource(uri.toString());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            context.marshallEntity(object, inputMediaType, os);
        
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
    
    public static <T> T restUpdateRelationship(String objectId, String type, String relationshipName, Object newValue, Class<T> resultClass, String persistenceUnit, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException {

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
    
    public static Object unmarshall(String result, String mediaType, Class expectedResultClass){
        if (unmarshaller == null){
            Class[] jaxbClasses = new Class[]{Link.class, LinkAdapter.class, PersistenceUnit.class, Descriptor.class, Attribute.class, Query.class};
            JAXBContext context = null;
            try{
                context = (JAXBContext)JAXBContextFactory.createContext(jaxbClasses, null);
                unmarshaller = context.createUnmarshaller();
                unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
            } catch (JAXBException e){
                e.printStackTrace();
            }
        }
        try{
            unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, mediaType);
            CharArrayReader reader = new CharArrayReader(result.toCharArray());
            StreamSource ss = new StreamSource(reader);
            Object unmarshalled = unmarshaller.unmarshal(ss, expectedResultClass);
            return unmarshalled;
        } catch (PropertyException exc){
            throw new RuntimeException(exc);
        } catch (JAXBException e){
            throw new RuntimeException(e);
        }

    }
    

    
    public static String getServerURI(){
        String serverURIBase = System.getProperty(SERVER_URI_BASE, DEFAULT_SERVER_URI_BASE);
        return serverURIBase + APPLICATION_LOCATION;
    }

}
