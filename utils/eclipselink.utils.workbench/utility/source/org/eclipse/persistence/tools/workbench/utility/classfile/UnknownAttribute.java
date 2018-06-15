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
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models an unknown (or unrecognized) attribute
 * that is not defined by the JVM spec or has been
 * recently added to the JVM spec:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u1[attribute_length] info;
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class UnknownAttribute extends Attribute {
    private byte[] info;

    /**
     * Construct a source file attribute from the specified stream
     * of byte codes.
     */
    UnknownAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
        super(stream, nameIndex, pool);
    }

    /**
     * Store the info in a byte array for possible later use.
     */
    void initializeInfo(ClassFileDataInputStream stream) throws IOException {
        int length = this.getLength();
        this.info = new byte[length];
        stream.read(this.info);
    }

    void displayInfoStringOn(IndentingPrintWriter writer) {
        this.writeHexStringOn(this.info, writer);
        writer.println();
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public byte[] getInfo() {
        return this.info;
    }

    void toString(StringBuffer sb) {
        this.appendHexStringTo(this.info, sb);
    }

}
