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

import org.eclipse.persistence.jpa.rs.resources.PersistenceResource;
import org.eclipse.persistence.jpars.test.BaseJparsTest;
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests a list of available contexts functionality.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
public class ContextsTest extends BaseJparsTest {
    protected static PersistenceResource persistenceResource;

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_employee-static", "v2.0");
        persistenceResource = new PersistenceResource();
        persistenceResource.setPersistenceFactory(factory);
    }

    @Test
    public void testContextsCatalog() throws URISyntaxException, JAXBException, UnsupportedEncodingException {
        final Response response = persistenceResource.getContexts(version,
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
        final String link = "{\"rel\":\"canonical\",\"href\":\"" + getServerURI() + "/metadata-catalog\"}";
        assertTrue(response.contains(link));
    }
}