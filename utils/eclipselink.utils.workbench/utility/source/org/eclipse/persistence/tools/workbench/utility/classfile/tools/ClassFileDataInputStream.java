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
package org.eclipse.persistence.tools.workbench.utility.classfile.tools;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Add a few helper methods to DataInputStream.
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ClassFileDataInputStream extends DataInputStream {

    /**
     * Public constructor.
     */
    public ClassFileDataInputStream(InputStream in) {
        super(in);
    }

    public final byte readU1() throws IOException {
        return (byte) this.readUnsignedByte();
    }

    public final short readU2() throws IOException {
        return (short) this.readUnsignedShort();
    }

    public final int readU4() throws IOException {
        return this.readInt();
    }

}
