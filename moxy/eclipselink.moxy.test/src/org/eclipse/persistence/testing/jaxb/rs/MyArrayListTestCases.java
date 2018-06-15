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
//     Martin Vojtek - initial implementation
package org.eclipse.persistence.testing.jaxb.rs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.eclipse.persistence.testing.jaxb.rs.model.JaxbBean;
import org.eclipse.persistence.testing.jaxb.rs.model.Bar;
import org.eclipse.persistence.testing.jaxb.rs.model.Foo;
import org.eclipse.persistence.testing.jaxb.rs.model.MyArrayList;

/**
 * Tests {@code MyArrayList<JaxbBean>} marshal/unmarshal via MOXyJsonPrivder.
 *
 * @author Martin Vojtek
 *
 */
public class MyArrayListTestCases extends TestCase {

    private MOXyJsonProvider moxyJsonProvider;

    public MyArrayList<JaxbBean> list;

    private final URL jsonResource = Thread.currentThread().getContextClassLoader().getResource("org/eclipse/persistence/testing/jaxb/rs/myarraylist.json");

    @Override
    protected void setUp() throws Exception {
        moxyJsonProvider = new MOXyJsonProvider();
        list = new MyArrayList<JaxbBean>();

        JaxbBean jaxbBean1 = new JaxbBean();
        jaxbBean1.setValue("one");

        list.add(jaxbBean1);

        JaxbBean jaxbBean2 = new JaxbBean();
        jaxbBean2.setValue("two");

        list.add(jaxbBean2);

        JaxbBean jaxbBean3 = new JaxbBean();
        jaxbBean3.setValue("three");

        list.add(jaxbBean3);
    }

    public void testReadMyArrayList() throws Exception {
        Field field = MyArrayListTestCases.class.getField("list");

        try (InputStream entityStream = jsonResource.openStream()) {
            MyArrayList<JaxbBean> result = (MyArrayList<JaxbBean>) moxyJsonProvider.readFrom((Class<Object>) field.getType(), field.getGenericType(), null, null, null, entityStream);
            assertEquals(list, result);
        }
    }

    public void testWriteMyArrayList() throws Exception {
        Field field = MyArrayListTestCases.class.getField("list");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        moxyJsonProvider.writeTo(list, (Class<Object>) field.getType(), field.getGenericType(), null, null, null, outputStream);

        try (InputStream entityStream = jsonResource.openStream();
                Scanner scanner = new Scanner(entityStream, "UTF-8")) {
            String expected = scanner.useDelimiter("\\A").next();
            assertEquals(expected, new String(outputStream.toByteArray()));
        }
    }

    public void testReadableGenericList() throws Exception {
        Field field = MyArrayListTestCases.class.getField("list");
        boolean test = moxyJsonProvider.isReadable((Class<Object>) field.getType(), field.getGenericType(), null, null);
        assertTrue(test);
    }

    public void testWriteableGenericList() throws Exception {
        Field field = MyArrayListTestCases.class.getField("list");
        boolean test = moxyJsonProvider.isWriteable((Class<Object>) field.getType(), field.getGenericType(), null, null);
        assertTrue(test);
    }

}
