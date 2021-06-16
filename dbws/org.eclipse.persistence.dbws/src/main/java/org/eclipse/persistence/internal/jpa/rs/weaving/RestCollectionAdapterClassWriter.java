/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.internal.jpa.rs.weaving;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.EclipseLinkClassWriter;
import org.eclipse.persistence.internal.libraries.asm.EclipseLinkASMClassWriter;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;

/**
 * This class is used to generate XML type adapters for collection references in JPARS 2.0.
 * The generated classes are subclasses of {@link org.eclipse.persistence.jpa.rs.util.xmladapters.RestCollectionAdapter}.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class RestCollectionAdapterClassWriter implements EclipseLinkClassWriter, Opcodes {
    private static final String CLASS_NAME_SUFFIX = "RestCollectionAdapter";
    private static final String REFERENCE_ADAPTER_SHORT_SIGNATURE = "org/eclipse/persistence/jpa/rs/util/xmladapters/RestCollectionAdapter";

    private String parentClassName;

    /**
     * Creates a new RestCollectionAdapterClassWriter.
     *
     * @param parentClassName superclass name.
     */
    public RestCollectionAdapterClassWriter(String parentClassName){
        this.parentClassName = parentClassName;
    }

    /**
     * Returns a class name for a RestCollectionAdapter for given class.
     * The name is constructed as _&lt;className&gt;RestCollectionAdapter.
     *
     * @param className class name of the class to get RestCollectionAdapter name for.
     * @return RestCollectionAdapter name.
     */
    public static String getClassName(String className){
        final int index = className.lastIndexOf('.');
        final String packageName = index >= 0 ? className.substring(0, index) : "";
        final String shortClassName = index >= 0 ? className.substring(index + 1) : className;
        return packageName + "._" + shortClassName + CLASS_NAME_SUFFIX;
    }

    /**
     * Returns a class name for a RestCollectionAdapter.
     * {@link #getClassName(String)}
     *
     * @return RestCollectionAdapter name.
     */
    public String getClassName() {
        return getClassName(parentClassName);
    }

    /**
     * public class _EntityRestCollectionAdapter extends RestCollectionAdapter&lt;Entity&gt; {
     *     public _EntityRestCollectionAdapter() {
     *         super();
     *     }
     *
     *     public _EntityRestCollectionAdapter(PersistentContext context) {
     *         super(context);
     *     }
     * }
     *
     * @param loader
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException {

        // Class signature
        final EclipseLinkASMClassWriter cw = new EclipseLinkASMClassWriter(0);
        cw.visit(ACC_PUBLIC + ACC_SUPER, getASMClassName(), "L" + REFERENCE_ADAPTER_SHORT_SIGNATURE + "<L" + getASMParentClassName() + ";>;", REFERENCE_ADAPTER_SHORT_SIGNATURE, null);

        // Default constructor
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, REFERENCE_ADAPTER_SHORT_SIGNATURE, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // Another constructor
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/eclipse/persistence/jpa/rs/PersistenceContext;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, REFERENCE_ADAPTER_SHORT_SIGNATURE, "<init>", "(Lorg/eclipse/persistence/jpa/rs/PersistenceContext;)V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

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

    private String getASMParentClassName() {
        return parentClassName.replace('.', '/');
    }

    private String getASMClassName() {
        return getClassName().replace('.', '/');
    }
}

