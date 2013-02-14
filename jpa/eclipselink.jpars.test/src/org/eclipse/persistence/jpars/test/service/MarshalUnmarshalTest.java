/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		gonural - initial implementation
 ******************************************************************************/

package org.eclipse.persistence.jpars.test.service;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpars.test.model.auction.StaticAddress;
import org.eclipse.persistence.jpars.test.model.auction.StaticAuction;
import org.eclipse.persistence.jpars.test.model.auction.StaticBid;
import org.eclipse.persistence.jpars.test.model.auction.StaticUser;
import org.eclipse.persistence.jpars.test.server.RestCallFailedException;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class MarshalUnmarshalTest {
    private static final String DEFAULT_PU = "jpars_auction-static";
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
        properties.put(PersistenceUnitProperties.DDL_GENERATION,
                PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER,
                new DynamicClassLoader(Thread.currentThread()
                        .getContextClassLoader()));
        PersistenceFactoryBase factory = new PersistenceFactoryBase();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(
                DEFAULT_PU, properties);
        context = factory.bootstrapPersistenceContext("jpars_auction-static", emf,
                RestUtils.getServerURI(), null, false);
    }

    /**
     * Test unmarshal by ref.
     *
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUnmarshalByRef() throws JAXBException {
        EntityManager em = context.getEmf().createEntityManager();

        // Create the bids referenced by hrefs, so that unmarshal can find them
        // in PU
        em.getTransaction().begin();

        // "href":"http://localhost:9090/JPA-RS/jpars_auction-static/entity/StaticBid/1234",
        StaticBid bid1 = new StaticBid();
        bid1.setId(1234);
        bid1.setAmount(110);
        bid1.setTime(System.currentTimeMillis());
        em.persist(bid1);

        // "href":"http://localhost:9090/JPA-RS/jpars_auction-static/entity/StaticBid/5678",
        StaticBid bid2 = new StaticBid();
        bid2.setId(5678);
        bid2.setAmount(111);
        bid2.setTime(System.currentTimeMillis());
        em.persist(bid2);
        em.getTransaction().commit();

        //
        String jsonMessage = RestUtils.getJSONMessage("auction-bidsByRef.json");
        assertTrue(jsonMessage != null);
        StaticAuction auction = unmarshal(jsonMessage,
                StaticAuction.class.getSimpleName());
        assertTrue("Incorrectly unmarshalled auction.", auction.getName()
                .equals("Dora Maar au Chat"));
        List<StaticBid> bids = auction.getBids();
        assertTrue("Incorrectly unmarshalled bids.", bids.size() > 0);

        // cleanup
        dbDelete(bid1);
        dbDelete(bid2);
    }

    /**
     * Test unmarshal by value.
     *
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUnmarshalByValue() throws JAXBException {
        String jsonMessage = RestUtils.getJSONMessage("auction-bidsByValue.json");
        StaticAuction auctionUnmarshalled = unmarshal(jsonMessage,
                StaticAuction.class.getSimpleName());

        assertTrue("Unmarshal returned a null object",
                auctionUnmarshalled != null);
        List<StaticBid> bidsUnmarshalled = auctionUnmarshalled.getBids();
        assertTrue("Incorrectly unmarshalled bids.",
                bidsUnmarshalled.size() > 0);
    }

    /**
     * Test unmarshal by reference non existing nested object.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JAXBException the jAXB exception
     */
    @Test(expected = ConversionException.class)
    public void testUnmarshalByReferenceNonExistingNestedObject()
            throws IOException, JAXBException {
        // Send a JSON message with links where the links point to non-existing
        // objects
        String jsonMessage = RestUtils.getJSONMessage("auction-bidsByRef.json");
        assertTrue(jsonMessage != null);
        // unmarshall should raise ConversionException
        unmarshal(jsonMessage, StaticAuction.class.getSimpleName());
    }

    /**
     * Test marshal.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testMarshal() throws RestCallFailedException,
    UnsupportedEncodingException, JAXBException {
        StaticBid bid = new StaticBid();
        bid.setId(20);
        bid.setAmount(100.0);
        bid.setTime(System.currentTimeMillis());

        StaticUser user = new StaticUser();
        user.setId(22);
        user.setName("Brahms");

        StaticAddress address = new StaticAddress();
        address.setCity("Hamburg");
        // set pk
        address.setId(67);
        address.setType("Home");

        user.setAddress(address);
        bid.setUser(user);

        String marshalledBid = marshal(bid);
        assertTrue("Marshalling bid returned null", marshalledBid != null);

        String marshalledUser = marshal(user);
        assertTrue("Marshalling user returned null", marshalledUser != null);
    }

    @Test
    public void testUnmarshalBidWithUserByRef() throws JAXBException, RestCallFailedException, UnsupportedEncodingException {
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        StaticBid bid = new StaticBid();
        bid.setId(1001);
        bid.setAmount(5000);
        bid.setTime(System.currentTimeMillis());

        StaticUser user = new StaticUser();
        user.setId(2002);
        user.setName("User 2002");
        bid.setUser(user);
        em.persist(bid);
        em.getTransaction().commit();

        marshal(bid);

        String xmlMessage = RestUtils.getXMLMessage("bid-UserByRef.xml");
        StaticBid bidUnmarshalled = unmarshal(xmlMessage,
                StaticBid.class.getSimpleName(), MediaType.APPLICATION_XML_TYPE);

        assertTrue("Unmarshal returned a null bid", bidUnmarshalled != null);
        StaticUser userUnmarshalled = bidUnmarshalled.getUser();
        assertTrue("Unmarshal returned a null user", userUnmarshalled != null);

        dbDelete(bid);
    }

    @SuppressWarnings("unchecked")
    private static <T> T unmarshal(String msg, String type)
            throws JAXBException {
        T resultObject = null;
        resultObject = (T) context.unmarshalEntity(type,
                MediaType.APPLICATION_JSON_TYPE,
                new ByteArrayInputStream(msg.getBytes()));
        return resultObject;
    }

    @SuppressWarnings("unchecked")
    private static <T> T unmarshal(String msg, String type, MediaType mediaType)
            throws JAXBException {
        T resultObject = null;
        resultObject = (T) context.unmarshalEntity(type, mediaType,
                new ByteArrayInputStream(msg.getBytes()));
        return resultObject;
    }

    private static <T> String marshal(Object object)
            throws RestCallFailedException, JAXBException,
            UnsupportedEncodingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        context.marshallEntity(object, MediaType.APPLICATION_JSON_TYPE, os,
                false);
        return os.toString("UTF-8");
    }

    private static void dbDelete(Object object) {
        EntityManager em = context.getEmf().createEntityManager();
        em.getTransaction().begin();
        Object merged = em.merge(object);
        em.remove(merged);
        em.getTransaction().commit();
    }
}
