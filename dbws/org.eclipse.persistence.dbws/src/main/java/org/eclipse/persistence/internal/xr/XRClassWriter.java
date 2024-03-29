/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     mnorman - convert DBWS to use new EclipseLink public Dynamic Persistence APIs
package org.eclipse.persistence.internal.xr;

//javase imports

//EclipseLink imports
import org.eclipse.persistence.asm.ClassWriter;
import org.eclipse.persistence.asm.EclipseLinkASMClassWriter;
import org.eclipse.persistence.asm.MethodVisitor;
import org.eclipse.persistence.asm.Opcodes;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicClassWriter;

import static org.eclipse.persistence.internal.dynamic.DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;

/**
 * <p>
 * <b>INTERNAL:</b> XRClassWriter uses ASM to dynamically generate subclasses of
 * {@link XRDynamicEntity}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class XRClassWriter extends DynamicClassWriter {

    static final String XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES = XRDynamicEntity.class.getName().replace('.', '/');
    static final String XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES = XRDynamicEntity_CollectionWrapper.class.getName().replace('.', '/');
    static final String XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES = XRDynamicPropertiesManager.class.getName().replace('.', '/');

    public XRClassWriter() {
        super();
    }

    /*
     * Pattern is as follows: <pre> public class Foo extends XRDynamicEntity {
     * public static XRDynamicPropertiesManager DPM = new
     * XRDynamicPropertiesManager(); public Foo() { super(); } public
     * XRDynamicPropertiesManager fetchPropertiesManager() { return DPM; } }
     *
     * later on, the DPM field is populated: XRDynamicEntity newInstance =
     * fooClz.newInstance(); XRDynamicPropertiesManager dpm =
     * newInstance.getPropertiesManager(); Set<String> propertyNames = new
     * HashSet<String(); propertyNames.add("prop1"); propertyNames.add("prop2");
     * dpm.setPropertiesNameSet(propertyNames); </pre>
     */

    @Override
    public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException {

        String classNameAsSlashes = className.replace('.', '/');
        ClassWriter cw = new EclipseLinkASMClassWriter();
        MethodVisitor mv;

        // special-case: build sub-class of XRDynamicEntityCollection
        if (className.endsWith(COLLECTION_WRAPPER_SUFFIX)) {
            cw.visit(Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNameAsSlashes, null, XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES, null);
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, INIT, "()V", null, null);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES, INIT, "()V", false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        } else {
            // public class Foo extends XRDynamicEntity {
            cw.visit(Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classNameAsSlashes, null, XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES, null);

            // public static XRDynamicPropertiesManager DPM = new
            // XRDynamicPropertiesManager();
            cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, PROPERTIES_MANAGER_FIELD, "L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);

            mv = cw.visitMethod(Opcodes.ACC_STATIC, CLINIT, "()V", null, null);
            mv.visitTypeInsn(Opcodes.NEW, XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES);
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES, INIT, "()V", false);
            mv.visitFieldInsn(Opcodes.PUTSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD, "L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            // public Foo() {
            // super();
            // }
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, INIT, "()V", null, null);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES, INIT, "()V", false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            // public XRDynamicPropertiesManager fetchPropertiesManager() {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "fetchPropertiesManager", "()L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);
            mv.visitFieldInsn(Opcodes.GETSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD, "L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    @Override
    protected boolean verify(Class<?> dynamicClass, ClassLoader loader) throws ClassNotFoundException {
        return dynamicClass != null && XRDynamicEntity.class.isAssignableFrom(dynamicClass);
    }
}
