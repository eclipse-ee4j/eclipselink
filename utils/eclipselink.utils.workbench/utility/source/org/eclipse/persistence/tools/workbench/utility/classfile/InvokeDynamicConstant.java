/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;

public class InvokeDynamicConstant extends Constant {

    private short bootstrapMethodAttrIndex;
    private short nameAndTypeIndex;
    
    InvokeDynamicConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
        super(pool, tag, stream);
    }

    @Override
    void initialize(ClassFileDataInputStream stream) throws IOException {
        bootstrapMethodAttrIndex = stream.readU2();
        nameAndTypeIndex = stream.readU2();
    }

    @Override
    public void displayStringOn(IndentingPrintWriter writer) {
        super.displayStringOn(writer);
        writer.print(" name: ");
        writer.print(this.name());
        writer.print("  descriptor: ");
        writer.println(this.descriptor());
    }

    @Override
    public String description() {
        return "invokeDynamic";
    }

    @Override
    public Object value() {
        return null;
    }

    public String name() {
        return this.nameAndTypeConstant().name();
    }

    public String descriptor() {
        return this.nameAndTypeConstant().descriptor();
    }

    public NameAndTypeConstant nameAndTypeConstant() {
        return this.getPool().getNameAndTypeConstant(this.nameAndTypeIndex);
    }

    public short getNameAndTypeIndex() {
        return this.nameAndTypeIndex;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
