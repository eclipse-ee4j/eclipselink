/*******************************************************************************
 * Copyright (c) 2014 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		Dmitry Kornilov - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.service.v2;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpars.test.model.basket.Basket;
import org.eclipse.persistence.jpars.test.model.basket.BasketItem;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.Persistence;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * A set of links tests.
 *
 * @author Dmitry Kornilov
 */
public class LinksTest {
    private static final String JPARS_VERSION = "v2.0";

    private static final Logger logger = Logger.getLogger("org.eclipse.persistence.jpars.test.service");
    private static final String DEFAULT_PU = "jpars_basket-static";

    private static PersistenceContext context;
    private static PersistenceFactoryBase factory;

    private Basket basket;

    @BeforeClass
    public static void setup() throws Exception {
        final Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties);
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
        factory = new PersistenceFactoryBase();
        context = factory.bootstrapPersistenceContext(DEFAULT_PU, Persistence.createEntityManagerFactory(DEFAULT_PU, properties), RestUtils.getServerURI(JPARS_VERSION), JPARS_VERSION, true);
    }

    @Before
    public void beforeTest() {
        basket = new Basket();
        basket.setId(1);
        basket.setName("Basket1");

        // Add items
        final List<BasketItem> items = new ArrayList<BasketItem>(5);
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
