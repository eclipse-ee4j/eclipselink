/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Helper for propagating {@link Readable} to {@link javax.json.stream.JsonParser}
 *
 * @author Roman Grigoriadi
 */
public class ReadableReader extends Reader {

    private final Readable readable;

    /**
     * @param readable readable to read from
     */
    public ReadableReader(Readable readable) {
        this.readable = readable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(char[] buffer, int off, int len) throws IOException {
        CharBuffer buf = CharBuffer.wrap(buffer);
        final int result = readable.read(buf);
        if (result == -1) {
            return result;
        }
        char[] test = new char[result];
        buf.get(test, 0, result);
        return result ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if (readable instanceof Closeable) {
            ((Closeable)readable).close();
        }
    }
}
