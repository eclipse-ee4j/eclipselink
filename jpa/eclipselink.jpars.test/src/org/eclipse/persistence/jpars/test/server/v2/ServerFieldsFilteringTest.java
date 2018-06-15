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
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This class tests fields filtering feature introduced in V2.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ServerFieldsFilteringTest extends BaseJparsTest {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_basket-static", "v2.0");
        initData();
    }

    @AfterClass
    public static void cleanup() throws URISyntaxException {
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
            RestUtils.restUpdate(context, basketItem, BasketItem.class, false);
            RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(basket.getId()), Basket.class, "basketItems", basketItem, MediaType.APPLICATION_XML_TYPE, "basket", true);
        }
    }

    @Test
    public void testFieldsJson() throws URISyntaxException, UnsupportedEncodingException {
        // fields parameter
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("fields", "name,id");

        // Get BasketItem with id = 1
        String queryResult = RestUtils.restReadWithHints(context, 1, Basket.class.getSimpleName(), hints, MediaType.APPLICATION_JSON_TYPE);
        queryResult = URLDecoder.decode(queryResult, "UTF-8");
        logger.info(queryResult);

        // Check that 'name' and 'id' fields are present in the response and other fields are not
        assertTrue(queryResult.contains("\"id\":1,\"name\":\"Basket1\""));
        assertFalse(queryResult.contains("\"basketItems\":[\n{"));
        assertTrue(checkLinkJson(queryResult, "self", "/entity/Basket/1?fields=name,id"));
        assertTrue(checkLinkJson(queryResult, "canonical", "/entity/Basket/1"));
    }

    @Test
    public void testFieldsXml() throws URISyntaxException, UnsupportedEncodingException {
        // fields parameter
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("fields", "name,id");

        // Get BasketItem with id = 1
        String queryResult = RestUtils.restReadWithHints(context, 1, Basket.class.getSimpleName(), hints, MediaType.APPLICATION_XML_TYPE);
        queryResult = URLDecoder.decode(queryResult, "UTF-8");
        logger.info(queryResult);

        // Check that 'name' and 'id' fields are present in the response and other fields are not
        assertTrue(queryResult.contains("<id>1</id>"));
        assertTrue(queryResult.contains("<name>Basket1</name>"));
        assertFalse(queryResult.contains("<basketItems>"));
        assertFalse(queryResult.contains("</basketItems>"));
        assertTrue(checkLinkXml(queryResult, "self", "/entity/Basket/1?fields=name,id"));
        assertTrue(checkLinkXml(queryResult, "canonical", "/entity/Basket/1"));
    }

    @Test
    public void testExcludeFieldsJson() throws URISyntaxException, UnsupportedEncodingException {
        // excludeFields parameter
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("excludeFields", "basketItems,name");

        // Get BasketItem with id = 1
        String queryResult = RestUtils.restReadWithHints(context, 1, Basket.class.getSimpleName(), hints, MediaType.APPLICATION_JSON_TYPE);
        queryResult = URLDecoder.decode(queryResult, "UTF-8");
        logger.info(queryResult);

        // Check that 'id' field is present in the response and other fields are not
        assertTrue(queryResult.contains("\"id\":1"));
        assertFalse(queryResult.contains("\"basketItems\":[\n{"));
        assertTrue(checkLinkJson(queryResult, "self", "/entity/Basket/1?excludeFields=basketItems,name"));
        assertTrue(checkLinkJson(queryResult, "canonical", "/entity/Basket/1"));
    }

    @Test
    public void testExcludeFieldsXml() throws URISyntaxException, UnsupportedEncodingException {
        // excludeFields parameter
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("excludeFields", "basketItems,name");

        // Get BasketItem with id = 1
        String queryResult = RestUtils.restReadWithHints(context, 1, Basket.class.getSimpleName(), hints, MediaType.APPLICATION_XML_TYPE);
        queryResult = URLDecoder.decode(queryResult, "UTF-8");
        logger.info(queryResult);

        // Check that 'id' field is present in the response and other fields are not
        assertTrue(queryResult.contains("<id>1</id>"));
        assertFalse(queryResult.contains("<basketItems>"));
        assertFalse(queryResult.contains("</basketItems>"));
        assertTrue(checkLinkXml(queryResult, "self", "/entity/Basket/1?excludeFields=basketItems,name"));
        assertTrue(checkLinkXml(queryResult, "canonical", "/entity/Basket/1"));
    }

    @Test(expected = Exception.class)
    public void testBothParametersPresent() throws URISyntaxException {
        // excludeFields parameter
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("excludeFields", "basketItems,name");
        hints.put("fields", "name,id");

        // Get BasketItem with id = 1. Exception must be thrown because both 'fields' and 'excludeFields' parameters
        // cannot be present in the same request.
        RestUtils.restReadWithHints(context, 1, Basket.class.getSimpleName(), hints, MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void testFieldsList() throws URISyntaxException {
        // fields parameter
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("fields", "name,id");

        final String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_JSON_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // Check that 'name' and 'id' fields are present in the response and other fields are not
        assertTrue(queryResult.contains("\"id\":1,\"name\":\"BasketItem1\""));
        assertFalse(queryResult.contains("\"basket\":"));
        assertFalse(queryResult.contains("\"qty\":"));
    }

    @Test
    public void testExcludeFieldsList() throws URISyntaxException {
        // fields parameter
        final Map<String, String> hints = new HashMap<>(1);
        hints.put("excludeFields", "basket,qty");

        final String queryResult = RestUtils.restNamedMultiResultQueryResult(context, "BasketItem.findAllPageable", null, hints, MediaType.APPLICATION_JSON_TYPE);
        logger.info(queryResult);
        assertNotNull("Query all basket items failed.", queryResult);

        // Check that 'name' and 'id' fields are present in the response and other fields are not
        assertTrue(queryResult.contains("\"id\":1,\"name\":\"BasketItem1\""));
        assertFalse(queryResult.contains("\"basket\":"));
        assertFalse(queryResult.contains("\"qty\":"));
    }
}
