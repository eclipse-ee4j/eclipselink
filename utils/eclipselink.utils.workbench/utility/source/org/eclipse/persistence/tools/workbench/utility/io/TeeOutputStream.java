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

import java.io.IOException;
import java.io.OutputStream;

/**
 * A "tee" output stream forwards the data written to it
 * to two streams.
 */
public class TeeOutputStream extends OutputStream {
    private OutputStream stream1;
    private OutputStream stream2;

    /**
     * Construct a "tee" output stream that writes to the
     * specified streams.
     */
    public TeeOutputStream(OutputStream stream1, OutputStream stream2) {
        super();
        this.stream1 = stream1;
        this.stream2 = stream2;
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    public synchronized void write(int b) throws IOException {
        this.stream1.write(b);
        this.stream2.write(b);
    }

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    public synchronized void write(byte[] b) throws IOException {
        this.stream1.write(b);
        this.stream2.write(b);
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        this.stream1.write(b, off, len);
        this.stream2.write(b, off, len);
    }

    /**
     * @see java.io.OutputStream#flush()
     */
    public synchronized void flush() throws IOException {
        this.stream1.flush();
        this.stream2.flush();
    }

    /**
     * @see java.io.OutputStream#close()
     */
    public synchronized void close() throws IOException {
        this.stream1.close();
        this.stream2.close();
    }

}
