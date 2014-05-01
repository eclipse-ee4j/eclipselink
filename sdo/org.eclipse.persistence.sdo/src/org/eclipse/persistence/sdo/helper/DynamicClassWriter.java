/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     bdoughan - Mar 18/2009 - 2.0 - Dynamically generated impl classes now
 *                                    implement correct interface.
 ******************************************************************************/
package org.eclipse.persistence.sdo.helper;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.extension.SDOUtil;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

import commonj.sdo.helper.HelperContext;

/*
 * Dynamically generate the implementation class for the SDO type.  If the type has an instance
 * (interface) class then the dynamically generated impl class must fully implement it.  Additionally
 * a constructor and a writeReplace() method are added.
 */
public class DynamicClassWriter {
    private static final String START_PROPERTY_INDEX = "START_PROPERTY_INDEX";
    private static final String END_PROPERTY_INDEX = "END_PROPERTY_INDEX";
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String LIST = "List";
    private static final String WRITE_REPLACE = "writeReplace";

    private Class parentClass;
    private String typeImplClassDescriptor;
    private SDOType type;
    private Integer startPropertyIndex;

    // hold the context containing all helpers so that we can preserve
    // inter-helper relationships
    private HelperContext aHelperContext;

    public DynamicClassWriter(String className, SDOType type, HelperContext aContext) {
        this.aHelperContext = aContext;
        this.parentClass = SDODataObject.class;
        this.typeImplClassDescriptor = className.replace('.', '/');
        this.type = type;
        initializeParentClass();

        if (type.isSubType()) {
            try {
                Field parentEndPropertyIndexField = PrivilegedAccessHelper.getField(parentClass, END_PROPERTY_INDEX, true);
                Integer parentEndPropertyIndex = (Integer) PrivilegedAccessHelper.getValueFromField(parentEndPropertyIndexField, parentClass);
                startPropertyIndex = parentEndPropertyIndex + 1;
            } catch (NoSuchFieldException e) {
                startPropertyIndex = 0;
            } catch (IllegalAccessException e) {
                startPropertyIndex = 0;
            }
        } else {
            startPropertyIndex = 0;
        }
    }

    private void initializeParentClass() {
        if (type.isSubType()) {
            SDOType parentSDOType = (SDOType) type.getBaseTypes().get(0);
            String parentClassName = parentSDOType.getInstanceClassName() + SDOConstants.SDO_IMPL_NAME;
            try {
                parentClass = ((SDOXMLHelper) aHelperContext.getXMLHelper()).getLoader().loadClass(parentClassName, parentSDOType);
            } catch (Exception e) {
                parentClass = null;
            }
            if (parentClass == null) {
                parentClass = SDODataObject.class;
            }
        } else {
            parentClass = SDODataObject.class;
        }
    }

    public Class getParentClass() {
        return this.parentClass;
    }

    /**
     * This is where the byte codes for the generic subclass are defined and the
     * class is created dynamically from them.
     */
    public byte[] createClass() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        if (null == type.getInstanceClass()) {
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, typeImplClassDescriptor, null, Type.getType(parentClass).getInternalName(), null);
        } else {
            String[] interfaces = new String[1];
            interfaces[0] = type.getInstanceClassName().replace('.', '/');
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, typeImplClassDescriptor, null, Type.getType(parentClass).getInternalName(), interfaces);
            addPropertyIndices(cw);
            for (Object object : type.getDeclaredProperties()) {
                SDOProperty sdoProperty = (SDOProperty) object;
                addPropertyGetMethod(cw, sdoProperty);
                addPropertySetMethod(cw, sdoProperty);
            }
        }

        addConstructors(cw);
        addWriteReplace(cw);
        cw.visitEnd();

        return cw.toByteArray();
    }

    private void addPropertyIndices(ClassWriter cw) {
        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, START_PROPERTY_INDEX, "I", null, startPropertyIndex).visitEnd();
        int declaredPropsSize = type.getDeclaredProperties().size();

        Integer endPropertyIndex;
        if (declaredPropsSize > 0) {
            endPropertyIndex = startPropertyIndex + declaredPropsSize - 2;
        } else {
            endPropertyIndex = startPropertyIndex - 1;
        }
        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, END_PROPERTY_INDEX, "I", null, endPropertyIndex).visitEnd();
    }

    private void addConstructors(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), null, new String[] { Type.getInternalName(Serializable.class) });
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getType(parentClass).getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void addPropertyGetMethod(ClassWriter cw, SDOProperty property) {
        String returnType = SDOUtil.getJavaTypeForProperty(property);
        String outerGetMethodName = SDOUtil.getMethodName(property.getName(), returnType);

        if (property.getType() == SDOConstants.SDO_BOOLEAN || property.getType() == SDOConstants.SDO_BOOLEANOBJECT) {
            String booleanGetterName = SDOUtil.getBooleanGetMethodName(property.getName(), returnType);
            addPropertyGetMethodInternal(cw, property, booleanGetterName, returnType);
        }
        addPropertyGetMethodInternal(cw, property, outerGetMethodName, returnType);
    }

    private void addPropertyGetMethodInternal(ClassWriter cw, SDOProperty property, String outerGetMethodName, String returnType) {
        String propertyInstanceClassDescriptor;
        if (property.isMany()) {
            propertyInstanceClassDescriptor = Type.getDescriptor(List.class);
        } else if (property.getType().isDataType()) {
            propertyInstanceClassDescriptor = Type.getDescriptor(property.getType().getInstanceClass());
        } else {
            propertyInstanceClassDescriptor = "L" + returnType.replace('.', '/') + ";";
        }
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, outerGetMethodName, "()" + propertyInstanceClassDescriptor, null, null);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitIntInsn(Opcodes.BIPUSH, startPropertyIndex + property.getIndexInType());

        String builtIn = SDOUtil.getBuiltInType(returnType);
        if (null != builtIn) {
            if (property.getType().isDataType() && !builtIn.equals(LIST)) {
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, typeImplClassDescriptor, GET + builtIn, "(I)" + propertyInstanceClassDescriptor, false);
                int iReturnOpcode = Type.getType(property.getType().getInstanceClass()).getOpcode(Opcodes.IRETURN);
                mv.visitInsn(iReturnOpcode);
            } else {
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, typeImplClassDescriptor, GET, "(I)Ljava/lang/Object;", false);
                mv.visitInsn(Opcodes.ARETURN);
            }
        } else {
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, typeImplClassDescriptor, GET, "(I)Ljava/lang/Object;", false);
            mv.visitInsn(Opcodes.ARETURN);
        }
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    private void addPropertySetMethod(ClassWriter cw, SDOProperty property) {
        String returnType = SDOUtil.getJavaTypeForProperty(property);
        String outerSetMethodName = SDOUtil.setMethodName(property.getName());

        String propertyInstanceClassDescriptor;

        if (property.isMany()) {
            propertyInstanceClassDescriptor = Type.getDescriptor(List.class);
        } else if (property.getType().isDataType()) {
            propertyInstanceClassDescriptor = Type.getDescriptor(property.getType().getInstanceClass());
        } else {
            propertyInstanceClassDescriptor = "L" + returnType.replace('.', '/') + ";";
        }
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, outerSetMethodName, "(" + propertyInstanceClassDescriptor + ")V", null, null);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitIntInsn(Opcodes.BIPUSH, startPropertyIndex + property.getIndexInType());

        String builtIn = SDOUtil.getBuiltInType(returnType);
        int iLoadOpcode = Opcodes.ALOAD;
        if (null != builtIn) {
            if (property.getType().isDataType() && !builtIn.equals(LIST)) {
                iLoadOpcode = Type.getType(property.getType().getInstanceClass()).getOpcode(Opcodes.ILOAD);
                mv.visitVarInsn(iLoadOpcode, 1);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, typeImplClassDescriptor, SET + builtIn, "(I" + propertyInstanceClassDescriptor + ")V", false);
            } else {
                mv.visitVarInsn(iLoadOpcode, 1);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, typeImplClassDescriptor, SET, "(ILjava/lang/Object;)V", false);
            }
        } else {
            mv.visitVarInsn(iLoadOpcode, 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, typeImplClassDescriptor, SET, "(ILjava/lang/Object;)V", false);
        }

        mv.visitInsn(Opcodes.RETURN);
        if (iLoadOpcode == Opcodes.DLOAD || iLoadOpcode == Opcodes.LLOAD) {
            mv.visitMaxs(4, 3);
        } else {
            mv.visitMaxs(3, 2);
        }
        mv.visitEnd();
    }

    private void addWriteReplace(ClassWriter cw) {
        Method method;
        try {
            method = parentClass.getDeclaredMethod(WRITE_REPLACE, new Class[0]);
        } catch (NoSuchMethodException e) {
            return;
        }

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PROTECTED, method.getName(), Type.getMethodDescriptor(method), null, new String[] { Type.getInternalName(ObjectStreamException.class) });

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(parentClass), method.getName(), Type.getMethodDescriptor(method), false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

}
