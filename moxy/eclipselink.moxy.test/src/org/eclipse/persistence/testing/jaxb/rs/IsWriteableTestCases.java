/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.rs;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import junit.framework.TestCase;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

public class IsWriteableTestCases extends TestCase {

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