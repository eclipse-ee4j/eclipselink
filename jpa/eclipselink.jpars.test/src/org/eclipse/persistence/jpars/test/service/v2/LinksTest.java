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
//         Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test.service.v2;

import org.eclipse.persistence.jpars.test.BaseJparsTest;
import org.eclipse.persistence.jpars.test.model.basket.Basket;
import org.eclipse.persistence.jpars.test.model.basket.BasketItem;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * A set of links tests.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class LinksTest extends BaseJparsTest {
    private Basket basket;

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_basket-static", "v2.0");
    }

    @Before
    public void beforeTest() {
        basket = new Basket();
        basket.setId(1);
        basket.setName("Basket1");

        // Add items
        final List<BasketItem> items = new ArrayList<>(5);
        for (int j=1; j<=5; j++) {
            BasketItem basketItem = new BasketItem();
            basketItem.setId(j);
            basketItem.setName("BasketItem" + j);
            basketItem.setQty(1);
            basketItem.setBasket(basket);
            items.add(basketItem);
        }
        basket.setBasketItems(items);
    }

    @Test
    public void testLinksJson() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final String result = RestUtils.marshal(context, basket, MediaType.APPLICATION_JSON_TYPE);
        logger.info(result);

        assertTrue(result.contains("{\"rel\":\"canonical\""));
        assertTrue(result.contains("{\"rel\":\"self\""));
    }

    @Test
    public void testLinksXml() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final String result = RestUtils.marshal(context, basket, MediaType.APPLICATION_XML_TYPE);
        logger.info(result);

        assertTrue(result.contains("<links rel=\"canonical\""));
        assertTrue(result.contains("<links rel=\"self\""));
    }
}
