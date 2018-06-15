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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.io.TokenWriter;

public class TokenTests extends TestCase {

    public static Test suite() {
        return new TestSuite(TokenTests.class);
    }

    public TokenTests(String name) {
        super(name);
    }

    public void testTokenWriter() throws IOException {
        String delimiters = "=";
        char escapeCharacter = '\\';
        TokenWriter writer;

        writer = new TokenWriter(delimiters, escapeCharacter);
        writer.write("Fred");
        writer.writeDelimiter("=");
        writer.write("Wilma");
        assertEquals(writer.toString(), "Fred=Wilma");

        writer = new TokenWriter(delimiters, escapeCharacter);
        writer.write("=Fred");
        writer.writeDelimiter("=");
        writer.write("Wilma");
        assertEquals(writer.toString(), "\\=Fred=Wilma");

        writer = new TokenWriter(delimiters, escapeCharacter);
        writer.write("Fred=");
        writer.writeDelimiter("=");
        writer.write("Wilma");
        assertEquals(writer.toString(), "Fred\\==Wilma");

        writer = new TokenWriter(delimiters, escapeCharacter);
        writer.write("Fred");
        writer.writeDelimiter("=");
        writer.write("=Wilma");
        assertEquals(writer.toString(), "Fred=\\=Wilma");

        writer = new TokenWriter(delimiters, escapeCharacter);
        writer.write("Fred");
        writer.writeDelimiter("=");
        writer.write("Wilma=");
        assertEquals(writer.toString(), "Fred=Wilma\\=");

        writer = new TokenWriter(delimiters, escapeCharacter);
        writer.write("==Fred==");
        writer.writeDelimiter("=");
        writer.write("==Wilma==");
        assertEquals(writer.toString(), "\\=\\=Fred\\=\\==\\=\\=Wilma\\=\\=");

        writer = new TokenWriter(delimiters + "!", escapeCharacter);
        writer.write("!==Fred==!");
        writer.writeDelimiter("!=!");
        writer.write("!==Wilma==!");
        assertEquals(writer.toString(), "\\!\\=\\=Fred\\=\\=\\!!=!\\!\\=\\=Wilma\\=\\=\\!");

        writer = new TokenWriter(delimiters + "!", escapeCharacter);
        writer.write("!==Fred==!");
        writer.writeDelimiter("!=!");
        writer.write("!==Wilma==!");
        writer.writeDelimiter("!=!");
        assertEquals(writer.toString(), "\\!\\=\\=Fred\\=\\=\\!!=!\\!\\=\\=Wilma\\=\\=\\!!=!");

    }

    public void testTokenWriterExceptions() {
        String delimiters = "=";
        char escapeCharacter = '\\';
        TokenWriter writer;
        boolean exCaught;

        exCaught = false;
        try {
            writer = new TokenWriter(delimiters + escapeCharacter, escapeCharacter);
        } catch (IllegalArgumentException ex) {
            exCaught = true;
        }
        assertTrue("\"Escape character is also delimiter\" exception not thrown.", exCaught);

        exCaught = false;
        try {
            writer = new TokenWriter(null, escapeCharacter);
        } catch (NullPointerException ex) {
            exCaught = true;
        }
        assertTrue("\"Missing delimiters\" NPE not thrown.", exCaught);

        exCaught = false;
        writer = new TokenWriter(delimiters, escapeCharacter);
        try {
            writer.writeDelimiter('c');
        } catch (IllegalArgumentException ex) {
            exCaught = true;
        }
        assertTrue("\"Not a delimiter\" exception not thrown.", exCaught);

        exCaught = false;
        writer = new TokenWriter(delimiters, escapeCharacter);
        try {
            writer.writeDelimiter("string");
        } catch (IllegalArgumentException ex) {
            exCaught = true;
        }
        assertTrue("\"Not a delimiter\" exception not thrown.", exCaught);

    }

}
