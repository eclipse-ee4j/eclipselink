/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial API and implementation from Oracle TopLink
//     19/04/2014-2.6 Lukas Jungmann
//       - 429992: JavaSE 8/ASM 5.0.1 support (EclipseLink silently ignores Entity classes with lambda expressions)
package org.eclipse.persistence.internal.jpa.rs.weaving;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.EclipseLinkClassWriter;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;

public class RestAdapterClassWriter implements EclipseLinkClassWriter, Opcodes {

    public static final String CLASS_NAME_SUFFIX = "PersistenceRestAdapter";
    public static final String REFERENCE_ADAPTER_SHORT_SIGNATURE = "org/eclipse/persistence/jpa/rs/util/xmladapters/ReferenceAdapter";

    /**
     * Creates a class name that can be used for a ReferenceAdapter subclass for the given
     * classname.
     * @param className
     * @return
     */
    public static String constructClassNameForReferenceAdapter(String className){
        String packageName = className.lastIndexOf('.') >= 0 ? className.substring(0, className.lastIndexOf('.')) : "";
        String shortClassName = className.lastIndexOf('.') >= 0 ? className.substring(className.lastIndexOf('.') + 1) : className;
        return packageName + "._" + shortClassName + CLASS_NAME_SUFFIX;

    }

    protected String parentClassName;

    public RestAdapterClassWriter(String parentClassName){
        this.parentClassName = parentClassName;
    }

    public String getClassName(){
        return constructClassNameForReferenceAdapter(parentClassName);
    }

    public String getASMParentClassName(){
        return parentClassName.replace('.', '/');
    }

    public String getASMClassName(){
        return getClassName().replace('.', '/');
    }

    @Override
    public byte[] writeClass(DynamicClassLoader loader, String className)
            throws ClassNotFoundException {

        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, getASMClassName(), "L" + REFERENCE_ADAPTER_SHORT_SIGNATURE + "<L" + getASMParentClassName() + ";>;", REFERENCE_ADAPTER_SHORT_SIGNATURE, null);

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, REFERENCE_ADAPTER_SHORT_SIGNATURE, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;Lorg/eclipse/persistence/jpa/rs/PersistenceContext;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, REFERENCE_ADAPTER_SHORT_SIGNATURE, "<init>", "(Ljava/lang/String;Lorg/eclipse/persistence/jpa/rs/PersistenceContext;)V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();

        cw.visitEnd();

        return cw.toByteArray();
    }

    @Override
    public boolean isCompatible(EclipseLinkClassWriter writer) {
        return getParentClassName().equals(writer.getParentClassName());
    }

    @Override
    public Class<?> getParentClass() {
        return null;
    }

    @Override
    public String getParentClassName() {
        return parentClassName;
    }

}

