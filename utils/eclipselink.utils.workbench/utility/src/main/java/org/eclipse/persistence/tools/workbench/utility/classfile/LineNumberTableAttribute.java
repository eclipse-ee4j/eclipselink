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

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a line number table attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 line_number_table_length;
 *     {
 *         u2 start_pc;
 *     u2 line_number;
 *     }[line_number_table_length] line_number_table;
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class LineNumberTableAttribute extends Attribute {
    private short count;
    private LineNumber[] lineNumbers;

    /**
     * Construct a line number table attribute from the specified stream
     * of byte codes.
     */
    LineNumberTableAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
        super(stream, nameIndex, pool);
    }

    void initializeInfo(ClassFileDataInputStream stream) throws IOException {
        this.count = stream.readU2();
        short cnt = this.count;
        this.lineNumbers = new LineNumber[cnt];
        LineNumber[] numbers = this.lineNumbers;
        for (short i = 0; i < cnt; i++) {
            numbers[i] = new LineNumber(stream);
        }
    }

    public void displayNameOn(IndentingPrintWriter writer) {
        super.displayNameOn(writer);
        writer.print(" (count: ");
        writer.print(this.count);
        writer.print(")");
    }

    void displayInfoStringOn(IndentingPrintWriter writer) {
        short cnt = this.count;
        LineNumber[] numbers = this.lineNumbers;
        for (short i = 0; i < cnt; i++) {
            writer.print(i);
            writer.print(": ");
            numbers[i].displayStringOn(writer);
        }
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
        short cnt = this.count;
        LineNumber[] numbers = this.lineNumbers;
        for (short i = 0; i < cnt; i++) {
            numbers[i].accept(visitor);
        }
    }

    public short getCount() {
        return this.count;
    }

    public LineNumber[] getLineNumbers() {
        return this.lineNumbers;
    }

    public LineNumber getLineNumber(short index) {
        return this.lineNumbers[index];
    }

    void toString(StringBuffer sb) {
        sb.append(this.count);
        sb.append(" line number(s)");
    }

}
