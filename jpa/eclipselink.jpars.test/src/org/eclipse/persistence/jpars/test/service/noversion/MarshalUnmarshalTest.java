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
//         gonural - Initial implementation
//         2014-09-01-2.6.0 Dmitry Kornilov
//           - Moved to separate package together with other 'no version' tests.
package org.eclipse.persistence.jpars.test.service.noversion;

import org.eclipse.persistence.exceptions.JPARSErrorCodes;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpars.test.BaseJparsTest;
import org.eclipse.persistence.jpars.test.model.auction.StaticAddress;
import org.eclipse.persistence.jpars.test.model.auction.StaticAuction;
import org.eclipse.persistence.jpars.test.model.auction.StaticBid;
import org.eclipse.persistence.jpars.test.model.auction.StaticUser;
import org.eclipse.persistence.jpars.test.server.RestCallFailedException;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * A set of marshal/unmarshal tests.
 *
 * @author gonural
 */
public class MarshalUnmarshalTest extends BaseJparsTest {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_auction-static", null);
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
    @Test
    public void testUnmarshalByReferenceNonExistingNestedObject()
            throws IOException, JAXBException {
        // Send a JSON message with links where the links point to non-existing
        // objects
        String jsonMessage = RestUtils.getJSONMessage("auction-bidsByRef.json");
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
        context.marshall(object, MediaType.APPLICATION_JSON_TYPE, os, false);
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
