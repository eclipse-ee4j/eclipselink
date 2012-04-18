package org.eclipse.persistence.jpars.test.server;

import static org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

public class ServerCrudTest {
    
    public static final String SERVER_URI = "http://localhost:8080/org.eclipse.persistence.jpars.test.server/jpa-rs/";
    public static final String DEFAULT_PU = "auction-static";
    protected static Client client = null;
    protected static Unmarshaller unmarshaller = null;
    protected static PersistenceContext context = null;
    
    protected static int user1Id;
    protected static int user2Id;
    protected static int user3Id;
    
    protected static int auction1Id;
    protected static int auction2Id;
    protected static int auction3Id;
    
    protected static int bid1Id;;
    protected static int bid2Id;
    protected static int bid3Id;
    
    @BeforeClass
    public static void setup(){
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties); 
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
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
        
        populateDB(emf);
        
        client = Client.create();
    }
    
    
    @Test
    public void testRead(){
        StaticBid bid = restRead(bid1Id, "StaticBid", StaticBid.class);
        assertTrue("Wrong big retrieved.", bid.getBid() == 110);
        assertTrue("No user for Bid", bid.getUser() != null);
        assertTrue("No auction for Bid", bid.getAuction() != null);
        bid = dbRead(bid1Id, StaticBid.class);
        assertTrue("Wrong big in DB.", bid.getBid() == 110);
        assertTrue("No user for Bid in DB", bid.getUser() != null);
        assertTrue("No auction for Bid in DB", bid.getAuction() != null);
    }
    
    @Test
    public void testReadXML(){
        StaticBid bid = restRead(bid1Id, "StaticBid", StaticBid.class, DEFAULT_PU, MediaType.APPLICATION_XML_TYPE);
        assertTrue("Wrong big retrieved.", bid.getBid() == 110);
        assertTrue("No user for Bid", bid.getUser() != null);
        assertTrue("No auction for Bid", bid.getAuction() != null);
        bid = dbRead(bid1Id, StaticBid.class);
        assertTrue("Wrong big in DB.", bid.getBid() == 110);
        assertTrue("No user for Bid in DB", bid.getUser() != null);
        assertTrue("No auction for Bid in DB", bid.getAuction() != null);
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
            restRead(1, "StaticBid", StaticBid.class, "non-existant", MediaType.APPLICATION_JSON_TYPE);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object read.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object read.", exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
    
    @Test
    public void testUpdate(){
        StaticBid bid = restRead(bid1Id, "StaticBid", StaticBid.class);
        bid.setBid(120);
        bid = restUpdate(bid, "StaticBid", StaticBid.class);
        assertTrue("Wrong big retrieved.", bid.getBid() == 120);
        assertTrue("No user for Bid", bid.getUser() != null);
        assertTrue("No auction for Bid", bid.getAuction() != null);
        bid = dbRead(bid1Id, StaticBid.class);
        assertTrue("Wrong big retrieved in db.", bid.getBid() == 120);
        assertTrue("No user for Bid in db", bid.getUser() != null);
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
        user = restCreate(user, "StaticUser", StaticUser.class, DEFAULT_PU,  MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
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
            user = restCreate(user, "StaticUser", StaticUser.class, "non-existant",  MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
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
        
        WebResource webResource = client.resource(SERVER_URI + DEFAULT_PU + "/entity/" + "StaticUser");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] b = "Garbage".getBytes();
        try{
            os.write(b);
        } catch (IOException e){
            fail("Error serializing data: "+ e);
        }
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, os.toString());
        Status status = response.getClientResponseStatus();
        assertTrue("Wrong exception garbage write. " + status, status.equals(Status.INTERNAL_SERVER_ERROR));
    }
    
    @Test
    public void testUpdateXML(){
        StaticBid bid = restRead(bid1Id, "StaticBid", StaticBid.class, DEFAULT_PU, MediaType.APPLICATION_XML_TYPE);
        bid.setBid(120);
        bid = restUpdate(bid, "StaticBid", StaticBid.class, DEFAULT_PU, MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
        assertTrue("Wrong big retrieved.", bid.getBid() == 120);
        assertTrue("No user for Bid", bid.getUser() != null);
        assertTrue("No auction for Bid", bid.getAuction() != null);
        bid = dbRead(bid1Id, StaticBid.class);
        assertTrue("Wrong big retrieved in db.", bid.getBid() == 120);
        assertTrue("No user for Bid in db", bid.getUser() != null);
        assertTrue("No auction for Bid in db", bid.getAuction() != null);
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
            user = restUpdate(user, "StaticUser", StaticUser.class, "non-existant",  MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_XML_TYPE);
        } catch (RestCallFailedException e){
            exc = e;
        }
        assertTrue("No exception thrown for non-existant object create.", exc != null);
        assertTrue("Wrong exception thrown for non-existant object create.", exc.getResponseStatus().equals(Status.NOT_FOUND));
    }
    
    @Test
    public void testUpdateGarbage(){
        WebResource webResource = client.resource(SERVER_URI + DEFAULT_PU + "/entity/" + "StaticUser");
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
            restDelete(1000, "StaticUser", StaticUser.class, "non-existant");
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
        parameters.put("id", user1Id);
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.byId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 1);
        assertTrue("Wrong user returned", users.get(0).getId() == user1Id);
    }
    
    @Test
    public void testNamedQueryParameters(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", user1Id);
        parameters.put("name", "user2");
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.byNameOrId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 2);
    }
    
    @Test
    public void testNamedQueryWrongParameter(){
        RestCallFailedException exc = null;
        try{
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("wrong", user1Id);
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
            parameters.put("id", user1Id);
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
            parameters.put("id", user1Id);
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
            parameters.put("id", user1Id);
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
        parameters.put("id", user1Id);
        // load the cache
        List<StaticUser> users = (List<StaticUser>)restNamedQuery("User.byId", "StaticUser", parameters, null);
        assertTrue("Incorrect Number of users found.", users.size() == 1);
        assertTrue("Wrong user returned", users.get(0).getId() == user1Id);
        StaticUser user1 = users.get(0);
        String oldName = user1.getName();
        user1.setName("Changed2");
        dbUpdate(user1);
        Map<String, String> hints = new HashMap<String, String>();
        hints.put(QueryHints.REFRESH, "true");
        users = (List<StaticUser>)restNamedQuery("User.byId", "StaticUser", parameters, hints);
        assertTrue("User was not refreshed", users.get(0).getName().equals(user1.getName()));
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
        parameters.put("id", user1Id);
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
        parameters.put("id", user1Id);
        parameters.put("name", "newName");
        Object result = restUpdateQuery("User.updateName", "StaticUser", DEFAULT_PU, parameters, null);
        assertTrue(result.equals("1"));
        StaticUser user1 = dbRead(user1Id, StaticUser.class);
        assertTrue(user1.getName().equals("newName"));
        dbUpdate(user1());
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
    
    public static <T> T restCreate(Object object, String type, Class<T> resultClass) throws RestCallFailedException {
        return restCreate(object, type, resultClass, DEFAULT_PU, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    }
    
    public static <T> T restCreate(Object object, String type, Class<T> resultClass, String persistenceUnit, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException {
        WebResource webResource = client.resource(SERVER_URI + persistenceUnit + "/entity/" + type);
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
            resultObject = (T)context.unmarshalEntity(type, null, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return resultObject;
    }
    public static <T> void restDelete(Object id, String type, Class<T> resultClass) throws RestCallFailedException {
        restDelete(id, type, resultClass, DEFAULT_PU);
    }
    
    public static <T> void restDelete(Object id, String type, Class<T> resultClass, String persistenceUnit) throws RestCallFailedException {
        WebResource webResource = client.resource(SERVER_URI + persistenceUnit + "/entity/" + type + "/" + id);
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
        resourceURL.append(SERVER_URI + persistenceUnit + "/query/" + queryName);
        appendParametersAndHints(resourceURL, parameters, hints);
        WebResource webResource = client.resource(resourceURL.toString());
        ClientResponse response = webResource.accept(outputMediaType).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK){
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        try {
            return context.unmarshalEntity(returnType, null, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return null;
    }
    
    public static Object restNamedSingleResultQuery(String queryName, String returnType, String persistenceUnit, Map<String, Object> parameters, Map<String, String> hints, MediaType outputMediaType){
        StringBuffer resourceURL = new StringBuffer();
        resourceURL.append(SERVER_URI + persistenceUnit + "/singleResultQuery/" + queryName);
        appendParametersAndHints(resourceURL, parameters, hints);
        
        WebResource webResource = client.resource(resourceURL.toString());
        ClientResponse response = webResource.accept(outputMediaType).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK){
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        try {
            return context.unmarshalEntity(returnType, null, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return null;
    }
    
    public static Object restUpdateQuery(String queryName, String returnType, String persistenceUnit, Map<String, Object> parameters, Map<String, String> hints){
        StringBuffer resourceURL = new StringBuffer();
        resourceURL.append(SERVER_URI + persistenceUnit + "/query/" + queryName);
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
        return restRead(id, type, resultClass, DEFAULT_PU, MediaType.APPLICATION_JSON_TYPE);
    }
    
    public static <T> T restRead(Object id, String type, Class<T> resultClass, String persistenceUnit, MediaType outputMediaType) throws RestCallFailedException {
        WebResource webResource = client.resource(SERVER_URI + persistenceUnit + "/entity/" + type + "/" + id);
        ClientResponse response = webResource.accept(outputMediaType).get(ClientResponse.class);
        Status status = response.getClientResponseStatus();
        if (status != Status.OK){
            throw new RestCallFailedException(status);
        }
        String result = response.getEntity(String.class);
        T resultObject = null;
        try {
            resultObject = (T)context.unmarshalEntity(type, null, outputMediaType, new ByteArrayInputStream(result.getBytes()));
        } catch (JAXBException e){
            fail("Exception thrown unmarshalling: " + e);
        }
        return resultObject;
    }
    
    
    public static <T> T restUpdate(Object object, String type, Class<T> resultClass) throws RestCallFailedException {
        return restUpdate(object, type, resultClass, DEFAULT_PU, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
    }
    
    public static <T> T restUpdate(Object object, String type, Class<T> resultClass, String persistenceUnit, MediaType inputMediaType, MediaType outputMediaType) throws RestCallFailedException {

        WebResource webResource = client.resource(SERVER_URI + persistenceUnit + "/entity/" + type);
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
            resultObject = (T)context.unmarshalEntity(type, null, outputMediaType, new ByteArrayInputStream(result.getBytes()));
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
                unmarshaller.setProperty(JAXBContext.JSON_INCLUDE_ROOT, Boolean.FALSE);
            } catch (JAXBException e){
                e.printStackTrace();
            }
        }
        try{
            unmarshaller.setProperty(MEDIA_TYPE, mediaType);
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
    
    public static void populateDB(EntityManagerFactory emf){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        StaticUser user1 = user1();
        em.persist(user1);
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
        user1Id = user1.getId();
        user2Id = user2.getId();
        user3Id = user3.getId();
        
        auction1Id = auction1.getId();
        auction2Id = auction2.getId();
        auction3Id = auction3.getId();
        
        bid1Id = bid1.getId();
        bid2Id = bid2.getId();
        bid3Id = bid3.getId();
        
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

}
