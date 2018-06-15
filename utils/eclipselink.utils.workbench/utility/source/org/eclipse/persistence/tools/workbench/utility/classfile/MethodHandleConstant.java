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

public class MethodHandleConstant extends Constant {

    private short referenceKind;
    private short referenceIndex;
    
    MethodHandleConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
        super(pool, tag, stream);
    }

    @Override
    void initialize(ClassFileDataInputStream stream) throws IOException {
        referenceKind = stream.readU1();
        referenceIndex = stream.readU2();
    }

    @Override
    public void displayStringOn(IndentingPrintWriter writer) {
        super.displayStringOn(writer);
        writer.print(" kind: ");
        writer.print(referenceKind);
        MemberRefConstant ref = this.descriptor();
        writer.print(" class: ");
        writer.print(ref.className());
        writer.print("  name: ");
        writer.print(ref.name());
        writer.print("  descriptor: ");
        writer.println(ref.descriptor());
    }

    @Override
    public String description() {
        return "methodhandle";
    }

    public MemberRefConstant descriptor() {
        return (MemberRefConstant) this.getPool().get(referenceIndex);
    }

    @Override
    public Object value() {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
