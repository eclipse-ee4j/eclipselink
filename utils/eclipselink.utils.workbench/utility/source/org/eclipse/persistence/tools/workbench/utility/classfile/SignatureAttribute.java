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
 * This class models a signature attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 signature_index;
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 * "A signature is a string representing the generic type of a field or
 * method, or generic type information for a class declaration."
 */
// TODO decrypt signature
public class SignatureAttribute extends Attribute {
    private short signatureIndex;

    /**
     * Construct a signature attribute from the specified stream
     * of byte codes.
     */
    public SignatureAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
        super(stream, nameIndex, pool);
    }

    void initializeInfo(ClassFileDataInputStream stream) throws IOException {
        this.signatureIndex = stream.readU2();
    }

    void displayInfoStringOn(IndentingPrintWriter writer) {
        writer.println(this.signature());
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String signature() {
        return this.constantPool().getUTF8String(this.signatureIndex);
    }

    public short getSignatureIndex() {
        return this.signatureIndex;
    }

    void toString(StringBuffer sb) {
        sb.append(this.signature());
    }

}
