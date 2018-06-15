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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.StringBufferWriter;


public class StringBufferWriterTests extends TestCase {
    private StringBufferWriter writer;

    public static Test suite() {
        return new TestSuite(StringBufferWriterTests.class);
    }

    public StringBufferWriterTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.writer = new StringBufferWriter();
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testWriteInt() throws Exception {
        this.writer.write('a');
        this.writer.write('b');
        this.writer.write('c');
        assertEquals(3, this.writer.getBuffer().length());
        assertEquals("abc", this.writer.toString());
    }

    public void testWriteCharArray() throws Exception {
        this.writer.write(new char[] {'a', 'b', 'c'});
        this.writer.write(new char[] {'a', 'b', 'c'});
        assertEquals(6, this.writer.getBuffer().length());
        assertEquals("abcabc", this.writer.toString());
    }

    public void testWriteString() throws Exception {
        this.writer.write("abc");
        this.writer.write("abc");
        assertEquals(6, this.writer.getBuffer().length());
        assertEquals("abcabc", this.writer.toString());
    }

    public void testWriteStringIntInt() throws Exception {
        this.writer.write("abc", 1, 2);
        this.writer.write("abc", 2, 1);
        assertEquals(3, this.writer.getBuffer().length());
        assertEquals("bcc", this.writer.toString());
    }

}
