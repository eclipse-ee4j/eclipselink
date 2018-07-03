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
import java.io.StringWriter;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a line number:
 *     u2 start_pc;
 *     u2 line_number;
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class LineNumber {
    private short startPC;
    private short lineNumber;

    /**
     * Construct a line number from the specified stream
     * of byte codes.
     */
    LineNumber(ClassFileDataInputStream stream) throws IOException {
        super();
        this.initialize(stream);
    }

    void initialize(ClassFileDataInputStream stream) throws IOException {
        this.startPC = stream.readU2();
        this.lineNumber = stream.readU2();
    }

    public String displayString() {
        StringWriter sw = new StringWriter(1000);
        IndentingPrintWriter writer = new IndentingPrintWriter(sw);
        this.displayStringOn(writer);
        return sw.toString();
    }

    public void displayStringOn(IndentingPrintWriter writer) {
        writer.print("line number: ");
        writer.print(this.lineNumber);
        writer.print(" PC start: ");
        writer.println(this.startPC);
    }

    public short getStartPC() {
        return this.startPC;
    }

    public short getLineNumber() {
        return this.lineNumber;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String toString() {
        return ClassTools.shortClassNameForObject(this) + '(' + this.lineNumber + ':' + this.startPC + ')';
    }

}
