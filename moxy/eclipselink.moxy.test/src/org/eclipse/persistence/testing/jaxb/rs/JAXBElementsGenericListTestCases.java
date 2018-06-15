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
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.eclipse.persistence.testing.jaxb.rs.model.Bar;
import org.eclipse.persistence.testing.jaxb.rs.model.Foo;

/**
 * Tests {@code List<JAXBElement<String>>} marshal/unmarshal via MOXyJsonPrivder.
 *
 * @author Martin Vojtek
 *
 */
public class JAXBElementsGenericListTestCases extends TestCase {

    private MOXyJsonProvider moxyJsonProvider;

    public final List<JAXBElement<String>> list = Arrays.asList(getJAXBElementArray());

    private final URL jsonResource = Thread.currentThread().getContextClassLoader().getResource("org/eclipse/persistence/testing/jaxb/rs/jaxbelement_generics_list.json");

    @Override
    protected void setUp() throws Exception {
        moxyJsonProvider = new MOXyJsonProvider();
    }

    @SuppressWarnings("unchecked")
    private JAXBElement<String>[] getJAXBElementArray() {
        return new JAXBElement[]{
                new JAXBElement(QName.valueOf("element1"), String.class, "book"),
                new JAXBElement(QName.valueOf("element2"), String.class, "car")
        };
    }

    public void testReadGenericList() throws Exception {
        Field field = JAXBElementsGenericListTestCases.class.getField("list");

        try (InputStream entityStream = jsonResource.openStream()) {
            List<JAXBElement<String>> result = (List<JAXBElement<String>>) moxyJsonProvider.readFrom((Class<Object>) field.getType(), field.getGenericType(), null, null, null, entityStream);
            assertTrue(equalsJAXBElementsList(list, result));
        }
    }

    private boolean equalsJAXBElementsList(List<JAXBElement<String>> list1, List<JAXBElement<String>> list2) {
        if (null == list1 && null == list2) {
            return true;
        }

        if ((null == list1 && null != list2) || (null != list1 && null == list2) ) {
            return false;
        }

        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            JAXBElement element1 = list1.get(i);
            JAXBElement element2 = list2.get(i);

            if (!equalsJAXBElements(element1, element2)) {
                return false;
            }
        }

        return true;
    }

    private boolean equalsJAXBElements(JAXBElement element1, JAXBElement element2) {
        if (null == element1 && null == element2) {
            return true;
        }

        if ((null == element1 && null != element2) || (null != element1 && null == element2)) {
            return false;
        }

        if ((element1.isNil() && !element2.isNil()) || (!element1.isNil() && element2.isNil())) {
            return false;
        }

        if ((null == element1.getValue() && null != element2.getValue()) || (null != element1.getValue() && null == element2.getValue())) {
            return false;
        }

        if (!element1.getValue().equals(element2.getValue())) {
            return false;
        }

        return true;
    }

    public void testWriteGenericList() throws Exception {
        Field field = JAXBElementsGenericListTestCases.class.getField("list");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        moxyJsonProvider.writeTo(list, (Class<Object>) field.getType(), field.getGenericType(), null, null, null, outputStream);

        try (InputStream entityStream = jsonResource.openStream();
                Scanner scanner = new Scanner(entityStream, "UTF-8")) {
            String expected = scanner.useDelimiter("\\A").next();
            assertEquals(expected, new String(outputStream.toByteArray()));
        }
    }

    public void testReadableGenericList() throws Exception {
        Field field = JAXBElementsGenericListTestCases.class.getField("list");
        boolean test = moxyJsonProvider.isReadable((Class<Object>) field.getType(), field.getGenericType(), null, null);
        assertTrue(test);
    }

    public void testWriteableGenericList() throws Exception {
        Field field = JAXBElementsGenericListTestCases.class.getField("list");
        boolean test = moxyJsonProvider.isWriteable((Class<Object>) field.getType(), field.getGenericType(), null, null);
        assertTrue(test);
    }

}
