/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.ws.rs.core.MediaType;

import junit.framework.TestCase;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

public class IsReadableTestCases extends TestCase {

    private MOXyJsonProvider moxyJsonProvider;

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