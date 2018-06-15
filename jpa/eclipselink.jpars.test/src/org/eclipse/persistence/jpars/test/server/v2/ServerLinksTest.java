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
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This class tests entity links.
 *
 * @author Dmitry Kornilov
 */
public class ServerLinksTest extends BaseJparsTest {

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
        // Create a basket with id = 1
        Basket basket = new Basket();
        basket.setId(1);
        basket.setName("Basket1");
        basket = RestUtils.restCreate(context, basket, Basket.class);
        assertNotNull("Basket creation failed.", basket);

        // Add items
        for (int j=1; j<=5; j++) {
            BasketItem basketItem = new BasketItem();
            basketItem.setId(j);
            basketItem.setName("BasketItem" + j);
            RestUtils.restUpdate(context, basketItem, BasketItem.class, null, MediaType.APPLICATION_XML_TYPE, false);
            RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(basket.getId()), Basket.class, "basketItems", basketItem, MediaType.APPLICATION_XML_TYPE, "basket", true);
        }
    }

    @Test
    public void testLinksJson() throws URISyntaxException {
        // Get Basket with id = 1
        final String queryResult = RestUtils.restRead(context, 1, Basket.class);
        logger.info(queryResult);

        // Check links
        assertTrue("JSON canonical link test failed.", checkLinkJson(queryResult, "canonical", "/entity/Basket/1"));
        assertTrue("JSON self link test failed.", checkLinkJson(queryResult, "self", "/entity/Basket/1"));
    }

    @Test
    public void testLinksXml() throws URISyntaxException {
        // Get Basket with id = 1
        final String queryResult = RestUtils.restRead(context, 1, Basket.class, MediaType.APPLICATION_XML_TYPE);
        logger.info(queryResult);

        // Check links
        assertTrue("XML canonical link test failed.", checkLinkXml(queryResult, "canonical", "/entity/Basket/1"));
        assertTrue("XML self link test failed.", checkLinkXml(queryResult, "self", "/entity/Basket/1"));
    }

    @Test
    public void testCollectionLinksJson() throws URISyntaxException {
        // Get BasketItem with id = 1
        final String queryResult = RestUtils.restRead(context, 1, BasketItem.class);
        logger.info(queryResult);

        // Check links. Canonical and self links to Basket must be different.
        assertTrue("JSON canonical link test failed.", checkLinkJson(queryResult, "canonical", "/entity/Basket/1"));
        assertTrue("JSON self link test failed.", checkLinkJson(queryResult, "self", "/entity/BasketItem/1/basket"));
    }

    @Test
    public void testCollectionLinksXml() throws URISyntaxException {
        // Get BasketItem with id = 1
        final String queryResult = RestUtils.restRead(context, 1, BasketItem.class, MediaType.APPLICATION_XML_TYPE);
        logger.info(queryResult);

        // Check links. Canonical and self links to Basket must be different.
        assertTrue("XML canonical link test failed.", checkLinkXml(queryResult, "canonical", "/entity/Basket/1"));
        assertTrue("XML self link test failed.", checkLinkXml(queryResult, "self", "/entity/BasketItem/1/basket"));
    }
}
