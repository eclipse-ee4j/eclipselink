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

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This is an output stream that does nothing.
 * Everything is thrown into the "bit bucket".
 * Performance should be pretty good....
 */
public final class NullOutputStream
    extends OutputStream
{

    // singleton
    private static OutputStream INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized OutputStream instance() {
        if (INSTANCE == null) {
            INSTANCE = new NullOutputStream();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private NullOutputStream() {
        super();
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException {
        // do nothing
    }

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    public void write(byte[] b) throws IOException {
        // do nothing
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) throws IOException {
        // do nothing
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this);
    }

}
