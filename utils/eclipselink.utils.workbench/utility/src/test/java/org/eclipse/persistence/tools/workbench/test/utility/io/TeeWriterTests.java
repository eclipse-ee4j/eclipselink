/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.utility.io;

import java.io.CharArrayWriter;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.TeeWriter;


public class TeeWriterTests extends TestCase {
    private Writer writer1;
    private Writer writer2;
    private Writer tee;

    public static Test suite() {
        return new TestSuite(TeeWriterTests.class);
    }

    public TeeWriterTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.writer1 = new CharArrayWriter();
        this.writer2 = new CharArrayWriter();
        this.tee = new TeeWriter(this.writer1, this.writer2);
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testWrite() throws Exception {
        String string = "The quick brown fox jumps over the lazy dog.";
        this.tee.write(string);
        assertEquals(string, this.writer1.toString());
        assertNotSame(string, this.writer1.toString());
        assertEquals(string, this.writer2.toString());
        assertNotSame(string, this.writer2.toString());
    }

}
