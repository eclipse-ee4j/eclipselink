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
//     dclarke - Metadata driven Dynamic Persistence
//
package org.eclipse.persistence.internal.jpa.metadata;

import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ARETURN;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.CHECKCAST;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.POP;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.RETURN;

import org.eclipse.persistence.dynamic.DynamicClassWriter;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Type;

/**
 * Custom {@link DynamicClassWriter} adding getter methods for virtual
 * attributes so that 3rd party frameworks such as javax.validation can access
 * the attribute values.
 *
 * @author dclarke
 * @since EclipseLink 2.4.1
 */
public class MetadataDynamicClassWriter extends DynamicClassWriter {

    private static final String LDYNAMIC_ENTITY = "Lorg/eclipse/persistence/dynamic/DynamicEntity;";
    private static final String SET = "set";
    private static final String LJAVA_LANG_OBJECT = "Ljava/lang/Object;";
    private static final String LJAVA_LANG_STRING = "Ljava/lang/String;";
    private static final String DYNAMIC_EXCEPTION = "org/eclipse/persistence/exceptions/DynamicException";
    private static final String GET = "get";

    /**
     * The {@link MetadataDescriptor} for the dynamic entity
     */
    private MetadataDescriptor descriptor;

    public MetadataDynamicClassWriter(MetadataDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public MetadataDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Add get methods for all virtual attributes
     */
    @Override
    protected void addMethods(ClassWriter cw, String parentClassType) {
        for (MappingAccessor accessor : getDescriptor().getMappingAccessors()) {
            String propertyName = propertyName(accessor.getAttributeName());
            Type returnType = getAsmType(accessor);

            // Add getter
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, GET + propertyName, "()" + returnType.getDescriptor(), null, new String[] { DYNAMIC_EXCEPTION });
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(accessor.getAttributeName());
            mv.visitMethodInsn(INVOKESPECIAL, parentClassType, "get", "(" + LJAVA_LANG_STRING + ")" + LJAVA_LANG_OBJECT);
            mv.visitTypeInsn(CHECKCAST, returnType.getInternalName());
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();

            // Add setter
            mv = cw.visitMethod(ACC_PUBLIC, SET + propertyName, "(" + returnType.getDescriptor() + ")V", null, new String[] { DYNAMIC_EXCEPTION });
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn("id");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, parentClassType, SET, "(" + LJAVA_LANG_STRING + LJAVA_LANG_OBJECT + ")" + LDYNAMIC_ENTITY);
            mv.visitInsn(POP);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
    }

    /**
     * Get the {@link Type} for the accessor. If the accessor's type is
     * primitive return the the non-primitive type.
     */
    private Type getAsmType(MappingAccessor accessor) {
        String attributeType = accessor.getFullyQualifiedClassName(accessor.getAttributeType());

        Class<?> primClass = accessor.getPrimitiveClassForName(attributeType);

        if (primClass != null) {
            Type asmType = Type.getType(primClass);

            switch (asmType.getSort()) {
            case Type.BOOLEAN:
                return Type.getType(ClassConstants.BOOLEAN);
            case Type.BYTE:
                return Type.getType(ClassConstants.BYTE);
            case Type.CHAR:
                return Type.getType(ClassConstants.CHAR);
            case Type.DOUBLE:
                return Type.getType(ClassConstants.DOUBLE);
            case Type.FLOAT:
                return Type.getType(ClassConstants.FLOAT);
            case Type.INT:
                return Type.getType(ClassConstants.INTEGER);
            case Type.LONG:
                return Type.getType(ClassConstants.LONG);
            case Type.SHORT:
                return Type.getType(ClassConstants.SHORT);
            }
        }

        return Type.getType("L" + attributeType.replace(".", "/") + ";");
    }

    /**
     * Convert attribute name into property name to be used in get/set method
     * names by upper casing first letter.
     */
    private String propertyName(String attributeName) {
        char string[] = attributeName.toCharArray();
        string[0] = Character.toUpperCase(string[0]);
        return new String(string);
    }
}
