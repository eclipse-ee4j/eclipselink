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
package org.eclipse.persistence.tools.workbench.utility.io;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This implementation of writer will throw an exception
 * any time it is written to. Flushing or closing the writer
 * will NOT trigger an exception.
 */
public final class InvalidWriter
    extends Writer
{

    // singleton
    private static Writer INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized Writer instance() {
        if (INSTANCE == null) {
            INSTANCE = new InvalidWriter();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private InvalidWriter() {
        super();
    }

    /**
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char[] cbuf, int off, int len) throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.io.Writer#flush()
     */
    public void flush() throws IOException {
        // do nothing
    }

    /**
     * @see java.io.Writer#close()
     */
    public void close() throws IOException {
        // do nothing
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this);
    }

}
