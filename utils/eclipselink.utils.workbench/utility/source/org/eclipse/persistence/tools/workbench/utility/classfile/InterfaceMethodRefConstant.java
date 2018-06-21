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


/**
 * This class models a class file interface method ref constant.
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class InterfaceMethodRefConstant extends AbstractMethodRefConstant {

    /**
     * Construct a class file constant from the specified stream
     * of byte codes.
     */
    InterfaceMethodRefConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
        super(pool, tag, stream);
    }

    public String description() {
        return "interface method ref";
    }

    public void accept(Visitor visitor) {
        super.accept(visitor);
        visitor.visit(this);
    }

}
