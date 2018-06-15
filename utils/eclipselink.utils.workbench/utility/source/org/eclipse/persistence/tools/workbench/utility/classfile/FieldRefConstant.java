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

import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.FieldType;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;


/**
 * This class models a class file field ref constant.
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class FieldRefConstant extends MemberRefConstant {
    private FieldType fieldDescriptor;        // lazy-initialized - so use the getter

    /**
     * Construct a class file constant from the specified stream
     * of byte codes.
     */
    FieldRefConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
        super(pool, tag, stream);
    }

    public String description() {
        return "field ref";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.getFieldDescriptor().accept(visitor);
    }

    public FieldType getFieldDescriptor() {
        if (this.fieldDescriptor == null) {
            this.fieldDescriptor = FieldType.createFieldType(this.descriptor());
        }
        return this.fieldDescriptor;
    }

}
