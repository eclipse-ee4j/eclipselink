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
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Base class for all JPARS tests.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public abstract class BaseJparsTest {
    protected static final Logger logger = Logger.getLogger("org.eclipse.persistence.jpars.test.server");

    protected static String pu;
    protected static String version;

    protected static PersistenceFactoryBase factory;
    protected static PersistenceContext context;
    protected static EntityManagerFactory emf;

    /**
     * Creates a new persistence context with given parameters and assigns it to 'context' static field.
     *
     * @param defaultPU     persistent unit name
     * @param jparsVersion  JPARS version. See {@link org.eclipse.persistence.jpa.rs.features.ServiceVersion}
     *                      for more details.
     */
    protected static void initContext(String defaultPU, String jparsVersion) throws Exception {
        pu = defaultPU;
        version = jparsVersion;

        final Map<String, Object> properties = new HashMap<>();
        ExamplePropertiesLoader.loadProperties(properties);
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
        emf = Persistence.createEntityManagerFactory(pu, properties);

        factory = new PersistenceFactoryBase();
        context = factory.bootstrapPersistenceContext(pu, emf, RestUtils.getServerURI(), version, true);
    }

    /**
     * Gets the server URI.
     *
     * @return the server uri
     * @throws java.net.URISyntaxException the URI syntax exception
     */
    protected static URI getServerURI() throws URISyntaxException {
        return new URI(RestUtils.getServerURI(version) + pu);
    }

    /**
     * Checks that given XML 'response' string contains a link with given 'rel' and 'href'.
     *
     * @param response  XML response string
     * @param rel       link 'rel'
     * @param href       link 'href'
     * @return true if link is found
     * @throws URISyntaxException
     */
    protected boolean checkLinkXml(String response, String rel, String href) throws URISyntaxException {
        final String link = "<links rel=\"" + rel + "\" href=\"" + getServerURI() + href + "\"/>";
        return response.contains(link);
    }

    /**
     * Checks that given JSON 'response' string contains a link with given 'rel' and 'href'.
     *
     * @param response  JSON response string
     * @param rel       link 'rel'
     * @param href       link 'href'
     * @return true if link is found
     * @throws URISyntaxException
     */
    protected boolean checkLinkJson(String response, String rel, String href) throws URISyntaxException {
        final String link = "{\"rel\":\"" + rel + "\",\"href\":\"" + getServerURI() + href + "\"}";
        return response.contains(link);
    }

    /**
     * Checks that given JSON 'response' string contains a link with given 'rel'.
     *
     * @param response  JSON response string
     * @param rel       link 'rel'
     * @return true if link is found
     * @throws URISyntaxException
     */
    protected boolean checkLinkJson(String response, String rel) throws URISyntaxException {
        return response.contains("{\"rel\":\"" + rel);
    }
}
