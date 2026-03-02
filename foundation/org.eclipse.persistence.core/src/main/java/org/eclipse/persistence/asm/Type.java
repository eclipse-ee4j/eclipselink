/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
package org.eclipse.persistence.asm;

import org.eclipse.persistence.asm.internal.Util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class Type {

    //This block must be first - begin
    private final static String ASM_TYPE_OW2 = "org.objectweb.asm.Type";

    private final static Map<String, String> ASM_TYPE_MAP = new HashMap<>();

    static {
        ASM_TYPE_MAP.put(ASMFactory.ASM_SERVICE_OW2, ASM_TYPE_OW2);
    }
    //This block must be first - end

    public static final int BOOLEAN = valueInt("BOOLEAN");
    public static final int BYTE = valueInt("BYTE");
    public static final int CHAR = valueInt("CHAR");
    public static final int DOUBLE = valueInt("DOUBLE");
    public static final int FLOAT = valueInt("FLOAT");
    public static final int INT = valueInt("INT");
    public static final int LONG = valueInt("LONG");
    public static final int SHORT = valueInt("SHORT");
    public static final Type VOID_TYPE = ASMFactory.createVoidType();

    private static int valueInt(String fieldName) {
        return ((int) Util.getFieldValue(ASM_TYPE_MAP, fieldName, Integer.TYPE));
    }

    public static Type getType(final String typeDescriptor) {
        return ASMFactory.createType(typeDescriptor);
    }

    public static Type getType(final Class<?> clazz) {
        return ASMFactory.createType(clazz);
    }

    public static String getMethodDescriptor(final Method method) {
        return ASMFactory.createType(Object.class).getMethodDescriptorInternal(method);
    }

    public static String getMethodDescriptor(final Type returnType, final Type... argumentTypes) {
        return ASMFactory.createType(Object.class).getMethodDescriptorInternal(returnType, argumentTypes);
    }

    public static String getDescriptor(final Class<?> clazz) {
        Type type = ASMFactory.createType(clazz);
        if (type instanceof org.eclipse.persistence.asm.internal.platform.ow2.TypeImpl) {
            return ((org.objectweb.asm.Type)ASMFactory.createType(clazz).unwrap()).getDescriptor(clazz);
        } else {
            return null;
        }
    }

    public static String getInternalName(final Class<?> clazz) {
        Type type = ASMFactory.createType(clazz);
        if (type instanceof org.eclipse.persistence.asm.internal.platform.ow2.TypeImpl) {
            return ((org.objectweb.asm.Type)ASMFactory.createType(clazz).unwrap()).getInternalName(clazz);
        } else {
            return null;
        }
    }

    public static Object getTypeClassName(Object value) {
        try {
            if (Class.forName(ASM_TYPE_OW2).isInstance(value)) {
                return ((org.objectweb.asm.Type) value).getClassName();
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            //ignore
        }
        return null;
    }

    public abstract String getDescriptor();

    public abstract String getInternalName();

    public abstract int getSort();

    public abstract int getOpcode(final int opcode);

    protected abstract String getMethodDescriptorInternal(final Method method);

    protected abstract String getMethodDescriptorInternal(final Type returnType, final Type... argumentTypes);

    public abstract String getClassName();

    public abstract <T> T unwrap();

}
