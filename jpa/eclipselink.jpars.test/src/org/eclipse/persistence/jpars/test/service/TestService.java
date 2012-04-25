/****************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - 
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.service;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;


import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactory;
import org.eclipse.persistence.jpa.rs.Service;
import org.eclipse.persistence.jpa.rs.metadata.DatabaseMetadataStore;
import org.eclipse.persistence.jpa.rs.util.LinkAdapter;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.jpars.test.model.StaticAddress;
import org.eclipse.persistence.jpars.test.model.StaticUser;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.TestHttpHeaders;
import org.eclipse.persistence.jpars.test.util.TestURIInfo;
import org.eclipse.persistence.jpars.test.util.XMLFilePathBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the JPA RS service class
 * @author tware
 *
 */
public class TestService {

    private static PersistenceFactory factory;
    
    @BeforeClass
    public static void setup(){
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties); 
        factory = null;
        try{
            factory = new PersistenceFactory();
            factory.setMetadataStore(new DatabaseMetadataStore());
            factory.getMetadataStore().setProperties(properties);
            factory.getMetadataStore().clearMetadata();
            FileInputStream xmlStream = new FileInputStream(XMLFilePathBuilder.getXMLFileName("auction-persistence.xml"));

            PersistenceContext context = factory.bootstrapPersistenceContext("auction", xmlStream, properties, true);
            context.setBaseURI(new URI("http://localhost:8080/JPA-RS/"));
            
            xmlStream = new FileInputStream(XMLFilePathBuilder.getXMLFileName("/phonebook-persistence.xml"));
            context = factory.bootstrapPersistenceContext("phonebook", xmlStream, properties, true);
            context.setBaseURI(new URI("http://localhost:8080/JPA-RS/"));
            
            properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
            properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("auction-static", properties);
            context = factory.bootstrapPersistenceContext("auction-static", emf, new URI("http://localhost:8080/JPA-RS/"), false);

        } catch (Exception e){
            fail(e.toString());
        }
    }
    
    @AfterClass
    public static void teardown(){
        clearData();
        factory.getMetadataStore().clearMetadata();
    }
    
    protected static void clearData(){
        EntityManager em = factory.getPersistenceContext("auction").getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from Bid b").executeUpdate();
        em.createQuery("delete from Auction a").executeUpdate();
        em.createQuery("delete from User u").executeUpdate();

        em.getTransaction().commit();
        em = factory.getPersistenceContext("phonebook").getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from Person p").executeUpdate();
        em.getTransaction().commit();
    }
    
    @Test
    public void testUpdateUserList(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("auction");
        
        DynamicEntity entity = (DynamicEntity)context.newEntity("User");
        entity.set("name", "Jim");
        context.create(null, entity);
        
        entity.set("name", "James");
        
        DynamicEntity entity2 = (DynamicEntity)context.newEntity("User");
        entity2.set("name", "Jill");
        context.create(null, entity2);
        
        entity2.set("name", "Gillian");    
        
        List<DynamicEntity> entities = new ArrayList<DynamicEntity>();
        entities.add(entity);
        entities.add(entity2);

        StreamingOutput output = (StreamingOutput)service.update("auction", "User", generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo(), TestService.serializeListToStream(entities, context, MediaType.APPLICATION_JSON_TYPE)).getEntity();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            output.write(outputStream);
        } catch (IOException ex){
            fail(ex.toString());
        }
        InputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
        try{
            entities = (List<DynamicEntity>)context.unmarshalEntity("User", null, MediaType.APPLICATION_JSON_TYPE, stream);
        } catch (JAXBException e){
            fail("Exception unmarsalling: " + e);
        }
        assertNotNull("returned data was null", entities);
        assertTrue("returned data had wrong list size", entities.size() == 2);
        List<String> values = new ArrayList<String>();
        values.add("James");
        values.add("Gillian");
        for (DynamicEntity value: entities){
            assertTrue("Incorrect name returned", value.get("name").equals("James") || value.get("name").equals("Gillian"));
            values.remove(value.get("name"));
        }
        assertTrue("Incorrent set of names.", values.isEmpty());
        
        clearData();
    }
    
    @Test
    public void testUpdatePhoneNumberList(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("phonebook");
        
        DynamicEntity entity = (DynamicEntity)context.newEntity("Person");
        entity.set("firstName", "Jim");
        entity.set("lastName", "Jones");
        entity.set("phoneNumber", "1234567");
        context.create(null, entity);
        
        entity.set("firstName", "James");
        
        DynamicEntity entity2 = (DynamicEntity)context.newEntity("Person");
        entity2.set("firstName", "Jill");
        entity2.set("lastName", "Jones");
        context.create(null, entity2);
        
        entity2.set("firstName", "Gillian");    
        
        List<DynamicEntity> entities = new ArrayList<DynamicEntity>();
        entities.add(entity);
        entities.add(entity2);

        StreamingOutput output = (StreamingOutput)service.update("phonebook", "Person", generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo(), serializeListToStream(entities, context, MediaType.APPLICATION_JSON_TYPE)).getEntity();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            output.write(outputStream);
        } catch (IOException ex){
            fail(ex.toString());
        }
        InputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
        try{
            entities = (List<DynamicEntity>)context.unmarshalEntity("Person", null, MediaType.APPLICATION_JSON_TYPE, stream);
        } catch (JAXBException e){
            fail("Exception unmarsalling: " + e);
        }        
        assertNotNull("returned data was null", entities);
        assertNotNull("returned data had null list", entities);
        assertTrue("returned data had wrong list size", entities.size() == 2);
        List<String> values = new ArrayList<String>();
        values.add("James");
        values.add("Gillian");
        for (DynamicEntity value: entities){
            assertTrue("Incorrect firstName returned", value.get("firstName").equals("James") || value.get("firstName").equals("Gillian"));
            values.remove(value.get("firstName"));
        }
        assertTrue("Incorrent set of names.", values.isEmpty());
        
        clearData();
    }
    
  /*  @Test
    public void testRestart(){
        factory.close();
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties); 
        factory = null;
        try{
            factory = new PersistenceFactory();
            factory.setMetadataStore(new DatabaseMetadataStore());
            factory.getMetadataStore().setProperties(properties);
            factory.initialize(properties);
            factory.getPersistenceContext("auction").setBaseURI(new URI("http://localhost:8080/JPA-RS/"));
            factory.getPersistenceContext("phonebook").setBaseURI(new URI("http://localhost:8080/JPA-RS/"));
        } catch (Exception e){
            fail(e.toString());
        }
        assertTrue("factory was not recreated at boot time.", factory.getPersistenceContext("auction") != null);
    }*/
    
    @Test 
    public void testMarshallBid(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("auction");
        
        DynamicEntity entity1 = (DynamicEntity)context.newEntity("Auction");
        entity1.set("name", "Computer");
        context.create(null, entity1);
        
        DynamicEntity entity2 = (DynamicEntity)context.newEntity("User");
        entity2.set("name", "Bob");
        context.create(null, entity2);
        
        DynamicEntity entity3 = (DynamicEntity)context.newEntity("Bid");
        entity3.set("bid", 200d);
        entity3.set("user", entity2);
        entity3.set("auction", entity1);
        context.create(null, entity3);
        
        InputStream stream = serializeToStream(entity3, context, MediaType.APPLICATION_JSON_TYPE);

        try{
            entity3 = (DynamicEntity)context.unmarshalEntity("Bid", null, MediaType.APPLICATION_JSON_TYPE, stream);
        } catch (JAXBException e){
            fail("Exception unmarsalling: " + e);
        }
        System.out.println(entity3);
        entity2 = entity3.get("auction");

        assertNotNull("Name of auction is null.", entity2.get("name"));
        assertTrue("Name of auction is incorrect.", entity2.get("name").equals("Computer"));
        
        entity1 = entity3.get("user");
        
        assertNotNull("Name of user is null.", entity1.get("name"));
        assertTrue("Name of user is incorrect.", entity1.get("name").equals("Bob"));
    }
    
    @Test
    public void testNamedQuery(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("auction");
        
        DynamicEntity entity1 = (DynamicEntity)context.newEntity("Auction");
        entity1.set("name", "Computer");
        context.create(null, entity1);
        
        DynamicEntity entity2 = (DynamicEntity)context.newEntity("Auction");
        entity2.set("name", "Word Processor");
        context.create(null, entity2);
        
        TestHttpHeaders headers = new TestHttpHeaders();
        headers.getAcceptableMediaTypes().add(MediaType.APPLICATION_JSON_TYPE);
        List<String> mediaTypes = new ArrayList<String>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        TestURIInfo ui = new TestURIInfo();
        StreamingOutput output = (StreamingOutput)service.namedQuery("auction", "Auction.all", headers, ui).getEntity();
     
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            output.write(outputStream);
        } catch (IOException ex){
            fail(ex.toString());
        }
        String resultString = outputStream.toString();
        
        assertTrue("Computer was not in results.", resultString.contains("\"name\":\"Computer\""));
        assertTrue("Word Processor was not in restuls.", resultString.contains("\"name\":\"Word Processor\""));
        clearData();
    }
    
    @Test
    public void testNamedQuerySingleResult(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("auction");
        
        DynamicEntity entity1 = (DynamicEntity)context.newEntity("Auction");
        entity1.set("name", "Computer");
        context.create(null, entity1);
        
        DynamicEntity entity2 = (DynamicEntity)context.newEntity("Auction");
        entity2.set("name", "Word Processor");
        context.create(null, entity2);
        
        TestHttpHeaders headers = new TestHttpHeaders();
        headers.getAcceptableMediaTypes().add(MediaType.APPLICATION_JSON_TYPE);
        List<String> mediaTypes = new ArrayList<String>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        TestURIInfo ui = new TestURIInfo();
        ui.addMatrixParameter("name", "Computer");
        StreamingOutput output = (StreamingOutput)service.namedQuerySingleResult("auction", "Auction.forName", headers, ui).getEntity();
        
        String resultString = stringifyResults(output);
        
        assertTrue("Computer was not in results.", resultString.contains("\"name\":\"Computer\""));
        assertFalse("Word Processor was in results.", resultString.contains("\"name\":\"Word Processor\""));
        
        clearData();
    }
   
    @Test
    public void testUpdate(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("auction");
        
        DynamicEntity entity1 = (DynamicEntity)context.newEntity("Auction");
        entity1.set("name", "Computer");
        context.create(null, entity1);
        entity1.set("name", "Laptop");
        entity1.set("description", "Speedy");

        TestURIInfo ui = new TestURIInfo();
        ui.addMatrixParameter("name", "Computer");
        StreamingOutput output = (StreamingOutput)service.update("auction", "Auction", generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo(), serializeToStream(entity1, context, MediaType.APPLICATION_JSON_TYPE)).getEntity();

        String resultString = stringifyResults(output);
        
        assertTrue("Laptop was not in results.", resultString.contains("\"name\":\"Laptop\""));
        assertTrue("Laptop was not in results.", resultString.contains("\"description\":\"Speedy\""));
    }
    
    @Test 
    public void testMetadataQuery(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        StreamingOutput output = null;
        try{
            output = (StreamingOutput)service.getContexts(generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo()).getEntity();
        } catch (JAXBException e){
            fail("Exception: " + e);
        }
        String result = stringifyResults(output);
        assertTrue("auction was not in the results", result.contains("auction"));
        assertTrue("phonebook was not in the results", result.contains("phonebook"));
        
        output = (StreamingOutput)service.getTypes("auction", generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo()).getEntity();
        result = stringifyResults(output);
        
        assertTrue("Bid was not in the results", result.contains("Bid"));
        assertTrue("Auction was not in the results", result.contains("Auction"));
        assertTrue("User was not in the results", result.contains("User"));

    }
    
    @Test
    public void testDelete(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("auction");
        
        DynamicEntity entity1 = (DynamicEntity)context.newEntity("Auction");
        entity1.set("name", "Computer1");
        context.create(null, entity1);

        TestURIInfo ui = new TestURIInfo();
        service.delete("auction", "Auction", entity1.get("id").toString(), generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), ui);

        entity1 = (DynamicEntity)context.find("Auction", entity1.get("id"));
        
        assertTrue("Entity was not deleted.", entity1 == null);

    }
    
    @Test
    public void testWriteQuery(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("auction");
        
        DynamicEntity entity = (DynamicEntity)context.newEntity("User");
        entity.set("name", "Bob");
        context.create(null, entity);
        
        TestURIInfo ui = new TestURIInfo();
        ui.addMatrixParameter("name", "Robert");
        ui.addMatrixParameter("id", entity.get("id").toString());
        
        service.namedQueryUpdate("auction", "User.updateName", generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), ui);

        entity = (DynamicEntity)context.find("User", entity.get("id"));
        
        assertTrue("Entity was not updated.", entity.get("name").equals("Robert"));
    }
    
    @Test 
    public void testDynamicCompositeKey(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("auction");
        DynamicEntity user = (DynamicEntity)context.newEntity("User");
        user.set("name", "Wes");
        DynamicEntity address = (DynamicEntity)context.newEntity("Address");
        address.set("city", "Ottawa");
        address.set("postalCode", "a1a1a1");
        address.set("street", "Main Street");
        address.set("type", "Home");
        user.set("address", address);
        context.create(null, user);

        Response output = service.find("auction", "Address", address.get("id") + "+" + address.get("type"), generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());

        String result = stringifyResults((StreamingOutputMarshaller)output.getEntity());
        assertTrue("Type was not in the result", result.contains("Home"));
        assertTrue("Id was not in the result", result.contains(address.get("id").toString()));
    }
    
    @Test 
    public void testStaticCompositeKey(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("auction-static");
        StaticUser user = new StaticUser();
        user.setName("Wes");
        user.setId(7);
        StaticAddress address = new StaticAddress();
        address.setCity("Ottawa");
        address.setPostalCode("a1a1a1");
        address.setStreet("Main Street");
        address.setType("Home");
        user.setAddress(address);
        context.create(null, user);

        Response output = service.find("auction-static", "StaticAddress", user.getAddress().getId() + "+" + user.getAddress().getType(), generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());

        String result = stringifyResults((StreamingOutputMarshaller)output.getEntity());
        assertTrue("Type was not in the result", result.contains("Home"));
        assertTrue("Id was not in the result", result.contains(Integer.toString(user.getAddress().getId())));
    }
    
    @Test
    public void testUnmarshallNonExistantLink(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        PersistenceContext context = factory.getPersistenceContext("auction");
        LinkAdapter adapter = new LinkAdapter("http://localhost:8080/JPA-RS/", context);
        DynamicEntity entity1 = null;
        DynamicEntity entity2 = null;
        try{
            entity1 = (DynamicEntity)adapter.unmarshal("http://localhost:8080/JPA-RS/auction/entity/Auction/1");
        } catch (Exception e){
            fail(e.toString());
        }
        assertTrue("id for Auction was missing", entity1.get("id").equals(1));
        try{
            entity2 = (DynamicEntity)adapter.unmarshal("http://localhost:8080/JPA-RS/auction/entity/Address/1+Home");
        } catch (Exception e){
            fail(e.toString());
        }
        assertTrue("id for Address was missing", entity2.get("id").equals(1));
        assertTrue("type for Address was missing", entity2.get("type").equals("Home"));
    }
    
    @Test
    public void testMetadata(){
        Service service = new Service();
        service.setPersistenceFactory(factory);
        Object result = null;
        try{
            result = service.getContexts(generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());
        } catch (JAXBException e){
            fail("Exception: " + e);
        }
        result = service.getTypes("auction", generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());

        result = service.getDescriptorMetadata("auction", "Bid", generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());
    
        result = service.getQueriesMetadata("auction", generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());
    }
    
    public static String stringifyResults(StreamingOutput output){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            output.write(outputStream);
        } catch (IOException ex){
            fail(ex.toString());
        }
        return outputStream.toString();
    }
    
    public static InputStream serializeToStream(Object object, PersistenceContext context, MediaType mediaType){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            context.marshallEntity(object, mediaType, os);
        } catch (Exception e){
            e.printStackTrace();
            fail(e.toString());
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(os.toByteArray());
        return stream;
    }
    
    public static InputStream serializeListToStream(List<DynamicEntity> object, PersistenceContext context, MediaType mediaType){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            context.marshallEntity(object, mediaType, os);
        } catch (Exception e){
            e.printStackTrace();
            fail(e.toString());
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(os.toByteArray());
        return stream;
    }
    
    public static HttpHeaders generateHTTPHeader(MediaType acceptableMedia, String mediaTypeString){
        TestHttpHeaders headers = new TestHttpHeaders();
        headers.getAcceptableMediaTypes().add(acceptableMedia);
        List<String> mediaTypes = new ArrayList<String>();
        mediaTypes.add(mediaTypeString);

        headers.getRequestHeaders().put(HttpHeaders.CONTENT_TYPE, mediaTypes);
        return headers;
    }
    
    
}
