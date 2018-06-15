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
package org.eclipse.persistence.tools.workbench.utility.io;

import java.io.Writer;

/**
 * Why StringWriter does not allow us to pass in a StringBuffer
 * is beyond me....
 *
 * @see java.io.StringWriter
 */
public class StringBufferWriter extends Writer {
    private StringBuffer sb;

    /**
     * Construct a writer on the specified string buffer.
     */
    public StringBufferWriter(StringBuffer sb) {
        super();
        this.sb = sb;
        this.lock = sb;
    }

    /**
     * Construct a writer on the specified string buffer.
     */
    public StringBufferWriter(int initialSize) {
        this(new StringBuffer(initialSize));
    }

    /**
     * Construct a writer on the specified string buffer.
     */
    public StringBufferWriter() {
        this(new StringBuffer());
    }

    /**
     * @see java.io.Writer#write(int)
     */
    public void write(int c) {
        this.sb.append((char) c);
    }

    /**
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char cbuf[], int off, int len) {
        if (len != 0) {
            this.sb.append(cbuf, off, len);
        }
    }

    /**
     * @see java.io.Writer#write(java.lang.String)
     */
    public void write(String str) {
        this.sb.append(str);
    }

    /**
     * @see java.io.Writer#write(java.lang.String, int, int)
     */
    public void write(String str, int off, int len)  {
        this.sb.append(str.substring(off, off + len));
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.sb.toString();
    }

    /**
     * Return the string buffer.
     */
    public StringBuffer getBuffer() {
        return this.sb;
    }

    /**
     * @see java.io.Writer#flush()
     */
    public void flush() {
        // do nothing
    }

    /**
     * @see java.io.Writer#close()
     */
    public void close() {
        // do nothing
    }

}
