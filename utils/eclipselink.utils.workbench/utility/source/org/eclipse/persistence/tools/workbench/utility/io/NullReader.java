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

import java.io.Reader;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This is a reader that does nothing.
 * It returns nothing.
 * Performance should be pretty good....
 */
public final class NullReader
    extends Reader
{

    // singleton
    private static Reader INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized Reader instance() {
        if (INSTANCE == null) {
            INSTANCE = new NullReader();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private NullReader() {
        super();
    }

    /**
     * @see java.io.Reader#close()
     */
    public void close() {
        // do nothing
    }

    /**
     * @see java.io.Reader#read()
     */
    public int read() {
        return -1;
    }

    /**
     * @see java.io.Reader#read(char[])
     */
    public int read(char[] cbuf) {
        return -1;
    }

    /**
     * @see java.io.Reader#read(char[], int, int)
     */
    public int read(char[] cbuf, int off, int len) {
        return -1;
    }

    /**
     * @see java.io.Reader#skip(long)
     */
    public long skip(long n) {
        return 0;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this);
    }

}
