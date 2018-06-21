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
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a class file float constant:
 *     u1 tag;
 *     u4 bytes;
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class FloatConstant extends Constant {
    private float value;

    /**
     * Construct a class file constant from the specified stream
     * of byte codes.
     */
    FloatConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
        super(pool, tag, stream);
    }

    void initialize(ClassFileDataInputStream stream) throws IOException {
        this.value = Float.intBitsToFloat(stream.readU4());
    }

    public void displayStringOn(IndentingPrintWriter writer) {
        super.displayStringOn(writer);
        writer.print(" value: ");
        writer.println(this.value);
    }

    public String description() {
        return "float";
    }

    public void printFieldInitializationClauseOn(PrintWriter writer) {
        writer.print(this.value);
        writer.print('F');
    }

    public float floatValue() {
        return this.value;
    }

    public Object value() {
        return new Float(this.value);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
