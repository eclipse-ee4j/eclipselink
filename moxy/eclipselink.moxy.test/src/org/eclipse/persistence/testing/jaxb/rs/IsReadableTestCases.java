/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.rs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.ws.rs.core.MediaType;

import junit.framework.TestCase;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

public class IsReadableTestCases extends TestCase {

    private MOXyJsonProvider moxyJsonProvider;

    public Integer integerField;

    @Override
    protected void setUp() throws Exception {
        moxyJsonProvider = new TestMOXyJsonProvider();
    }

    public void testDomainObjectReadable() {
        assertTrue(moxyJsonProvider.isReadable(Root.class, null, null, null));
    }

    public void testStringNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(String.class, null, null, null));
    }

    public void testPrimitiveByteArrayNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(ClassConstants.APBYTE, null, null, null));
    }

    public void testFileNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(File.class, null, null, null));
    }

    public void testFileSubclasseNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(MyFile.class, null, null, null));
    }

    public void testDataSourceNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(DataSource.class, null, null, null));
    }

    public void testDataSourceSubclassNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(URLDataSource.class, null, null, null));
    }

    public void testInputStreamNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(InputStream.class, null, null, null));
    }

    public void testInputStreamSubclassNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(ByteArrayInputStream.class, null, null, null));
    }

    public void testReaderNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(Reader.class, null, null, null));
    }

    public void testReaderSubclassNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(StringReader.class, null, null, null));
    }

    public void testInvalidDomainClassNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(InvalidDomainClass.class, null, null, null));
    }

    public void testInvalidDomainClassNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(InvalidDomainClass.class, null, null, null));
    }

    public void testMapNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(Map.class, null, null, null));
    }

    public void testMapNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(Map.class, null, null, null));
    }

    public void testMapImplNotReadable() {
        assertFalse(moxyJsonProvider.isReadable(TreeMap.class, null, null, null));
    }

    public void testMapImplNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(HashMap.class, null, null, null));
    }

    public void testIntegerReadable() throws Exception {
        assertTrue(moxyJsonProvider.isReadable(Integer.class, null, null, null));

        Field integerField = IsReadableTestCases.class.getDeclaredField("integerField");
        ByteArrayInputStream bais = new ByteArrayInputStream("{\"value\" : 123}".getBytes());
        Object result = moxyJsonProvider.readFrom((Class<Object>) integerField.getType(), null, null, null, null, bais);
        assertEquals(123, result);
    }

    private static class TestMOXyJsonProvider extends MOXyJsonProvider {

        @Override
        protected boolean supportsMediaType(MediaType mediaType) {
            return true;
        }

    }

    private static class MyFile extends File {

        public MyFile(File arg0, String arg1) {
            super(arg0, arg1);
        }

    }

    public static class Root {

    }

    public static class InvalidDomainClass {

        public InvalidDomainClass(String parameter) {
        }

    }

}
