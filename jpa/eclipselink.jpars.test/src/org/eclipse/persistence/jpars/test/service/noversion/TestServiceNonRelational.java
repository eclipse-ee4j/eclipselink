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
//      gonural - Initial implementation
package org.eclipse.persistence.jpars.test.service.noversion;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpa.rs.resources.unversioned.PersistenceResource;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestServiceNonRelational {
    PersistenceFactoryBase factory = null;
    PersistenceContext context = null;
    private static final String PLACE_PU = "jpars_place";
    private static final String ZIPS_PU = "jpars_zip";
    private static final String PERSON_PU = "jpars_person";

    @After
    public void resetContext() {
        if (factory != null) {
            factory.close();
            factory = null;
        }
        if (context != null) {
            context.stop();
        }
    }

    @Test
    public void testMarshallPlace() throws Exception {
        setContext(PLACE_PU);
        PersistenceResource resource = new PersistenceResource();
        resource.setPersistenceFactory(factory);
        DynamicEntity place = context.newEntity("Place");
        place.set("state", "Ontario");
        context.create(null, place);
        InputStream stream = serializeToStream(place, context, MediaType.APPLICATION_JSON_TYPE);
        place = (DynamicEntity) context.unmarshalEntity("Place", MediaType.APPLICATION_JSON_TYPE, stream);
        (new FetchGroupManager()).setObjectFetchGroup(place, null, null);
        String state = place.get("state");
        assertNotNull("State of place is null.", state);
        assertTrue("Ontario".equals(state));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testMarshallZips() throws Exception {
        setContext(ZIPS_PU);
        PersistenceResource resource = new PersistenceResource();
        resource.setPersistenceFactory(factory);
        DynamicEntity zips = context.newEntity("Zips");
        zips.set("state", "ON");
        zips.set("city", "Ottawa");
        zips.set("pop", 6055);
        zips.set("id", "1234");

        List<Double> loc = new ArrayList<>();
        loc.add(45.4214);
        loc.add(75.6919);
        zips.set("loc", loc);

        context.create(null, zips);
        InputStream stream = serializeToStream(zips, context, MediaType.APPLICATION_XML_TYPE);
        zips = (DynamicEntity) context.unmarshalEntity("Zips", MediaType.APPLICATION_XML_TYPE, stream);
        (new FetchGroupManager()).setObjectFetchGroup(zips, null, null);
        String state = zips.get("state");
        assertNotNull("State of place is null.", state);
        assertTrue("ON".equals(state));

        String id = zips.get("id");
        assertNotNull("id is null.", id);
        assertTrue("1234".equals(id));
        assertTrue("Unexpected number of objects in loc", (((Collection) zips.get("loc")).size() == 2));
    }

    @Test
    public void testMarshallPerson() throws Exception {
        setContext(PERSON_PU);
        PersistenceResource resource = new PersistenceResource();
        resource.setPersistenceFactory(factory);
        DynamicEntity person = context.newEntity("Person");
        person.set("firstName", "Jim");
        person.set("lastName", "Smith");
        person.set("age", 48);
        person.set("occupation", "Engineer");
        person.set("currentEmployer", "Oracle");

        List<String> pastEmployers = new ArrayList<>();
        pastEmployers.add("BEA");
        pastEmployers.add("IBM");
        pastEmployers.add("Sun");
        person.set("pastEmployers", pastEmployers);

        DynamicEntity address1 = context.newEntity("Addresses");
        address1.set("street1", "123 Sandy Lane");
        address1.set("city", "San Jose");
        address1.set("state", "CA");
        address1.set("zip", 94143);

        DynamicEntity address2 = context.newEntity("Addresses");
        address2.set("street1", "334 California Street");
        address2.set("city", "San Francisco");
        address2.set("state", "CA");
        address2.set("zip", 94110);

        List<Object> addresses = new ArrayList<>();
        addresses.add(address1);
        addresses.add(address2);
        person.set("addresses", addresses);

        context.create(null, person);
        InputStream stream = serializeToStream(person, context, MediaType.APPLICATION_XML_TYPE);
        person = (DynamicEntity) context.unmarshalEntity("Person", MediaType.APPLICATION_XML_TYPE, stream);
        (new FetchGroupManager()).setObjectFetchGroup(person, null, null);
        String firstName = person.get("firstName");
        assertNotNull("firstName is null.", firstName);
        assertTrue("Jim".equals(firstName));
    }

    private static InputStream serializeToStream(Object object, PersistenceContext context, MediaType mediaType) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        context.marshallEntity(object, mediaType, os);
        ByteArrayInputStream stream = new ByteArrayInputStream(os.toByteArray());
        return stream;
    }

    private void setContext(String persistenceUnit) throws Exception {
        context = null;
        Map<String, Object> properties = new HashMap<>();
        ExamplePropertiesLoader.loadProperties(properties);
        factory = new PersistenceFactoryBase();
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.WEAVING, "static");
        properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
        context = factory.get(persistenceUnit, RestUtils.getServerURI(), null, properties);
        if (context == null) {
            throw new Exception("Persistence context could not be created.");
        }
    }
}
