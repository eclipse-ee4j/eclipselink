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
import java.io.OutputStream;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.NullOutputStream;


public class NullOutputStreamTests extends TestCase {
    private OutputStream nullOutputStream;

    public static Test suite() {
        return new TestSuite(NullOutputStreamTests.class);
    }

    public NullOutputStreamTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.nullOutputStream = NullOutputStream.instance();
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testClose() throws IOException {
        this.nullOutputStream.close();
    }

    public void testFlush() throws IOException {
        this.nullOutputStream.flush();
    }

    public void testWriteByteArray() throws IOException {
        byte[] byteArray = new byte[10];
        Arrays.fill(byteArray, (byte) 7);
        this.nullOutputStream.write(byteArray);
    }

    public void testWriteByteArrayIntInt() throws IOException {
        byte[] byteArray = new byte[10];
        Arrays.fill(byteArray, (byte) 7);
        this.nullOutputStream.write(byteArray, 2, 5);
    }

    public void testWriteInt() throws IOException {
        this.nullOutputStream.write(10);
    }

}
