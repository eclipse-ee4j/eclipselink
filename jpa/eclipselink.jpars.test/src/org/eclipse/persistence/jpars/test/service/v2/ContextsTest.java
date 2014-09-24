/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.service.v2;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpa.rs.resources.PersistenceResource;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.eclipse.persistence.jpars.test.util.TestHttpHeaders;
import org.eclipse.persistence.jpars.test.util.TestURIInfo;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests a list of available contexts functionality.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
public class ContextsTest {
    private static final String JPARS_VERSION = "v2.0";

    private static final Logger logger = Logger.getLogger("org.eclipse.persistence.jpars.test.service");
    private static final String DEFAULT_PU = "jpars_employee-static";

    private static PersistenceResource persistenceResource;

    @BeforeClass
    public static void setup() throws Exception {
        final Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties);
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
        properties.put(PersistenceUnitProperties.WEAVING, "static");

        final PersistenceFactoryBase factory = new PersistenceFactoryBase();
        factory.get(DEFAULT_PU, RestUtils.getServerURI(), JPARS_VERSION, properties);

        persistenceResource = new PersistenceResource();
        persistenceResource.setPersistenceFactory(factory);
    }

    @Test
    public void testContextsCatalog() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final Response response = persistenceResource.getContexts(JPARS_VERSION,
                TestHttpHeaders.generateHTTPHeader(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON),
                new TestURIInfo());
        final String responseString = getResponseAsString(response);

        // Check contexts
        checkContext(responseString, "jpars_auction-static-local");
        checkContext(responseString, "jpars_employee-static");
        checkContext(responseString, "jpars_traveler-static");
        checkContext(responseString, "jpars_auction-static");
        checkContext(responseString, "jpars_phonebook");
        checkContext(responseString, "jpars_basket-static");
        checkContext(responseString, "jpars_auction");
    }

    private String getResponseAsString(Response response) {
        StreamingOutput output = (StreamingOutput)response.getEntity();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            output.write(outputStream);
        } catch (IOException ex) {
            fail(ex.toString());
        }
        return outputStream.toString();
    }

    private void checkContext(String response, String contextName) throws URISyntaxException {
        assertTrue(response.contains("\"name\":\"" + contextName + "\""));
        final String link = "{\"rel\":\"canonical\",\"href\":\"" + RestUtils.getServerURI(JPARS_VERSION) + contextName + "/metadata-catalog\"}";
        assertTrue(response.contains(link));
    }
}