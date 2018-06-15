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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import junit.framework.TestCase;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

public class IsWriteableTestCases extends TestCase {

    private static final String JSON_INTEGER = "{\"value\":123}";

    public Integer integerField;
    private MOXyJsonProvider moxyJsonProvider;

    @Override
    protected void setUp() throws Exception {
        moxyJsonProvider = new TestMOXyJsonProvider();
    }

    public void testDomainObjectWriteable() {
        assertTrue(moxyJsonProvider.isWriteable(Root.class, null, null, null));
    }

    public void testStringNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(String.class, null, null, null));
    }

    public void testPrimitiveByteArrayNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(ClassConstants.APBYTE, null, null, null));
    }

    public void testFileNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(File.class, null, null, null));
    }

    public void testFilSubclasseNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(MyFile.class, null, null, null));
    }

    public void testDataSourceNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(DataSource.class, null, null, null));
    }

    public void testDataSourceSubclassNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(URLDataSource.class, null, null, null));
    }

    public void testStreamingOutputNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(StreamingOutput.class, null, null, null));
    }

    public void testStreamingOutputSubclassNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(MyStreamingOutput.class, null, null, null));
    }

    public void testStreamingOutputImplNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(StreamingOutputImpl.class, null, null, null));
    }

    public void testIntNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(int.class, null, null, null));
    }

    public void testIntegerWriteable() throws Exception {
        Field integerField = IsWriteableTestCases.class.getDeclaredField("integerField");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        moxyJsonProvider.writeTo(123, integerField.getType(), integerField.getType(), null, null, null, baos);
        assertEquals(JSON_INTEGER, new String(baos.toByteArray()));
        baos.close();
    }

    public void testObjectNotWriteable() {
        assertFalse(moxyJsonProvider.isWriteable(Object.class, null, null, null));
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

    private static interface MyStreamingOutput extends StreamingOutput {

    }

    private static class StreamingOutputImpl implements StreamingOutput {

        @Override
        public void write(OutputStream output) throws IOException,
                WebApplicationException {
        }

    }

    public static class Root {

    }

}
