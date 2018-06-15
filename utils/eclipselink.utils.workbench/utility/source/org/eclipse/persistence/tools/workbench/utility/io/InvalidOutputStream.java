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

import java.io.OutputStream;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This implementation of output stream will throw an exception
 * any time it is written to. Flushing or closing the stream
 * will NOT trigger an exception.
 */
public final class InvalidOutputStream
    extends OutputStream
{

    // singleton
    private static OutputStream INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized OutputStream instance() {
        if (INSTANCE == null) {
            INSTANCE = new InvalidOutputStream();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private InvalidOutputStream() {
        super();
    }

    /**
     * Throw an exception.
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) {
        // we don't throw an IOException because that is swallowed by PrintStream
        throw new UnsupportedOperationException();
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this);
    }

}
