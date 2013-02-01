/****************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpa.rs.resources.unversioned.PersistenceResource;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestServiceNonRelational {
    private static PersistenceFactoryBase factory;
    private static PersistenceContext context;
    private static final String DEFAULT_PU = "jpars_place";

    @BeforeClass
    public static void setup(){
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties);
        factory = null;
        try{
            factory = new PersistenceFactoryBase();
            properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
            properties.put(PersistenceUnitProperties.WEAVING, "static");
            properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
            properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
            context = factory.get(DEFAULT_PU, RestUtils.getServerURI(), properties);
            if (context == null) {
                throw new Exception("Persistence context could not be created.");
            }
        } catch (Exception e){
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testMarshallPlace() throws Exception {
        PersistenceResource resource = new PersistenceResource();
        resource.setPersistenceFactory(factory);
        DynamicEntity place = (DynamicEntity) context.newEntity("Place");
        place.set("state", "Ontario");
        context.create(null, place);
        InputStream stream = serializeToStream(place, context, MediaType.APPLICATION_JSON_TYPE);
        place = (DynamicEntity) context.unmarshalEntity("Place", MediaType.APPLICATION_JSON_TYPE, stream);
        (new FetchGroupManager()).setObjectFetchGroup(place, null, null);
        String state = place.get("state");
        assertNotNull("State of place is null.", place.get("state"));
        assertTrue("Ontario".equals(state));
    }

    public static InputStream serializeToStream(Object object, PersistenceContext context, MediaType mediaType){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            context.marshallEntity(object, mediaType, os);
        } catch (Exception e){
            e.printStackTrace();
            fail(e.toString());
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(os.toByteArray());
        return stream;
    }
}
