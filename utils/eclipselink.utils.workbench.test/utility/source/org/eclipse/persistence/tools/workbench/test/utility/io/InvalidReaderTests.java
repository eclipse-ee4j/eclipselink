/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.utility.io;

import java.io.IOException;
import java.io.Reader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.InvalidReader;


public class InvalidReaderTests extends TestCase {
    private Reader invalidReader;

    public static Test suite() {
        return new TestSuite(InvalidReaderTests.class);
    }

    public InvalidReaderTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.invalidReader = InvalidReader.instance();
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testClose() throws IOException {
        this.invalidReader.close();
    }

    public void testMark() {
        boolean exCaught = false;
        try {
            this.invalidReader.mark(100);
        } catch (IOException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
    }

    public void testMarkSupported() {
        assertFalse(this.invalidReader.markSupported());
    }

    public void testRead() throws IOException {
        boolean exCaught = false;
        try {
            this.invalidReader.read();
        } catch (UnsupportedOperationException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
    }

    public void testReadCharArray() throws IOException {
        char[] cbuf = new char[10];
        boolean exCaught = false;
        try {
            this.invalidReader.read(cbuf);
        } catch (UnsupportedOperationException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
    }

    public void testReadCharArrayIntInt() throws IOException {
        char[] cbuf = new char[10];
        boolean exCaught = false;
        try {
            this.invalidReader.read(cbuf, 3, 2);
        } catch (UnsupportedOperationException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
    }

    public void testReady() throws IOException {
        assertFalse(this.invalidReader.ready());
    }

    public void testReset() {
        boolean exCaught = false;
        try {
            this.invalidReader.reset();
        } catch (IOException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
    }

    public void testSkip() throws IOException {
        boolean exCaught = false;
        try {
            this.invalidReader.skip(44);
        } catch (UnsupportedOperationException ex) {
            exCaught = true;
        }
        assertTrue(exCaught);
    }

}
