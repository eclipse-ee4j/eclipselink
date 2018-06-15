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
//      tware - Initial implementation
package org.eclipse.persistence.jpars.test.service.noversion;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.resources.unversioned.EntityResource;
import org.eclipse.persistence.jpa.rs.resources.unversioned.PersistenceResource;
import org.eclipse.persistence.jpa.rs.resources.unversioned.PersistenceUnitResource;
import org.eclipse.persistence.jpa.rs.resources.unversioned.QueryResource;
import org.eclipse.persistence.jpa.rs.resources.unversioned.SingleResultQueryResource;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.jpa.rs.util.xmladapters.LinkAdapter;
import org.eclipse.persistence.jpars.test.model.auction.StaticBid;
import org.eclipse.persistence.jpars.test.model.auction.StaticUser;
import org.eclipse.persistence.jpars.test.model.multitenant.Account;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.eclipse.persistence.jpars.test.util.StaticModelDatabasePopulator;
import org.eclipse.persistence.jpars.test.util.TestHttpHeaders;
import org.eclipse.persistence.jpars.test.util.TestURIInfo;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the JPA RS resource classes
 *
 * @author tware
 */
public class TestService {

    private static PersistenceFactoryBase factory;
    private static PersistenceContext context;

    public static PersistenceContext getAuctionPersistenceContext(Map<String, Object> additionalProperties) throws URISyntaxException {
        Map<String, Object> properties = new HashMap<>();
        if (additionalProperties != null) {
            properties.putAll(additionalProperties);
        }
        properties.put(PersistenceUnitProperties.WEAVING, "static");
        return factory.get("jpars_auction", RestUtils.getServerURI(), null, properties);
    }

    public static PersistenceContext getPhoneBookPersistenceContext(Map<String, Object> additionalProperties) throws URISyntaxException {
        Map<String, Object> properties = new HashMap<>();
        if (additionalProperties != null) {
            properties.putAll(additionalProperties);
        }
        properties.put(PersistenceUnitProperties.WEAVING, "static");
        return factory.get("jpars_phonebook", RestUtils.getServerURI(), null, properties);
    }

    @BeforeClass
    public static void setup() {
        Map<String, Object> properties = new HashMap<>();
        ExamplePropertiesLoader.loadProperties(properties);
        factory = null;
        try {
            factory = new PersistenceFactoryBase();

            properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
            properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
            //properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
            properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));

            getAuctionPersistenceContext(properties);

            getPhoneBookPersistenceContext(properties);

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpars_auction-static-local", properties);
            context = factory.bootstrapPersistenceContext("jpars_auction-static-local", emf, RestUtils.getServerURI(), null, false);
            if (context == null) {
                throw new Exception("Persistence context could not be created.");
            }

            StaticModelDatabasePopulator.populateDB(emf);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @AfterClass
    public static void teardown() throws URISyntaxException {
        clearData();
    }

    protected static void clearData() throws URISyntaxException {
        EntityManager em = getAuctionPersistenceContext(null).getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from Bid b").executeUpdate();
        em.createQuery("delete from Auction a").executeUpdate();
        em.createQuery("delete from User u").executeUpdate();

        em.getTransaction().commit();
        em = getPhoneBookPersistenceContext(null).getEmf().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from Person p").executeUpdate();
        em.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateUserList() throws Exception {
        EntityResource resource = new EntityResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = TestService.getAuctionPersistenceContext(null);

        DynamicEntity entity = context.newEntity("User");
        entity.set("name", "Jim");
        context.create(null, entity);

        entity.set("name", "James");

        DynamicEntity entity2 = context.newEntity("User");
        entity2.set("name", "Jill");
        context.create(null, entity2);

        entity2.set("name", "Gillian");

        List<DynamicEntity> entities = new ArrayList<>();
        entities.add(entity);
        entities.add(entity2);

        StreamingOutput output = (StreamingOutput) resource.update("jpars_auction", "User", TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo(),
                TestService.serializeListToStream(entities, context, MediaType.APPLICATION_JSON_TYPE)).getEntity();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            output.write(outputStream);
        } catch (IOException ex) {
            fail(ex.toString());
        }
        InputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
        try {
            entities = (List<DynamicEntity>) context.unmarshalEntity("User", MediaType.APPLICATION_JSON_TYPE, stream);
        } catch (JAXBException e) {
            fail("Exception unmarsalling: " + e);
        }
        assertNotNull("returned data was null", entities);
        assertTrue("returned data had wrong list size", entities.size() == 2);
        List<String> values = new ArrayList<>();
        values.add("James");
        values.add("Gillian");
        for (DynamicEntity value : entities) {
            ((FetchGroupTracker) value)._persistence_setSession(context.getServerSession());
            assertTrue("Incorrect name returned", value.get("name").equals("James") || value.get("name").equals("Gillian"));
            values.remove(value.get("name"));
        }
        assertTrue("Incorrent set of names.", values.isEmpty());

        clearData();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdatePhoneNumberList() throws Exception {
        EntityResource resource = new EntityResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = getPhoneBookPersistenceContext(null);

        DynamicEntity entity = context.newEntity("Person");
        entity.set("firstName", "Jim");
        entity.set("lastName", "Jones");
        entity.set("phoneNumber", "1234567");
        context.create(null, entity);

        entity.set("firstName", "James");

        DynamicEntity entity2 = context.newEntity("Person");
        entity2.set("firstName", "Jill");
        entity2.set("lastName", "Jones");
        context.create(null, entity2);

        entity2.set("firstName", "Gillian");

        List<DynamicEntity> entities = new ArrayList<>();
        entities.add(entity);
        entities.add(entity2);

        StreamingOutput output = (StreamingOutput) resource.update("jpars_phonebook", "Person", TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo(),
                serializeListToStream(entities, context, MediaType.APPLICATION_JSON_TYPE)).getEntity();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            output.write(outputStream);
        } catch (IOException ex) {
            fail(ex.toString());
        }
        InputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
        try {
            entities = (List<DynamicEntity>) context.unmarshalEntity("Person", MediaType.APPLICATION_JSON_TYPE, stream);
        } catch (JAXBException e) {
            fail("Exception unmarsalling: " + e);
        }
        assertNotNull("returned data was null", entities);
        assertNotNull("returned data had null list", entities);
        assertTrue("returned data had wrong list size", entities.size() == 2);
        List<String> values = new ArrayList<>();
        values.add("James");
        values.add("Gillian");
        for (DynamicEntity value : entities) {
            ((FetchGroupTracker) value)._persistence_setSession(context.getServerSession());
            assertTrue("Incorrect firstName returned", value.get("firstName").equals("James") || value.get("firstName").equals("Gillian"));
            values.remove(value.get("firstName"));
        }
        assertTrue("Incorrent set of names.", values.isEmpty());

        clearData();
    }

    @Test
    public void testMarshallBid() throws Exception {
        PersistenceResource resource = new PersistenceResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = getAuctionPersistenceContext(null);

        DynamicEntity entity1 = context.newEntity("Auction");
        entity1.set("name", "Computer");

        DynamicEntity entity2 = context.newEntity("User");
        entity2.set("name", "Bob");
        context.create(null, entity2);

        DynamicEntity entity3 = context.newEntity("Bid");
        entity3.set("amount", 200d);
        entity3.set("user", entity2);
        entity3.set("auction", entity1);
        context.create(null, entity3);

        InputStream stream = serializeToStream(entity3, context, MediaType.APPLICATION_JSON_TYPE);

        try {
            entity3 = (DynamicEntity) context.unmarshalEntity("Bid", MediaType.APPLICATION_JSON_TYPE, stream);
        } catch (JAXBException e) {
            fail("Exception unmarsalling: " + e);
        }
        (new FetchGroupManager()).setObjectFetchGroup(entity3, null, null);
        entity1 = entity3.get("auction");

        assertNotNull("Name of auction is null.", entity1.get("name"));
        assertTrue("Name of auction is incorrect.", entity1.get("name").equals("Computer"));

    }

    @Test
    public void testNamedQuery() throws Exception {
        QueryResource resource = new QueryResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = getAuctionPersistenceContext(null);

        DynamicEntity entity1 = context.newEntity("Auction");
        entity1.set("name", "Computer");
        context.create(null, entity1);

        DynamicEntity entity2 = context.newEntity("Auction");
        entity2.set("name", "Word Processor");
        context.create(null, entity2);

        TestHttpHeaders headers = new TestHttpHeaders();
        headers.getAcceptableMediaTypes().add(MediaType.APPLICATION_JSON_TYPE);
        List<String> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        TestURIInfo ui = new TestURIInfo();
        StreamingOutput output = (StreamingOutput) resource.namedQuery("jpars_auction", "Auction.all", headers, ui).getEntity();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            output.write(outputStream);
        } catch (IOException ex) {
            fail(ex.toString());
        }
        String resultString = outputStream.toString();

        assertTrue("Computer was not in results.", resultString.contains("\"name\":\"Computer\""));
        assertTrue("Word Processor was not in restuls.", resultString.contains("\"name\":\"Word Processor\""));
        clearData();
    }

    @Test
    public void testNamedQuerySingleResult() throws Exception {
        SingleResultQueryResource resource = new SingleResultQueryResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = getAuctionPersistenceContext(null);

        DynamicEntity entity1 = context.newEntity("Auction");
        entity1.set("name", "Computer");
        context.create(null, entity1);

        DynamicEntity entity2 = context.newEntity("Auction");
        entity2.set("name", "Word Processor");
        context.create(null, entity2);

        TestHttpHeaders headers = new TestHttpHeaders();
        headers.getAcceptableMediaTypes().add(MediaType.APPLICATION_JSON_TYPE);
        List<String> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        TestURIInfo ui = new TestURIInfo();
        ui.addMatrixParameter("Auction.forName", "name", "Computer");
        StreamingOutput output = (StreamingOutput) resource.namedQuerySingleResult("jpars_auction", "Auction.forName", headers, ui).getEntity();

        String resultString = stringifyResults(output);

        assertTrue("Computer was not in results.", resultString.contains("\"name\":\"Computer\""));
        assertFalse("Word Processor was in results.", resultString.contains("\"name\":\"Word Processor\""));

        clearData();
    }

    @Test
    public void testUpdate() throws Exception {
        EntityResource resource = new EntityResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = getAuctionPersistenceContext(null);

        DynamicEntity entity1 = context.newEntity("Auction");
        entity1.set("name", "Computer");
        context.create(null, entity1);
        entity1.set("name", "Laptop");
        entity1.set("description", "Speedy");

        StreamingOutput output = (StreamingOutput) resource.update("jpars_auction", "Auction", TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo(),
                serializeToStream(entity1, context, MediaType.APPLICATION_JSON_TYPE)).getEntity();

        String resultString = stringifyResults(output);

        assertTrue("Laptop was not in results.", resultString.contains("\"name\":\"Laptop\""));
        assertTrue("Laptop was not in results.", resultString.contains("\"description\":\"Speedy\""));
    }

    @Test
    public void testUpdateRelationship() throws Exception {
        EntityResource resource = new EntityResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = getAuctionPersistenceContext(null);

        DynamicEntity entity1 = context.newEntity("Auction");
        entity1.set("name", "Computer");

        DynamicEntity entity2 = context.newEntity("User");
        entity2.set("name", "Bob");
        context.create(null, entity2);

        DynamicEntity entity3 = context.newEntity("Bid");
        entity3.set("amount", 200d);
        entity3.set("user", entity2);
        entity3.set("auction", entity1);
        context.create(null, entity3);

        entity3 = (DynamicEntity) context.find(null, "Bid", entity3.get("id"), null);

        entity3.set("amount", 201d);
        entity1 = entity3.get("auction");
        entity1.set("name", "Laptop");
        StreamingOutput output = (StreamingOutput) resource.update("jpars_auction", "Bid", TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo(),
                serializeToStream(entity3, context, MediaType.APPLICATION_JSON_TYPE)).getEntity();

        String resultString = stringifyResults(output);

        assertTrue("amount was not in results.", resultString.replace(" ", "").contains("\"amount\":201.0"));
        String uri = RestUtils.getServerURI() + "jpars_auction/entity/Bid/";
        assertTrue("link was not in results.", resultString.replace(" ", "").contains(uri));
        assertTrue("rel was not in results.", resultString.replace(" ", "").contains("\"rel\":\"user\""));
        assertTrue("Laptop was not a link in results.", !resultString.replace(" ", "").contains("\"name\":\"Laptop\""));

    }

    @Test
    public void testMetadataQuery() {
        PersistenceResource resource = new PersistenceResource();
        resource.setPersistenceFactory(factory);
        StreamingOutput output = null;
        try {
            output = (StreamingOutput) resource.getContexts(TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo()).getEntity();
        } catch (JAXBException e) {
            fail("Exception: " + e);
        }
        String result = stringifyResults(output);
        assertTrue("auction was not in the results", result.contains("jpars_auction"));
        assertTrue("phonebook was not in the results", result.contains("jpars_phonebook"));
    }

    @Test
    public void testTypes() {
        PersistenceUnitResource resource = new PersistenceUnitResource();
        resource.setPersistenceFactory(factory);
        StreamingOutput output = null;
        output = (StreamingOutput) resource.getTypes("jpars_auction", TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo()).getEntity();
        String result = stringifyResults(output);

        assertTrue("Bid was not in the results", result.contains("Bid"));
        assertTrue("Auction was not in the results", result.contains("Auction"));
        assertTrue("User was not in the results", result.contains("User"));
    }

    @Test
    public void testDelete() throws Exception {
        EntityResource resource = new EntityResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = getAuctionPersistenceContext(null);

        DynamicEntity entity1 = context.newEntity("Auction");
        entity1.set("name", "Computer1");
        context.create(null, entity1);

        TestURIInfo ui = new TestURIInfo();
        resource.delete("jpars_auction", "Auction", entity1.get("id").toString(), TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), ui);

        entity1 = (DynamicEntity) context.find("Auction", entity1.get("id"));

        assertTrue("Entity was not deleted.", entity1 == null);

    }

    @Test
    public void testWriteQuery() throws Exception {
        QueryResource resource = new QueryResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = getAuctionPersistenceContext(null);

        DynamicEntity entity = context.newEntity("User");
        entity.set("name", "Bob");
        context.create(null, entity);

        TestURIInfo ui = new TestURIInfo();
        ui.addMatrixParameter("User.updateName", "name", "Robert");
        ui.addMatrixParameter("User.updateName", "id", entity.get("id").toString());

        resource.namedQueryUpdate("jpars_auction", "User.updateName", TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), ui);

        entity = (DynamicEntity) context.find("User", entity.get("id"));

        assertTrue("Entity was not updated.", entity.get("name").equals("Robert"));
    }

    @Test
    public void testDynamicCompositeKey() throws Exception {
        EntityResource resource = new EntityResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = getAuctionPersistenceContext(null);
        DynamicEntity user = context.newEntity("User");
        user.set("name", "Wes");
        DynamicEntity address = context.newEntity("Address");
        address.set("city", "Ottawa");
        address.set("postalCode", "a1a1a1");
        address.set("street", "Main Street");
        address.set("type", "Home");
        user.set("address", address);
        context.create(null, user);

        Response output = resource.find("jpars_auction", "Address", address.get("id") + "+" + address.get("type"), TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON),
                new TestURIInfo());

        String result = stringifyResults((StreamingOutputMarshaller) output.getEntity());
        assertTrue("Type was not in the result", result.contains("Home"));
        assertTrue("Id was not in the result", result.contains(address.get("id").toString()));
    }

    @Test
    public void testStaticCompositeKey() {
        EntityResource resource = new EntityResource();
        resource.setPersistenceFactory(factory);

        Response output = resource.find("jpars_auction-static-local", "StaticAddress", StaticModelDatabasePopulator.ADDRESS1_ID + "+" + StaticModelDatabasePopulator.address1().getType(),
                TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());

        String result = stringifyResults((StreamingOutputMarshaller) output.getEntity());
        assertTrue("home was not in the result", result.contains("home"));
        assertTrue("user was not in the result", result.contains("\"rel\":\"user\""));
    }

    @Test
    public void testStaticRelationships() {
        EntityResource resource = new EntityResource();
        resource.setPersistenceFactory(factory);

        Response output = resource.find("jpars_auction-static-local", "StaticBid", String.valueOf(StaticModelDatabasePopulator.BID1_ID),
                TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());

        String result = stringifyResults((StreamingOutputMarshaller) output.getEntity());
        assertTrue("Auction was not in the result", result.contains("\"rel\":\"auction\""));
        assertTrue("User link was not in the result", result.contains("\"rel\":\"user\""));
    }

    @Test
    public void testUpdateStaticRelationship() throws URISyntaxException {
        EntityResource resource = new EntityResource();
        resource.setPersistenceFactory(factory);
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        PersistenceContext context = factory.get("jpars_auction-static-local", RestUtils.getServerURI(), null, properties);

        StaticUser initialUser = ((StaticBid) context.find("StaticBid", StaticModelDatabasePopulator.BID1_ID)).getUser();
        StaticUser user2 = (StaticUser) context.find("StaticUser", StaticModelDatabasePopulator.USER2_ID);

        try {
            Response output = resource.setOrAddAttribute("jpars_auction-static-local", "StaticBid", String.valueOf(StaticModelDatabasePopulator.BID1_ID), "user",
                    TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo(), serializeToStream(user2, context, MediaType.APPLICATION_JSON_TYPE));

            stringifyResults((StreamingOutputMarshaller) output.getEntity());
            context.getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();

            StaticBid bid = (StaticBid) context.find("StaticBid", StaticModelDatabasePopulator.BID1_ID);

            assertTrue(bid.getUser().equals(user2));
        } finally {

            context.updateOrAddAttribute(null, "StaticBid", StaticModelDatabasePopulator.BID1_ID, null, "user", initialUser, null);
        }
    }

    @Test
    public void testStaticReportQuery() throws URISyntaxException {
        QueryResource resource = new QueryResource();
        resource.setPersistenceFactory(factory);
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        @SuppressWarnings("unused")
        PersistenceContext context = factory.get("jpars_auction-static-local", RestUtils.getServerURI(), null, properties);

        TestHttpHeaders headers = new TestHttpHeaders();
        headers.getAcceptableMediaTypes().add(MediaType.APPLICATION_JSON_TYPE);
        List<String> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        TestURIInfo ui = new TestURIInfo();
        StreamingOutput output = (StreamingOutput) resource.namedQuery("jpars_auction-static-local", "User.count", headers, ui).getEntity();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            output.write(outputStream);
        } catch (IOException ex) {
            fail(ex.toString());
        }
        String resultString = outputStream.toString();

        assertTrue("Incorrect result", resultString.contains("[{\"COUNT\":3}]"));
        clearData();
    }

    @Test
    public void testUnmarshallNonExistantLink() throws URISyntaxException {
        PersistenceResource resource = new PersistenceResource();
        resource.setPersistenceFactory(factory);
        PersistenceContext context = getAuctionPersistenceContext(null);
        LinkAdapter adapter = new LinkAdapter("http://localhost:9090/JPA-RS/", context);
        DynamicEntity entity1 = null;
        DynamicEntity entity2 = null;
        try {
            entity1 = (DynamicEntity) adapter.unmarshal("http://localhost:9090/JPA-RS/jpars_auction/entity/Auction/1");
        } catch (Exception e) {
            fail(e.toString());
        }
        assertTrue("id for Auction was missing", entity1.get("id").equals(1));
        try {
            entity2 = (DynamicEntity) adapter.unmarshal("http://localhost:9090/JPA-RS/jpars_auction/entity/Address/1+Home");
        } catch (Exception e) {
            fail(e.toString());
        }
        assertTrue("id for Address was missing", entity2.get("id").equals(1));
        assertTrue("type for Address was missing", entity2.get("type").equals("Home"));
    }

    @Test
    public void testMetadata() {
        PersistenceResource resource = new PersistenceResource();
        resource.setPersistenceFactory(factory);
        try {
            resource.getContexts(TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());
        } catch (JAXBException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testMetadataTypes() {
        PersistenceUnitResource resource = new PersistenceUnitResource();
        resource.setPersistenceFactory(factory);
        resource.getTypes("jpars_auction", TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());
        resource.getDescriptorMetadata("jpars_auction", "Bid", TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());
        resource.getQueriesMetadata("jpars_auction", TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), new TestURIInfo());
    }

    @Test(expected = JPARSException.class)
    public void testMultitenant() throws JAXBException, URISyntaxException {
        EntityResource resource = new EntityResource();
        resource.setPersistenceFactory(factory);
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        PersistenceContext context = factory.get("jpars_auction-static-local", RestUtils.getServerURI(), null, properties);

        Account account = new Account();
        account.setAccountNumber("AAA111");
        TestURIInfo ui = new TestURIInfo();
        ui.addMatrixParameter("jpars_auction-static-local", "tenant.id", "AcctOwner1");

        StreamingOutput output = (StreamingOutput) resource.update("jpars_auction-static-local", "Account", TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), ui,
                serializeToStream(account, context, MediaType.APPLICATION_JSON_TYPE)).getEntity();

        String result = stringifyResults(output);

        account = (Account) context.unmarshal(Account.class, MediaType.APPLICATION_JSON_TYPE, new ByteArrayInputStream(result.getBytes()));

        output = (StreamingOutput) resource.find("jpars_auction-static-local", "Account", String.valueOf(account.getId()),
                TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), ui).getEntity();
        result = stringifyResults(output);
        account = (Account) context.unmarshal(Account.class, MediaType.APPLICATION_JSON_TYPE, new ByteArrayInputStream(result.getBytes()));

        assertTrue("account is null", account != null);
        TestURIInfo ui2 = new TestURIInfo();
        ui2.addMatrixParameter("jpars_auction-static-local", "tenant.id", "AcctOwner2");

        output = (StreamingOutput) resource.find("jpars_auction-static-local", "Account", String.valueOf(account.getId()),
                TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON), ui2).getEntity();
    }

    public static String stringifyResults(StreamingOutput output) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            output.write(outputStream);
        } catch (IOException ex) {
            fail(ex.toString());
        }
        return outputStream.toString();
    }

    public static InputStream serializeToStream(Object object, PersistenceContext context, MediaType mediaType) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            context.marshallEntity(object, mediaType, os);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
        return new ByteArrayInputStream(os.toByteArray());
    }

    public static InputStream serializeListToStream(List<DynamicEntity> object, PersistenceContext context, MediaType mediaType) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            context.marshallEntity(object, mediaType, os);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
        return new ByteArrayInputStream(os.toByteArray());
    }
}
