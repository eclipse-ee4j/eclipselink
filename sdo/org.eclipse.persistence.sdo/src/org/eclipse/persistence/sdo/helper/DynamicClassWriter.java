/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Method;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;
import org.eclipse.persistence.internal.libraries.asm.Constants;
import org.eclipse.persistence.internal.libraries.asm.Type;
import commonj.sdo.helper.HelperContext;

/**
 * <p><b>Purpose</b>: Dynamically create a class for the given name as a subclass of the generic
 * superclass. This dynamically created subclass should only provide constructors
 * and a writeReplace() method.
 */
public class DynamicClassWriter {
    private Class parentClass;
    private String className;
    private SDOType type;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public DynamicClassWriter(String className, SDOType type, HelperContext aContext) {
        aHelperContext = aContext;
        this.parentClass = SDODataObject.class;
        this.className = className;
        this.type = type;
        initializeParentClass();
    }

    private void initializeParentClass() {
        if (type.isSubType()) {
            SDOType parentSDOType = (SDOType)type.getBaseTypes().get(0);
            String parentClassName = parentSDOType.getInstanceClassName() + SDOConstants.SDO_IMPL_NAME;
            try {
                parentClass = ((SDOXMLHelper)aHelperContext.getXMLHelper()).getLoader().loadClass(parentClassName, parentSDOType);
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

    public String getClassName() {
        return this.className;
    }

    /**
     * This is where the byte codes for the generic subclass are defined and the
     * class is created dynamically from them.
     */
    public byte[] createClass() {
        ClassWriter cw = new ClassWriter(false);

        cw.visit(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_SUPER, className.replace('.', '/'), Type.getType(parentClass).getInternalName(), null, null);

        addConstructors(cw);
        addWriteReplace(cw);
        cw.visitEnd();

        return cw.toByteArray();
    }

    private void addConstructors(ClassWriter cw) {        
        CodeVisitor mv = cw.visitMethod(Constants.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), new String[] { Type.getInternalName(Serializable.class) }, null);
        mv.visitVarInsn(Constants.ALOAD, 0);
        mv.visitMethodInsn(Constants.INVOKESPECIAL, Type.getType(parentClass).getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));
        mv.visitInsn(Constants.RETURN);
        mv.visitMaxs(1, 1);
    }

    // TODO: JIRA129: This static call must be refactored to return an instance of HelperContext or remove this method entirely
    private void addWriteReplace(ClassWriter cw) {
        Method method;
        try {
            method = parentClass.getDeclaredMethod("writeReplace", new Class[0]);
        } catch (NoSuchMethodException e) {
            return;
        }

        CodeVisitor mv = cw.visitMethod(Constants.ACC_PROTECTED, method.getName(), Type.getMethodDescriptor(method), new String[] { Type.getInternalName(ObjectStreamException.class) }, null);

        mv.visitVarInsn(Constants.ALOAD, 0);
        mv.visitMethodInsn(Constants.INVOKESPECIAL, Type.getInternalName(parentClass), method.getName(), Type.getMethodDescriptor(method));
        mv.visitInsn(Constants.ARETURN);
        mv.visitMaxs(1, 1);
    }
}