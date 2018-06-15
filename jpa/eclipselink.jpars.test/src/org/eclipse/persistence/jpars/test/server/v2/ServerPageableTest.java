/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - initial implementation, upgrade to Jersey 2.x
package org.eclipse.persistence.jpars.test.server.v2;

import org.eclipse.persistence.jpars.test.BaseJparsTest;
import org.eclipse.persistence.jpars.test.model.basket.Basket;
import org.eclipse.persistence.jpars.test.model.basket.BasketItem;
import org.eclipse.persistence.jpars.test.server.RestCallFailedException;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This class tests paging feature introduced in V2.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ServerPageableTest extends BaseJparsTest {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_basket-static", "v2.0");
        initData();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        RestUtils.restUpdateQuery(context, "BasketItem.deleteAll", null, null, MediaType.APPLICATION_JSON_TYPE);
        RestUtils.restUpdateQuery(context, "Basket.deleteAll", null, null, MediaType.APPLICATION_JSON_TYPE);
    }

    protected static void initData() throws Exception {
        // Create a basket
        Basket basket = new Basket();
        basket.setId(1);
        basket.setName("Basket1");
        basket = RestUtils.restCreate(context, basket, Basket.class);
        assertNotNull("Basket create failed.", basket);

        // Add items
        for (int j=1; j<=5; j++) {
            BasketItem basketItem = new BasketItem();
            basketItem.setId(j);
            basketItem.setName("BasketItem" + j);
            //RestUtils.restCreate(context, basketItem, BasketItem.class, null, MediaType.APPLICATION_XML_TYPE, false);
            RestUtils.restCreate(context, basketItem, BasketItem.class);
            RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(basket.getId()), Basket.class, "basketItems", basketItem, MediaType.APPLICATION_XML_TYPE, "basket", true);
        }
    }

    @Test
    public void testPageableQueryLimitJson() throws URISyntaxException {
        // Run pageable query with limit = 2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("limit", "2");

        final String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_JSON_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // First 2 items must be in the response
        assertTrue(basketItemExists(queryResult, 1));
        assertTrue(basketItemExists(queryResult, 2));

        // Last 3 must not be in the response
        assertFalse(basketItemExists(queryResult, 3));
        assertFalse(basketItemExists(queryResult, 4));
        assertFalse(basketItemExists(queryResult, 5));

        // Check links
        assertTrue(checkLinkJson(queryResult, "next", "/query/BasketItem.findAllPageable?offset=2&limit=2")
            || checkLinkJson(queryResult, "next", "/query/BasketItem.findAllPageable?limit=2&offset=2"));
        assertFalse(queryResult.contains("\"rel\": \"prev\""));
        assertTrue(checkLinkJson(queryResult, "self", "/query/BasketItem.findAllPageable?limit=2"));

        // Check items (limit = 2, offset = 0, count = 2, hasMore = true)
        checkItemsJson(queryResult, 2, 0, 2, true);
    }

    @Test
    public void testPageableQueryLimitXml() throws URISyntaxException {
        // Run pageable query with limit = 2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("limit", "2");

        final String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_XML_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // First 2 items must be in the response
        assertTrue(basketItemExists(queryResult, 1));
        assertTrue(basketItemExists(queryResult, 2));

        // Last 3 must not be in the response
        assertFalse(basketItemExists(queryResult, 3));
        assertFalse(basketItemExists(queryResult, 4));
        assertFalse(basketItemExists(queryResult, 5));

        // Check links
        assertTrue(checkLinkXml(queryResult, "next", "/query/BasketItem.findAllPageable?offset=2&amp;limit=2")
            || checkLinkXml(queryResult, "next", "/query/BasketItem.findAllPageable?limit=2&amp;offset=2"));
        assertFalse(queryResult.contains("<rel>prev</rel>"));
        assertTrue(checkLinkXml(queryResult, "self", "/query/BasketItem.findAllPageable?limit=2"));

        // Check items (limit = 2, offset = 0, count = 2, hasMore = true)
        checkItemsXml(queryResult, 2, 0, 2, true);
    }

    @Test
    public void testPageableQueryOffsetJson() throws URISyntaxException {
        // Run pageable query with limit = 2 and offset = 2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("limit", "2");
        hints.put("offset", "2");

        final String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_JSON_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // First 2 items must not be in the response
        assertFalse(basketItemExists(queryResult, 1));
        assertFalse(basketItemExists(queryResult, 2));

        // The following 2 must be there
        assertTrue(basketItemExists(queryResult, 3));
        assertTrue(basketItemExists(queryResult, 4));

        // And the last one not
        assertFalse(basketItemExists(queryResult, 5));

        // Check links
        assertTrue(checkLinkJson(queryResult, "next", "/query/BasketItem.findAllPageable?offset=4&limit=2")
            || checkLinkJson(queryResult, "next", "/query/BasketItem.findAllPageable?limit=2&offset=4"));
        assertTrue(checkLinkJson(queryResult, "prev", "/query/BasketItem.findAllPageable?offset=0&limit=2")
            || checkLinkJson(queryResult, "prev", "/query/BasketItem.findAllPageable?limit=2&offset=0"));
        assertTrue(checkLinkJson(queryResult, "self", "/query/BasketItem.findAllPageable?limit=2&offset=2")
            || checkLinkJson(queryResult, "self", "/query/BasketItem.findAllPageable?offset=2&limit=2"));

        // Check items (limit = 2, offset = 2, count = 2, hasMore = true)
        checkItemsJson(queryResult, 2, 2, 2, true);
    }

    @Test
    public void testPageableQueryOffsetXml() throws URISyntaxException {
        // Run pageable query with limit = 2 and offset = 2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("limit", "2");
        hints.put("offset", "2");

        final String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_XML_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // First 2 items must not be in the response
        assertFalse(basketItemExists(queryResult, 1));
        assertFalse(basketItemExists(queryResult, 2));

        // The following 2 must be there
        assertTrue(basketItemExists(queryResult, 3));
        assertTrue(basketItemExists(queryResult, 4));

        // And the last one not
        assertFalse(basketItemExists(queryResult, 5));

        // Check links
        assertTrue(checkLinkXml(queryResult, "next", "/query/BasketItem.findAllPageable?offset=4&amp;limit=2")
            || checkLinkXml(queryResult, "next", "/query/BasketItem.findAllPageable?limit=2&amp;offset=4"));
        assertTrue(checkLinkXml(queryResult, "prev", "/query/BasketItem.findAllPageable?offset=0&amp;limit=2")
            || checkLinkXml(queryResult, "prev", "/query/BasketItem.findAllPageable?limit=2&amp;offset=0"));
        assertTrue(checkLinkXml(queryResult, "self", "/query/BasketItem.findAllPageable?limit=2&amp;offset=2")
            || checkLinkXml(queryResult, "self", "/query/BasketItem.findAllPageable?offset=2&amp;limit=2"));

        // Check items (limit = 2, offset = 2, count = 2, hasMore = true)
        checkItemsXml(queryResult, 2, 2, 2, true);
    }

    @Test
    public void testPageableQueryOffsetGtCount() throws URISyntaxException {
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("offset", "10");

        final String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_JSON_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // Empty response
        assertFalse(basketItemExists(queryResult, 1));
        assertFalse(basketItemExists(queryResult, 2));
        assertFalse(basketItemExists(queryResult, 3));
        assertFalse(basketItemExists(queryResult, 4));
        assertFalse(basketItemExists(queryResult, 5));

        // Check links
        assertFalse(checkLinkJson(queryResult, "next"));
        assertFalse(checkLinkJson(queryResult, "prev"));
        assertTrue(checkLinkJson(queryResult, "self", "/query/BasketItem.findAllPageable?offset=10"));

        // Check items (limit = 20, offset = 10, count = 0, hasMore = false)
        checkItemsJson(queryResult, 20, 10, 0, false);
    }

    @Test
    public void testNoLimitJson() throws URISyntaxException {
        final String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, null, MediaType.APPLICATION_JSON_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // All items must be in the response
        assertTrue(basketItemExists(queryResult, 1));
        assertTrue(basketItemExists(queryResult, 2));
        assertTrue(basketItemExists(queryResult, 3));
        assertTrue(basketItemExists(queryResult, 4));
        assertTrue(basketItemExists(queryResult, 5));

        // Check links
        assertFalse(queryResult.contains("\"rel\": \"next\""));
        assertFalse(queryResult.contains("\"rel\": \"prev\""));
        assertTrue(checkLinkJson(queryResult, "self", "/query/BasketItem.findAllPageable"));

        // Check items (limit = 20, offset = 0, count = 5, hasMore = false)
        checkItemsJson(queryResult, 20, 0, 5, false);
    }

    @Test
    public void testNoLimitXml() throws URISyntaxException {
        final String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, null, MediaType.APPLICATION_XML_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // All items must be in the response
        assertTrue(basketItemExists(queryResult, 1));
        assertTrue(basketItemExists(queryResult, 2));
        assertTrue(basketItemExists(queryResult, 3));
        assertTrue(basketItemExists(queryResult, 4));
        assertTrue(basketItemExists(queryResult, 5));

        // Check links
        assertFalse(queryResult.contains("<rel>next</rel>"));
        assertFalse(queryResult.contains("<rel>prev</rel>"));
        assertTrue(checkLinkXml(queryResult, "self", "/query/BasketItem.findAllPageable"));

        // Check items (limit = 20, offset = 0, count = 5, hasMore = false)
        checkItemsXml(queryResult, 20, 0, 5, false);
    }

    @Test
    public void testCountXml() throws URISyntaxException {
        // Run pageable query with limit = 2 and offset = 2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("limit", "2");
        hints.put("offset", "4");

        final String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_XML_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // First 4 items shouldn't be in the response
        assertFalse(basketItemExists(queryResult, 1));
        assertFalse(basketItemExists(queryResult, 2));
        assertFalse(basketItemExists(queryResult, 3));
        assertFalse(basketItemExists(queryResult, 4));

        // The last one have to be there
        assertTrue(basketItemExists(queryResult, 5));

        // Check links
        assertFalse(queryResult.contains("<rel>next</rel>"));
        assertTrue(checkLinkXml(queryResult, "prev", "/query/BasketItem.findAllPageable?offset=2&amp;limit=2")
            || checkLinkXml(queryResult, "prev", "/query/BasketItem.findAllPageable?limit=2&amp;offset=2"));
        assertTrue(checkLinkXml(queryResult, "self", "/query/BasketItem.findAllPageable?limit=2&amp;offset=4")
            || checkLinkXml(queryResult, "self", "/query/BasketItem.findAllPageable?offset=4&amp;limit=2"));

        // Check items (limit = 1, offset = 4, count = 1, hasMore = false)
        checkItemsXml(queryResult, 2, 4, 1, false);
    }

    @Test(expected = Exception.class)
    public void testIncorrectLimitUsage() throws URISyntaxException {
        // Run pageable query with limit = 2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("limit", "2");

        // Call not pageable query with limit parameter. It suppose to throw an exception.
        RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAll", null, hints, MediaType.APPLICATION_XML_TYPE);
    }

    @Test(expected = Exception.class)
    public void testIncorrectOffsetUsage() throws URISyntaxException {
        // Run pageable query with offset = 2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("offset", "2");

        // Call not pageable query with limit parameter. It suppose to throw an exception.
        RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAll", null, hints, MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void testPageableField() throws URISyntaxException {
        // Run pageable query with limit = 2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("limit", "2");

        final String queryResult = RestUtils.restFindAttribute(context, 1, Basket.class, "basketItems", null, hints, MediaType.APPLICATION_XML_TYPE);
        logger.info(queryResult);

        // First 2 items must be in the response
        assertTrue(queryResult.contains("Item1"));
        assertTrue(queryResult.contains("Item2"));

        // Last 3 must not be in the response
        assertFalse(queryResult.contains("Item3"));
        assertFalse(queryResult.contains("Item4"));
        assertFalse(queryResult.contains("Item5"));

        // Check links
        assertTrue(checkLinkXml(queryResult, "next", "/entity/Basket/1/basketItems?offset=2&amp;limit=2") ||
                checkLinkXml(queryResult, "next", "/entity/Basket/1/basketItems?limit=2&amp;offset=2"));
        assertFalse(queryResult.contains("<rel>prev</rel>"));

        // Check items (limit = 2, offset = 0, count = 2, hasMore = true)
        checkItemsXml(queryResult, 2, 0, 2, true);
    }

    @Test
    public void testPageableFieldOffset() throws URISyntaxException {
        // Run pageable query with offset=2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("offset", "2");

        final String queryResult = RestUtils.restFindAttribute(context, 1, Basket.class, "basketItems", null, hints, MediaType.APPLICATION_XML_TYPE);
        logger.info(queryResult);

        // First 2 items mustn't be in the response
        assertFalse(queryResult.contains("Item1"));
        assertFalse(queryResult.contains("Item2"));

        // Next 2 must be in the response
        assertTrue(queryResult.contains("Item3"));
        assertTrue(queryResult.contains("Item4"));

        // Last item mustn't be in the response
        assertFalse(queryResult.contains("Item5"));

        // Check links
        assertTrue(checkLinkXml(queryResult, "next", "/entity/Basket/1/basketItems?offset=4"));
        assertTrue(checkLinkXml(queryResult, "prev", "/entity/Basket/1/basketItems?offset=0"));

        // Check items (limit = 2, offset = 2, count = 2, hasMore = true)
        checkItemsXml(queryResult, 2, 2, 2, true);
    }

    @Test
    public void testPageableFieldOffset1() throws URISyntaxException {
        // Run pageable query with offset=1
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("offset", "1");

        final String queryResult = RestUtils.restFindAttribute(context, 1, Basket.class, "basketItems", null, hints, MediaType.APPLICATION_XML_TYPE);
        logger.info(queryResult);

        assertFalse(queryResult.contains("Item1"));
        assertTrue(queryResult.contains("Item2"));
        assertTrue(queryResult.contains("Item3"));
        assertFalse(queryResult.contains("Item4"));
        assertFalse(queryResult.contains("Item5"));

        // Check links
        assertTrue(checkLinkXml(queryResult, "next", "/entity/Basket/1/basketItems?offset=3"));
        assertFalse(queryResult.contains("<rel>prev</rel>"));

        // Check items (limit = 2, offset = 1, count = 2, hasMore = true)
        checkItemsXml(queryResult, 2, 1, 2, true);
    }

    @Test
    public void testPageableFieldOffsetGtCount() throws URISyntaxException {
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("offset", "10");

        final String queryResult = RestUtils.restFindAttribute(context, 1, Basket.class, "basketItems", null, hints, MediaType.APPLICATION_JSON_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // Empty response
        assertFalse(queryResult.contains("Item1"));
        assertFalse(queryResult.contains("Item2"));
        assertFalse(queryResult.contains("Item3"));
        assertFalse(queryResult.contains("Item4"));
        assertFalse(queryResult.contains("Item5"));

        // Check links
        assertFalse(checkLinkJson(queryResult, "next"));
        assertFalse(checkLinkJson(queryResult, "prev"));
        assertTrue(checkLinkJson(queryResult, "self", "/entity/Basket/1/basketItems?offset=10"));

        // Check items (limit = 2, offset = 10, count = 0, hasMore = false)
        checkItemsJson(queryResult, 2, 10, 0, false);
    }

    @Test
    public void testPageableFieldLastPage() throws URISyntaxException {
        // Get the last page (offset=3)
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("offset", "3");

        final String queryResult = RestUtils.restFindAttribute(context, 1, Basket.class, "basketItems", null, hints, MediaType.APPLICATION_XML_TYPE);
        logger.info(queryResult);

        assertFalse(queryResult.contains("Item1"));
        assertFalse(queryResult.contains("Item2"));
        assertFalse(queryResult.contains("Item3"));
        assertTrue(queryResult.contains("Item4"));
        assertTrue(queryResult.contains("Item5"));

        // Check links
        assertTrue(checkLinkXml(queryResult, "prev", "/entity/Basket/1/basketItems?offset=1"));
        assertFalse(queryResult.contains("<rel>next</rel>"));

        // Check items (limit = 2, offset = 3, count = 2, hasMore = false)
        checkItemsXml(queryResult, 2, 3, 2, false);
    }

    @Test(expected = RestCallFailedException.class)
    public void testNegativeLimit() throws URISyntaxException {
        // Run pageable query with limit = -2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("limit", "-2");

        // It has to fail because negative limit is not permitted
        RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_JSON_TYPE);
    }

    @Test(expected = RestCallFailedException.class)
    public void testZeroLimit() throws URISyntaxException {
        // Run pageable query with limit = 0
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("limit", "0");

        // It has to fail because zero limit is not permitted
        RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_JSON_TYPE);
    }

    @Test(expected = RestCallFailedException.class)
    public void testNegativeOffset() throws URISyntaxException {
        // Run pageable query with offset = -2
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("offset", "-2");

        // It has to fail because negative offset is not permitted
        RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_JSON_TYPE);
    }

    private boolean basketItemExists(String response, int id) {
        return response.contains("BasketItem" + id);
    }

    private void checkItemsXml(String response, int limit, int offset, int count, boolean hasMore) {
        assertTrue(response.contains("<limit>" + limit + "</limit>"));
        assertTrue(response.contains("<offset>" + offset + "</offset>"));
        assertTrue(response.contains("<count>" + count + "</count>"));
        assertTrue(response.contains("<hasMore>" + hasMore + "</hasMore>"));
    }

    private void checkItemsJson(String response, int limit, int offset, int count, boolean hasMore) {
        assertTrue(response.contains("\"limit\":" + limit));
        assertTrue(response.contains("\"offset\":" + offset));
        assertTrue(response.contains("\"count\":" + count));
        assertTrue(response.contains("\"hasMore\":" + hasMore));
    }
}
