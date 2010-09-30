/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;
import static org.eclipse.persistence.internal.dynamic.DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_STATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_SUPER;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ARETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.DUP;
import static org.eclipse.persistence.internal.libraries.asm.Constants.GETSTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.NEW;
import static org.eclipse.persistence.internal.libraries.asm.Constants.PUTSTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.RETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.V1_5;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;

/**
 * <p>
 * <b>INTERNAL:</b> XRClassWriter uses ASM to dynamically
 * generate subclasses of {@link XRDynamicEntity}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class XRClassWriter extends DynamicClassWriter {

    static final String XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES =
        XRDynamicEntity.class.getName().replace('.', '/');
    static final String XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES =
        XRDynamicEntity_CollectionWrapper.class.getName().replace('.', '/');
    static final String XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES =
        XRDynamicPropertiesManager.class.getName().replace('.', '/');

    public XRClassWriter() {
        super();
    }

    /*
     * Pattern is as follows:
     * <pre>
     * public class Foo extends XRDynamicEntity {
     *     public static XRDynamicPropertiesManager DPM = new XRDynamicPropertiesManager();
     *     public Foo() {
     *         super();
     *     }
     *     public XRDynamicPropertiesManager fetchPropertiesManager() {
     *         return DPM;
     *     }
     * }
     *
     * later on, the DPM field is populated:
     *     XRDynamicEntity newInstance = fooClz.newInstance();
     *     XRDynamicPropertiesManager dpm = newInstance.getPropertiesManager();
     *     Set<String> propertyNames = new HashSet<String();
     *     propertyNames.add("prop1");
     *     propertyNames.add("prop2");
     *     dpm.setPropertiesNameSet(propertyNames);
     * </pre>
     */

    @Override
    public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException {

        String classNameAsSlashes = className.replace('.', '/');
        ClassWriter cw = new ClassWriter(true);
        CodeVisitor cv;
        // special-case: build sub-class of XRDynamicEntityCollection
        if (className.endsWith(COLLECTION_WRAPPER_SUFFIX)) {
                cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes,
                        XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES, null, null);
                cv = cw.visitMethod(ACC_PUBLIC, INIT, "()V", null, null);
                cv.visitVarInsn(ALOAD, 0);
                cv.visitMethodInsn(INVOKESPECIAL, XR_DYNAMIC_ENTITY_COLLECTION_CLASSNAME_SLASHES,
                    INIT, "()V");
                cv.visitInsn(RETURN);
                cv.visitMaxs(0, 0);
        }
        else {
        // public class Foo extends XRDynamicEntity {
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes,
            XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES, null, null);

        // public static XRDynamicPropertiesManager DPM = new XRDynamicPropertiesManager();
        cw.visitField(ACC_PUBLIC + ACC_STATIC, PROPERTIES_MANAGER_FIELD,
            "L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);
        cv = cw.visitMethod(ACC_STATIC, CLINIT, "()V", null, null);
        cv.visitTypeInsn(NEW, XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES);
        cv.visitInsn(DUP);
        cv.visitMethodInsn(INVOKESPECIAL, XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES, INIT, "()V");
        cv.visitFieldInsn(PUTSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD,
            "L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
        cv.visitInsn(RETURN);
        cv.visitMaxs(0, 0);

        // public Foo() {
        //     super();
        // }
        cv = cw.visitMethod(ACC_PUBLIC, INIT, "()V", null, null);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitMethodInsn(INVOKESPECIAL, XR_DYNAMIC_ENTITY_CLASSNAME_SLASHES, INIT, "()V");
        cv.visitInsn(RETURN);
        cv.visitMaxs(0, 0);

        // public XRDynamicPropertiesManager fetchPropertiesManager() {
        cv = cw.visitMethod(ACC_PUBLIC, "fetchPropertiesManager",
            "()L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);
        cv.visitFieldInsn(GETSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD,
            "L" + XR_DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
        cv.visitInsn(ARETURN);
        cv.visitMaxs(0, 0);
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    @Override
    protected boolean verify(Class<?> dynamicClass, ClassLoader loader)
        throws ClassNotFoundException {
        return dynamicClass != null && XRDynamicEntity.class.isAssignableFrom(dynamicClass);
    }
}