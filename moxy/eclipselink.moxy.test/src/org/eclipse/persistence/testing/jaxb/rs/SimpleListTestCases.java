/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.rs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

import junit.framework.TestCase;

public class SimpleListTestCases extends TestCase {

    private MOXyJsonProvider moxyJsonProvider;
    
    public static final String jsonArrayOfString = "[\"A\",\"B\",\"C\",null]";
    public static final String[] arrayOfString = {"A", "B", "C", null};
    public static final List<String> listOfString = Arrays.asList(arrayOfString);
    
    public static final String jsonArrayOfBoolean = "[true,false,null,true]";
    public static final Boolean[] arrayOfBoolean = {true,false,null,true};
    public static final List<Boolean> listOfBoolean = Arrays.asList(arrayOfBoolean);

    public static final String jsonArrayOfInteger = "[null,1,2,3]";
    public static final Integer[] arrayOfInteger = {null,1,2,3};
    public static final List<Integer> listOfInteger = Arrays.asList(arrayOfInteger);

    @Override
    protected void setUp() throws Exception {
        moxyJsonProvider = new MOXyJsonProvider();
    }

    public void testReadArrayOfString() throws Exception {
        Field field = SimpleListTestCases.class.getField("arrayOfString");
        InputStream entityStream = new ByteArrayInputStream(jsonArrayOfString.getBytes("UTF-8"));
        String[] result = (String[])  moxyJsonProvider.readFrom((Class<Object>) field.getType(), field.getGenericType(), null, null, null, entityStream);
        assertTrue(Arrays.equals(arrayOfString, result));
    }

    public void testReadListOfString() throws Exception {
        Field field = SimpleListTestCases.class.getField("listOfString");
        InputStream entityStream = new ByteArrayInputStream(jsonArrayOfString.getBytes("UTF-8"));
        Object result =  moxyJsonProvider.readFrom((Class<Object>) field.getType(), field.getGenericType(), null, null, null, entityStream);
        entityStream.close();
        assertEquals(listOfString, result);
    }

    public void testReadArrayOfBoolean() throws Exception {
        Field field = SimpleListTestCases.class.getField("arrayOfBoolean");
        InputStream entityStream = new ByteArrayInputStream(jsonArrayOfBoolean.getBytes("UTF-8"));
        Boolean[] result =  (Boolean[]) moxyJsonProvider.readFrom((Class<Object>) field.getType(), field.getGenericType(), null, null, null, entityStream);
        entityStream.close();
        assertTrue(Arrays.equals(arrayOfBoolean, result));
    }

    public void testReadListOfBoolean() throws Exception {
        Field field = SimpleListTestCases.class.getField("listOfBoolean");
        InputStream entityStream = new ByteArrayInputStream(jsonArrayOfBoolean.getBytes("UTF-8"));
        Object result =  moxyJsonProvider.readFrom((Class<Object>) field.getType(), field.getGenericType(), null, null, null, entityStream);
        entityStream.close();
        assertEquals(listOfBoolean, result);
    }

    public void testReadArrayOfInteger() throws Exception {
        Field field = SimpleListTestCases.class.getField("arrayOfInteger");
        InputStream entityStream = new ByteArrayInputStream(jsonArrayOfInteger.getBytes("UTF-8"));
        Integer[] result =  (Integer[]) moxyJsonProvider.readFrom((Class<Object>) field.getType(), field.getGenericType(), null, null, null, entityStream);
        entityStream.close();
        assertTrue(Arrays.equals(arrayOfInteger, result));
    }

    public void testReadListOfInteger() throws Exception {
        Field field = SimpleListTestCases.class.getField("listOfInteger");
        InputStream entityStream = new ByteArrayInputStream(jsonArrayOfInteger.getBytes("UTF-8"));
        Object result =  moxyJsonProvider.readFrom((Class<Object>) field.getType(), field.getGenericType(), null, null, null, entityStream);
        entityStream.close();
        assertEquals(listOfInteger, result);
    }

}
