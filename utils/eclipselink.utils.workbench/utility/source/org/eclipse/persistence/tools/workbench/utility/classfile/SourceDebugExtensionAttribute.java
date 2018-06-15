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
 * This class models a source debug extension attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u1[attribute_length] debug_extension;
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class SourceDebugExtensionAttribute extends Attribute {
    private byte[] debugExtension;

    /**
     * Construct a source file attribute from the specified stream
     * of byte codes.
     */
    public SourceDebugExtensionAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
        super(stream, nameIndex, pool);
    }

    void initializeInfo(ClassFileDataInputStream stream) throws IOException {
        int length = this.getLength();
        this.debugExtension = new byte[length];
        stream.read(this.debugExtension);
    }

    void displayInfoStringOn(IndentingPrintWriter writer) {
        this.writeHexStringOn(this.debugExtension, writer);
        writer.println();
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public byte[] getDebugExtension() {
        return this.debugExtension;
    }

    void toString(StringBuffer sb) {
        this.appendHexStringTo(this.debugExtension, sb);
    }

}
