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
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     dclarke - Bug 387240: added field and method calls to allow extensibility
 *
 ******************************************************************************/
package org.eclipse.persistence.dynamic;

//javase imports
import java.lang.reflect.Modifier;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader.EnumInfo;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.dynamic.DynamicPropertiesManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Type;

import static org.eclipse.persistence.internal.dynamic.DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.AASTORE;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_ENUM;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_FINAL;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_PRIVATE;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_STATIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_SUPER;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_SYNTHETIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ANEWARRAY;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ARETURN;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.BIPUSH;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.SIPUSH;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.CHECKCAST;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.DUP;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.GETSTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ICONST_0;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ICONST_1;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ICONST_2;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ICONST_3;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ICONST_4;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ICONST_5;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ILOAD;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.INVOKESTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.INVOKEVIRTUAL;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.NEW;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.PUTSTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.RETURN;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.V1_5;

/**
 * Write the byte codes of a dynamic entity class. The class writer will create
 * the byte codes for a dynamic class that subclasses any provided class
 * replicating its constructors and writeReplace method (if one exists).
 * <p>
 * The intent is to provide a common writer for dynamic JPA entities but also
 * allow for subclasses of this to be used in more complex writing situations
 * such as SDO and DBWS.
 * <p>
 * Instances of this class and any subclasses are maintained within the
 * {@link DynamicClassLoader#getClassWriters()} and
 * {@link DynamicClassLoader#defaultWriter} for the life of the class loader so
 * it is important that no unnecessary state be maintained that may effect
 * memory usage.
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class DynamicClassWriter implements EclipseLinkClassWriter {

    /*
     * Pattern is as follows: <pre> public class Foo extends DynamicEntityImpl {
     * 
     * public static DynamicPropertiesManager DPM = new
     * DynamicPropertiesManager();
     * 
     * public Foo() { super(); } public DynamicPropertiesManager
     * fetchPropertiesManager() { return DPM; } }
     * 
     * later on, the DPM field is populated: Field dpmField =
     * myDynamicClass.getField
     * (DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD);
     * DynamicPropertiesManager dpm =
     * (DynamicPropertiesManager)dpmField.get(null); dpm.setType(...) </pre>
     */

    protected static final String DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES = DynamicPropertiesManager.class.getName().replace('.', '/');
    protected static final String INIT = "<init>";
    protected static final String CLINIT = "<clinit>";

    protected Class<?> parentClass;

    /**
     * Name of parent class. This is used only when the parent class is not
     * known at the time the dynamic class writer is registered. This is
     * generally only required when loading from an XML mapping file where the
     * order of class access is not known.
     */
    protected String parentClassName;

    public DynamicClassWriter() {
        this(DynamicEntityImpl.class);
    }

    public DynamicClassWriter(Class<?> parentClass) {
        this.parentClass = parentClass;
    }

    /**
     * Create using a loader and class name so that the parent class can be
     * lazily loaded when the writer is used to generate a dynamic class.
     * <p>
     * The loader must not be null and the parentClassName must not be null and
     * not an empty String. The parentClassName will be converted to a class
     * using the provided loader lazily.
     * 
     * @see #getParentClass()
     * @see DynamicException#illegalDynamicClassWriter(DynamicClassLoader,
     *      String)
     */
    public DynamicClassWriter(String parentClassName) {
        if (parentClassName == null || parentClassName.length() == 0) {
            throw DynamicException.illegalParentClassName(parentClassName);
        }
        this.parentClassName = parentClassName;
    }

    public Class<?> getParentClass() {
        return this.parentClass;
    }

    public String getParentClassName() {
        return this.parentClassName;
    }

    /**
     * Return the {@link #parentClass} converting the {@link #parentClassName}
     * using the provided loader if required.
     * 
     * @throws ClassNotFoundException
     *             if the parentClass is not available.
     */
    private Class<?> getParentClass(ClassLoader loader) throws ClassNotFoundException {
        if (parentClass == null && parentClassName != null) {
            parentClass = loader.loadClass(parentClassName);
        }
        return parentClass;
    }

    public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException {

        EnumInfo enumInfo = loader.enumInfoRegistry.get(className);
        if (enumInfo != null) {
            return createEnum(enumInfo);
        }

        Class<?> parent = getParentClass(loader);
        parentClassName = parent.getName();
        if (parent == null || parent.isPrimitive() || parent.isArray() || parent.isEnum() || parent.isInterface() || Modifier.isFinal(parent.getModifiers())) {
            throw new IllegalArgumentException("Invalid parent class: " + parent);
        }
        String classNameAsSlashes = className.replace('.', '/');
        String parentClassNameAsSlashes = parentClassName.replace('.', '/');

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // public class Foo extends DynamicEntityImpl {
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes, null, parentClassNameAsSlashes, null);

        // public static DynamicPropertiesManager DPM = new
        // DynamicPropertiesManager();
        cw.visitField(ACC_PUBLIC + ACC_STATIC, PROPERTIES_MANAGER_FIELD, "L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);
        MethodVisitor mv = cw.visitMethod(ACC_STATIC, CLINIT, "()V", null, null);
        mv.visitTypeInsn(NEW, DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES, INIT, "()V");
        mv.visitFieldInsn(PUTSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD, "L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);

        // public Foo() {
        // super();
        // }
        mv = cw.visitMethod(ACC_PUBLIC, INIT, "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, parentClassNameAsSlashes, INIT, "()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);

        mv = cw.visitMethod(ACC_PUBLIC, "fetchPropertiesManager", "()L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);
        mv.visitFieldInsn(GETSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD, "L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);

        addFields(cw, parentClassNameAsSlashes);
        addMethods(cw, parentClassNameAsSlashes);

        cw.visitEnd();
        return cw.toByteArray();

    }

    /**
     * Allow subclasses to add additional state to the dynamic entity.
     * 
     * @param cw
     * @param parentClassNameAsSlashes
     */
    protected void addFields(ClassWriter cw, String parentClassType) {
    }

    /**
     * Allow subclasses to add additional methods to the dynamic entity.
     * 
     * @param cw
     * @param parentClassNameAsSlashes
     */
    protected void addMethods(ClassWriter cw, String parentClassType) {
    }

    public static int[] ICONST = new int[] { ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5 };

    protected byte[] createEnum(EnumInfo enumInfo) {

        String[] enumValues = enumInfo.getLiteralLabels();
        String className = enumInfo.getClassName();

        String internalClassName = className.replace('.', '/');

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_5, ACC_PUBLIC + ACC_FINAL + ACC_SUPER + ACC_ENUM, internalClassName, null, "java/lang/Enum", null);

        // Add the individual enum values
        for (String enumValue : enumValues) {
            cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC + ACC_ENUM, enumValue, "L" + internalClassName + ";", null, null);
        }

        // add the synthetic "$VALUES" field
        cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC + ACC_SYNTHETIC, "$VALUES", "[L" + internalClassName + ";", null, null);

        // Add the "values()" method
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "values", "()[L" + internalClassName + ";", null, null);
        mv.visitFieldInsn(GETSTATIC, internalClassName, "$VALUES", "[L" + internalClassName + ";");
        mv.visitMethodInsn(INVOKEVIRTUAL, "[L" + internalClassName + ";", "clone", "()Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, "[L" + internalClassName + ";");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 0);

        // Add the "valueOf()" method
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "valueOf", "(Ljava/lang/String;)L" + internalClassName + ";", null, null);
        mv.visitLdcInsn(Type.getType("L" + internalClassName + ";"));
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;");
        mv.visitTypeInsn(CHECKCAST, internalClassName);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 1);

        // Add constructors
        // SignatureAttribute methodAttrs1 = new SignatureAttribute("()V");
        mv = cw.visitMethod(ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 3);

        // Add enum constants
        mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);

        int lastCount = 0;
        for (int i = 0; i < enumValues.length; i++) {
            String enumValue = enumValues[i];
            mv.visitTypeInsn(NEW, internalClassName);
            mv.visitInsn(DUP);
            mv.visitLdcInsn(enumValue);
            if (i <= 5) {
                mv.visitInsn(ICONST[i]);
            } else if (i <= 127) {
                mv.visitIntInsn(BIPUSH, i);
            } else {
                mv.visitIntInsn(SIPUSH, i);
            }
            mv.visitMethodInsn(INVOKESPECIAL, internalClassName, "<init>", "(Ljava/lang/String;I)V");
            mv.visitFieldInsn(PUTSTATIC, internalClassName, enumValue, "L" + internalClassName + ";");
            lastCount = i;
        }

        if (lastCount < 5) {
            mv.visitInsn(ICONST[lastCount + 1]);
        } else if (lastCount < 127) {
            mv.visitIntInsn(BIPUSH, lastCount + 1);
        } else {
            mv.visitIntInsn(SIPUSH, lastCount + 1);
        }
        mv.visitTypeInsn(ANEWARRAY, internalClassName);

        for (int i = 0; i < enumValues.length; i++) {
            String enumValue = enumValues[i];
            mv.visitInsn(DUP);
            if (i <= 5) {
                mv.visitInsn(ICONST[i]);
            } else if (i <= 127) {
                mv.visitIntInsn(BIPUSH, i);
            } else {
                mv.visitIntInsn(SIPUSH, i);
            }
            mv.visitFieldInsn(GETSTATIC, internalClassName, enumValue, "L" + internalClassName + ";");
            mv.visitInsn(AASTORE);
        }
        mv.visitFieldInsn(PUTSTATIC, internalClassName, "$VALUES", "[L" + internalClassName + ";");
        mv.visitInsn(RETURN);
        mv.visitMaxs(4, 0);

        cw.visitEnd();
        return cw.toByteArray();
    }

    /**
     * Verify that the provided class meets the requirements of the writer. In
     * the case of {@link DynamicClassWriter} this will ensure that the class is
     * a subclass of the {@link #parentClass}
     * 
     * @param dynamicClass
     * @throws ClassNotFoundException
     */
    protected boolean verify(Class<?> dynamicClass, ClassLoader loader) throws ClassNotFoundException {
        Class<?> parent = getParentClass(loader);
        return dynamicClass != null && parent.isAssignableFrom(dynamicClass);
    }

    /**
     * Interfaces the dynamic entity class implements. By default this is none
     * but in the case of SDO a concrete interface must be implemented.
     * Subclasses should override this as required.
     * 
     * @return Interfaces implemented by Dynamic class. May be null
     */
    protected String[] getInterfaces() {
        return null;
    }

    /**
     * Create a copy of this {@link DynamicClassWriter} but with a different
     * parent class.
     * 
     * @see DynamicClassLoader#addClass(String, Class)
     */
    protected DynamicClassWriter createCopy(Class<?> parentClass) {
        return new DynamicClassWriter(parentClass);
    }

    /**
     * Verify that the provided writer is compatible with the current writer.
     * Returning true means that the bytes that would be created using this
     * writer are identical with what would come from the provided writer.
     * <p>
     * Used in {@link DynamicClassLoader#addClass(String, DynamicClassWriter)}
     * to verify if a duplicate request of the same className can proceed and
     * return the same class that may already exist.
     */
    public boolean isCompatible(EclipseLinkClassWriter writer) {
        if (writer == null) {
            return false;
        }
        // Ensure writers are the exact same class. If subclasses do not alter
        // the bytes created then they must override this method and not return
        // false on this check.
        if (getClass() != writer.getClass()) {
            return false;
        }
        if (getParentClass() == null) {
            return getParentClassName() != null && getParentClassName().equals(writer.getParentClassName());
        }
        return getParentClass() == writer.getParentClass();
    }

    @Override
    public String toString() {
        String parentName = getParentClass() == null ? getParentClassName() : getParentClass().getName();
        return Helper.getShortClassName(getClass()) + "(" + parentName + ")";
    }
}
