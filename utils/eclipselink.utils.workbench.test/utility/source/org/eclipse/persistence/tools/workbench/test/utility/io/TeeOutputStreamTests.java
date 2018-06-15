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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.TeeOutputStream;


public class TeeOutputStreamTests extends TestCase {
    private OutputStream out1;
    private OutputStream out2;
    private OutputStream tee;

    public static Test suite() {
        return new TestSuite(TeeOutputStreamTests.class);
    }

    public TeeOutputStreamTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.out1 = new ByteArrayOutputStream();
        this.out2 = new ByteArrayOutputStream();
        this.tee = new TeeOutputStream(this.out1, this.out2);
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testWrite() throws Exception {
        String string = "The quick brown fox jumps over the lazy dog.";
        this.tee.write(string.getBytes());
        assertEquals(string, this.out1.toString());
        assertNotSame(string, this.out1.toString());
        assertEquals(string, this.out2.toString());
        assertNotSame(string, this.out2.toString());
    }

}
