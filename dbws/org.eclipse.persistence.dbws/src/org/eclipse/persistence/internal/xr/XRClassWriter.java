/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     mnorman - convert DBWS to use new EclipseLink public Dynamic Persistence APIs
 ******************************************************************************/
package org.eclipse.persistence.internal.xr;

//javase imports

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicClassWriter;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import static org.eclipse.persistence.internal.dynamic.DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_STATIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_SUPER;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ARETURN;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.DUP;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.GETSTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.NEW;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.PUTSTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.RETURN;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.V1_5;
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
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        MethodVisitor mv;

        // special-case: build sub-class of XRDynamicEntityCollection
        if (className.endsWith(COLLECTION_WRAPPER_SUFFIX)) {
            cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes, null, XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES, null);
            mv = cw.visitMethod(ACC_PUBLIC, INIT, "()V", null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES, INIT, "()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        } else {
            // public class Foo extends XRDynamicEntity {
            cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes, null, XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES, null);

            // public static XRDynamicPropertiesManager DPM = new
            // XRDynamicPropertiesManager();
            cw.visitField(ACC_PUBLIC + ACC_STATIC, PROPERTIES_MANAGER_FIELD, "L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);

            mv = cw.visitMethod(ACC_STATIC, CLINIT, "()V", null, null);
            mv.visitTypeInsn(NEW, XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES, INIT, "()V");
            mv.visitFieldInsn(PUTSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD, "L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            // public Foo() {
            // super();
            // }
            mv = cw.visitMethod(ACC_PUBLIC, INIT, "()V", null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES, INIT, "()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            // public XRDynamicPropertiesManager fetchPropertiesManager() {
            mv = cw.visitMethod(ACC_PUBLIC, "fetchPropertiesManager", "()L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);
            mv.visitFieldInsn(GETSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD, "L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
            mv.visitInsn(ARETURN);
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