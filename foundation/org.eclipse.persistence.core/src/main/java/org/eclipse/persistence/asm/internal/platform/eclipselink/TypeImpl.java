/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.asm.internal.platform.eclipselink;

import org.eclipse.persistence.internal.libraries.asm.Type;

import java.lang.reflect.Method;
import java.util.Arrays;

public class TypeImpl extends org.eclipse.persistence.asm.Type {

    private Type type = null;

    public TypeImpl() {
    }

    public TypeImpl(final Class<?> clazz) {
        if (clazz != null) {
            this.type = Type.getType(clazz);
        } else {
            this.type = Type.VOID_TYPE;
        }
    }

    public TypeImpl(final String typeDescriptor) {
        this.type = Type.getType(typeDescriptor);
    }

    @Override
    public String getDescriptor() {
        return this.type.getDescriptor();
    }

    @Override
    public String getInternalName() {
        return this.type.getInternalName();
    }

    @Override
    public int getSort() {
        return this.type.getSort();
    }

    @Override
    public int getOpcode(final int opcode) {
        return this.type.getOpcode(opcode);
    }

    @Override
    protected String getMethodDescriptorInternal(final Method method) {
        return this.type.getMethodDescriptor(method);
    }

    @Override
    protected String getMethodDescriptorInternal(org.eclipse.persistence.asm.Type returnType, org.eclipse.persistence.asm.Type... argumentTypes) {
        Type[] unwrappedArgumentTypes = Arrays.stream(argumentTypes).map(value -> value.unwrap()).toArray(size -> new org.eclipse.persistence.internal.libraries.asm.Type[size]);
        return this.type.getMethodDescriptor(returnType.unwrap(), unwrappedArgumentTypes);
    }

    @Override
    public String getClassName() {
        return this.type.getClassName();
    }

    @Override
    public <T> T unwrap() {
        return (T)this.type;
    }
}
