/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.rs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.eclipse.persistence.testing.jaxb.rs.model.Bar;
import org.eclipse.persistence.testing.jaxb.rs.model.Foo;
import org.eclipse.persistence.testing.jaxb.rs.model.MyMap;

/**
 * Tests {@code List<MyMap>} marshal/unmarshal via MOXyJsonPrivder.
 *
 * @author Martin Vojtek
 *
 */
public class MapAdapterTestCases extends TestCase {

    private MOXyJsonProvider moxyJsonProvider;

    public List<MyMap> list;

    private final URL jsonResource = Thread.currentThread().getContextClassLoader().getResource("org/eclipse/persistence/testing/jaxb/rs/map_adapter.json");

    @Override
    protected void setUp() throws Exception {
        moxyJsonProvider = new MOXyJsonProvider();
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("key", "value");
        MyMap myMap = new MyMap();
        myMap.setMap(stringMap);

        list = Arrays.asList(myMap, myMap);
    }

    public void testRead() throws Exception {
        Field field = MapAdapterTestCases.class.getField("list");

        try (InputStream entityStream = jsonResource.openStream()) {
            List<MyMap> result = (List<MyMap>) moxyJsonProvider.readFrom((Class<Object>) field.getType(), field.getGenericType(), null, null, null, entityStream);
            assertEquals(list, result);
        }
    }

    public void testWrite() throws Exception {
        Field field = MapAdapterTestCases.class.getField("list");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        moxyJsonProvider.writeTo(list, (Class<Object>) field.getType(), field.getGenericType(), null, null, null, outputStream);

        try (InputStream entityStream = jsonResource.openStream();
                Scanner scanner = new Scanner(entityStream, "UTF-8")) {
            String expected = scanner.useDelimiter("\\A").next();
            assertEquals(expected, new String(outputStream.toByteArray()));
        }
    }

    public void testReadable() throws Exception {
        Field field = MapAdapterTestCases.class.getField("list");
        boolean test = moxyJsonProvider.isReadable((Class<Object>) field.getType(), field.getGenericType(), null, null);
        assertTrue(test);
    }

    public void testWriteable() throws Exception {
        Field field = MapAdapterTestCases.class.getField("list");
        boolean test = moxyJsonProvider.isWriteable((Class<Object>) field.getType(), field.getGenericType(), null, null);
        assertTrue(test);
    }
}
