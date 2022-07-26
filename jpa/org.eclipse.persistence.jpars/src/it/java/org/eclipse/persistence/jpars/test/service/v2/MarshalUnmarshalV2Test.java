/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//         Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test.service.v2;

import org.eclipse.persistence.exceptions.JPARSErrorCodes;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpars.test.BaseJparsTest;
import org.eclipse.persistence.jpars.test.model.auction.StaticAddress;
import org.eclipse.persistence.jpars.test.model.auction.StaticAuction;
import org.eclipse.persistence.jpars.test.model.auction.StaticBid;
import org.eclipse.persistence.jpars.test.model.auction.StaticUser;
import org.eclipse.persistence.jpars.test.server.RestCallFailedException;
import org.eclipse.persistence.jpars.test.service.MarshalUnmarshalTestBase;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * MarshalUnmarshalTestBase adapted for JPARS 2.0.
 * {@link MarshalUnmarshalTestBase}
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class MarshalUnmarshalV2Test extends BaseJparsTest {
    protected static EntityManager em;

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_auction-static", "v2.0");
        em = context.getEmf().createEntityManager();
        initData();
    }

    /**
     * Creates test data in the database.
     */
    protected static void initData() {
        // Create the bids referenced by hrefs, so that unmarshal can find them in PU
        em.getTransaction().begin();

        // Auction 1
        StaticAuction auction = new StaticAuction();
        auction.setName("Auction 1");
        auction.setDescription("Auction 1 description");
        auction.setId(1);
        auction.setStartPrice(100.0);
        auction.setStartPrice(1000.0);
        em.persist(auction);

        // Bid 1234
        StaticBid bid = new StaticBid();
        bid.setId(1234);
        bid.setAmount(110);
        bid.setTime(System.currentTimeMillis());
        bid.setAuction(auction);
        em.persist(bid);

        // Bid 5678
        bid = new StaticBid();
        bid.setId(5678);
        bid.setAmount(111);
        bid.setTime(System.currentTimeMillis());
        bid.setAuction(auction);
        em.persist(bid);

        // User 2002
        StaticUser user = new StaticUser();
        user.setId(2002);
        user.setName("User 2002");

        // Bid 1001
        bid = new StaticBid();
        bid.setId(1001);
        bid.setAmount(5000);
        bid.setTime(System.currentTimeMillis());
        bid.setUser(user);
        em.persist(bid);

        em.getTransaction().commit();
    }

    /**
     * Deletes the test data.
     */
    @AfterClass
    public static void cleanup() {
        em.getTransaction().begin();
        em.remove(em.find(StaticBid.class, 1234));
        em.remove(em.find(StaticBid.class, 5678));
        em.remove(em.find(StaticBid.class, 1001));
        em.remove(em.find(StaticAuction.class, 1));
        em.getTransaction().commit();
    }

    /**
     * Test unmarshal by ref.
     *
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUnmarshalByRef() throws JAXBException {
        // Read JSON message from file
        String jsonMessage = RestUtils.getJSONMessage("auction-bidsByRef-V2.json");
        assertTrue(jsonMessage != null);

        StaticAuction auction = unmarshal(jsonMessage, StaticAuction.class.getSimpleName());
        assertTrue("Incorrectly unmarshalled auction.", auction.getName().equals("Dora Maar au Chat"));

        List<StaticBid> bids = auction.getBids();
        assertTrue("Incorrectly unmarshalled bids.", bids.size() == 2);
        assertTrue(bids.get(0).getId() == 1234);
        assertTrue(bids.get(1).getId() == 5678);
    }

    /**
     * Test unmarshal by value.
     *
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUnmarshalByValue() throws JAXBException {
        final String jsonMessage = RestUtils.getJSONMessage("auction-bidsByValue-V2.json");
        final StaticAuction auction = unmarshal(jsonMessage, StaticAuction.class.getSimpleName());
        assertTrue("Unmarshal returned a null object", auction != null);

        final List<StaticBid> bids = auction.getBids();
        assertTrue("Incorrectly unmarshalled bids.", bids.size() == 3);
        assertTrue(bids.get(0).getId() == 333);
        assertTrue(bids.get(1).getId() == 444);
        assertTrue(bids.get(2).getId() == 555);
    }

    /**
     * Test unmarshal by reference non existing nested object.
     *
     * @throws java.io.IOException Signals that an I/O exception has occurred.
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUnmarshalByReferenceNonExistingNestedObject() throws IOException, JAXBException {
        // Send a JSON message with links where the links point to non-existing objects
        final String jsonMessage = RestUtils.getJSONMessage("auction-bidsByRef-V2.json");
        assertTrue(jsonMessage != null);
        try {
            unmarshal(jsonMessage, StaticAuction.class.getSimpleName());
        } catch (JPARSException ex) {
            assertTrue(ex.getErrorCode() == JPARSErrorCodes.OBJECT_REFERRED_BY_LINK_DOES_NOT_EXIST);
        }
    }

    /**
     * Test marshal.
     *
     * @throws org.eclipse.persistence.jpars.test.server.RestCallFailedException the rest call failed exception
     * @throws java.io.UnsupportedEncodingException the unsupported encoding exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testMarshalAuction() throws RestCallFailedException, UnsupportedEncodingException, JAXBException {
        final StaticAuction auction = new StaticAuction();
        auction.setId(1);
        auction.setDescription("Test Auction Description");
        auction.setName("Test Auction");

        final List<StaticBid> bids = new ArrayList<>(5);
        for (int i=1; i<5; i++) {
            final StaticBid bid = new StaticBid();
            bid.setId(i);
            bid.setAmount(100.0 + i);
            bid.setTime(System.currentTimeMillis());
            bids.add(bid);
        }
        auction.setBids(bids);

        final String marshalledAuction = marshal(auction, MediaType.APPLICATION_XML_TYPE);
        logger.info(marshalledAuction);
        assertTrue("Marshalling auction returned null", marshalledAuction != null);

        final StaticAuction unmarshalledAuction = unmarshal(marshalledAuction, StaticAuction.class.getSimpleName(), MediaType.APPLICATION_XML_TYPE);
        assertTrue(unmarshalledAuction != null);
    }

    @Test
    public void testMarshal() throws RestCallFailedException, UnsupportedEncodingException, JAXBException {
        final StaticBid bid = new StaticBid();
        bid.setId(20);
        bid.setAmount(100.0);
        bid.setTime(System.currentTimeMillis());

        final StaticUser user = new StaticUser();
        user.setId(22);
        user.setName("Brahms");

        final StaticAddress address = new StaticAddress();
        address.setCity("Hamburg");

        // set pk
        address.setId(67);
        address.setType("Home");

        user.setAddress(address);
        bid.setUser(user);

        final String marshalledBid = marshal(bid, MediaType.APPLICATION_XML_TYPE);
        logger.info(marshalledBid);
        assertTrue("Marshalling bid returned null", marshalledBid != null);

        final String marshalledUser = marshal(user, MediaType.APPLICATION_XML_TYPE);
        logger.info(marshalledUser);
        assertTrue("Marshalling user returned null", marshalledUser != null);
    }

    @Test
    public void testUnmarshalBidWithUserByRef() throws JAXBException, RestCallFailedException, UnsupportedEncodingException {
        final String xmlMessage = RestUtils.getXMLMessage("bid-UserByRef-V2.xml");
        final StaticBid bid = unmarshal(xmlMessage, StaticBid.class.getSimpleName(), MediaType.APPLICATION_XML_TYPE);
        final StaticUser user = bid.getUser();

        assertTrue("Unmarshal returned a null bid", bid != null);
        assertTrue(bid.getId() == 1001);
        assertTrue("Unmarshal returned a null user", user != null);
        assertTrue(user.getId() == 2002);
    }

    @SuppressWarnings("unchecked")
    private static <T> T unmarshal(String msg, String type) throws JAXBException {
        T resultObject = (T) context.unmarshalEntity(type, MediaType.APPLICATION_JSON_TYPE, new ByteArrayInputStream(msg.getBytes()));
        return resultObject;
    }

    @SuppressWarnings("unchecked")
    private static <T> T unmarshal(String msg, String type, MediaType mediaType) throws JAXBException {
        T resultObject = (T) context.unmarshalEntity(type, mediaType, new ByteArrayInputStream(msg.getBytes()));
        return resultObject;
    }

    private static String marshal(Object object) throws RestCallFailedException, JAXBException, UnsupportedEncodingException {
        return marshal(object, MediaType.APPLICATION_JSON_TYPE);
    }

    private static String marshal(Object object, MediaType mediaType) throws RestCallFailedException, JAXBException, UnsupportedEncodingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        context.marshall(object, mediaType, os, false);
        return os.toString("UTF-8");
    }
}
