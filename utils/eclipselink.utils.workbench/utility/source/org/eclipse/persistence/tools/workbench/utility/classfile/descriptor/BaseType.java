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
package org.eclipse.persistence.tools.workbench.utility.classfile.descriptor;

import java.io.PrintWriter;

import org.eclipse.persistence.tools.workbench.utility.classfile.Visitor;


/**
 * This class models a class file Descriptor Base Type.
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class BaseType extends FieldType {
    private int code;
    private Class javaClass;


    // ********** construction/initialization **********

    BaseType(int code) {
        super();
        this.code = code;
        this.javaClass = javaClass(code);
    }

    /*
     * Class.forName(String) does not work for primitives
     */
    private static Class javaClass(int code) {
        switch (code) {
            case 'B':    return byte.class;
            case 'C':    return char.class;
            case 'D':    return double.class;
            case 'F':    return float.class;
            case 'I':    return int.class;
            case 'J':    return long.class;
            case 'S':    return short.class;
            case 'Z':    return boolean.class;
            case 'V':    return void.class;
            default: throw new IllegalArgumentException("invalid code: " + (char) code);
        }
    }


    // ********** public API **********

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public int arrayDepth() {
        return 0;
    }

    public String elementTypeName() {
        return this.javaClass.getName();
    }

    public String javaName() {
        return this.javaClass.getName();
    }

    /*
     * Class.forName(String) does not work for primitives
     */
    public Class javaClass() {
        return this.javaClass;
    }

    public void appendDeclarationTo(StringBuffer sb) {
        sb.append(this.javaClass.getName());
    }

    public void printDeclarationOn(PrintWriter writer) {
        writer.print(this.javaClass.getName());
    }

    public String internalName() {
        return String.valueOf((char) this.code);
    }


    // ********** internal API **********

    void appendArrayJavaNameTo(StringBuffer sb) {
        sb.append((char) this.code);
    }

    void appendArrayInternalNameTo(StringBuffer sb) {
        sb.append((char) this.code);
    }

}
