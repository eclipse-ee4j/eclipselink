/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
******************************************************************************/
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
