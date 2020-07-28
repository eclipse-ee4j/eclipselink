/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;

public class MethodTypeConstant extends Constant {

    private short descriptorIndex;
    
    MethodTypeConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
        super(pool, tag, stream);
    }

    @Override
    void initialize(ClassFileDataInputStream stream) throws IOException {
        this.descriptorIndex = stream.readU2();
    }

    @Override
    public void displayStringOn(IndentingPrintWriter writer) {
        super.displayStringOn(writer);
        writer.print("  descriptor: ");
        writer.println(this.descriptor());
    }
    
    @Override
    public String description() {
        return "method type";
    }

    public String descriptor() {
        return this.utf8String(this.descriptorIndex);
    }

    public short getDescriptorIndex() {
        return this.descriptorIndex;
    }

    @Override
    public Object value() {
        return this.descriptor();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
