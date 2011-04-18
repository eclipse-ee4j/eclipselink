/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.libraries.asm.attrs.SignatureAttribute;
import static org.eclipse.persistence.internal.dynamic.DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.AASTORE;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_ENUM;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_FINAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_PRIVATE;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_STATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_SUPER;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_SYNTHETIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ANEWARRAY;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ARETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.BIPUSH;
import static org.eclipse.persistence.internal.libraries.asm.Constants.CHECKCAST;
import static org.eclipse.persistence.internal.libraries.asm.Constants.DUP;
import static org.eclipse.persistence.internal.libraries.asm.Constants.GETSTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_0;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_1;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_2;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_3;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_4;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_5;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ILOAD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKEVIRTUAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.NEW;
import static org.eclipse.persistence.internal.libraries.asm.Constants.PUTSTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.RETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.V1_5;

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
public class DynamicClassWriter {
    
    /*
     * Pattern is as follows:
     * <pre>
     * public class Foo extends DynamicEntityImpl {
     *
     *     public static DynamicPropertiesManager DPM = new DynamicPropertiesManager();
     *     
     *     public Foo() {
     *         super();
     *     }
     *     public DynamicPropertiesManager fetchPropertiesManager() {
     *         return DPM;
     *     }
     * }
     * 
     * later on, the DPM field is populated:
     *     Field dpmField = myDynamicClass.getField(DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD);
     *     DynamicPropertiesManager dpm = (DynamicPropertiesManager)dpmField.get(null);
     *     dpm.setType(...)
     * </pre>
     */
    
    protected static final String DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES =
       DynamicPropertiesManager.class.getName().replace('.', '/');
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
        if (parent == null || parent.isPrimitive() || parent.isArray() || parent.isEnum() ||
            parent.isInterface() || Modifier.isFinal(parent.getModifiers())) {
            throw new IllegalArgumentException("Invalid parent class: " + parent);
        }
        String classNameAsSlashes = className.replace('.', '/');
        String parentClassNameAsSlashes = parentClassName.replace('.', '/');
        
        ClassWriter cw = new ClassWriter(true);
        CodeVisitor cv;
        
        // public class Foo extends DynamicEntityImpl {
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes, parentClassNameAsSlashes, null,
            null);
        
        // public static DynamicPropertiesManager DPM = new DynamicPropertiesManager();
        cw.visitField(ACC_PUBLIC + ACC_STATIC, PROPERTIES_MANAGER_FIELD,
            "L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);
        cv = cw.visitMethod(ACC_STATIC, CLINIT, "()V", null, null);
        cv.visitTypeInsn(NEW, DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES);
        cv.visitInsn(DUP);
        cv.visitMethodInsn(INVOKESPECIAL, DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES, INIT, "()V");
        cv.visitFieldInsn(PUTSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD,
            "L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
        cv.visitInsn(RETURN);
        cv.visitMaxs(0, 0);
        
        // public Foo() {
        //     super();
        // }
        cv = cw.visitMethod(ACC_PUBLIC, INIT, "()V", null, null);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitMethodInsn(INVOKESPECIAL, parentClassNameAsSlashes, INIT, "()V");
        cv.visitInsn(RETURN);
        cv.visitMaxs(0, 0);
        
        cv = cw.visitMethod(ACC_PUBLIC, "fetchPropertiesManager", 
            "()L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);
        cv.visitFieldInsn(GETSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD,
            "L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
        cv.visitInsn(ARETURN);
        cv.visitMaxs(0, 0);
        
        cw.visitEnd();
        return cw.toByteArray();
        
    }

    public static int[] ICONST = 
        new int[] { ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5 };
    protected byte[] createEnum(EnumInfo enumInfo) {

        String[] enumValues = enumInfo.getLiteralLabels();
        String className = enumInfo.getClassName();

        String internalClassName = className.replace('.', '/');

        CodeVisitor cv;

        ClassWriter cw = new ClassWriter(true);
        cw.visit(V1_5, ACC_PUBLIC + ACC_FINAL + ACC_SUPER + ACC_ENUM, internalClassName,
            "java/lang/Enum", null, null);

        // Add the individual enum values
        for (String enumValue : enumValues) {
            cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC + ACC_ENUM, enumValue, "L"
                + internalClassName + ";", null, null);
        }

        // add the synthetic "$VALUES" field
        cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC + ACC_SYNTHETIC, "$VALUES", "[L"
            + internalClassName + ";", null, null);

        // Add the "values()" method
        cv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "values", "()[L" + internalClassName + ";",
            null, null);
        cv.visitFieldInsn(GETSTATIC, internalClassName, "$VALUES", "[L" + internalClassName + ";");
        cv.visitMethodInsn(INVOKEVIRTUAL, "[L" + internalClassName + ";", "clone",
            "()Ljava/lang/Object;");
        cv.visitTypeInsn(CHECKCAST, "[L" + internalClassName + ";");
        cv.visitInsn(ARETURN);
        cv.visitMaxs(1, 0);

        // Add the "valueOf()" method
        cv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "valueOf", "(Ljava/lang/String;)L"
            + internalClassName + ";", null, null);
        cv.visitLdcInsn(Type.getType("L" + internalClassName + ";"));
        cv.visitVarInsn(ALOAD, 0);
        cv.visitMethodInsn(INVOKESTATIC, "java/lang/Enum", "valueOf",
            "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;");
        cv.visitTypeInsn(CHECKCAST, internalClassName);
        cv.visitInsn(ARETURN);
        cv.visitMaxs(2, 1);

        // Add constructors
        SignatureAttribute methodAttrs1 = new SignatureAttribute("()V");
        cv = cw.visitMethod(ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", null, methodAttrs1);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitVarInsn(ALOAD, 1);
        cv.visitVarInsn(ILOAD, 2);
        cv.visitMethodInsn(INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V");
        cv.visitInsn(RETURN);
        cv.visitMaxs(3, 3);

        // Add enum constants
        cv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);

        int lastCount = 0;
        for (int i = 0; i < enumValues.length; i++) {
            String enumValue = enumValues[i];
            cv.visitTypeInsn(NEW, internalClassName);
            cv.visitInsn(DUP);
            cv.visitLdcInsn(enumValue);
            if (i <= 5) {
                cv.visitInsn(ICONST[i]);
            }
            else {
                cv.visitIntInsn(BIPUSH, i);
            }
            cv.visitMethodInsn(INVOKESPECIAL, internalClassName, "<init>",
                    "(Ljava/lang/String;I)V");
            cv.visitFieldInsn(PUTSTATIC, internalClassName, enumValue, "L" + internalClassName
                + ";");
            lastCount = i;
        }

        if (lastCount < 5) {
            cv.visitInsn(ICONST[lastCount + 1]);
        }
        else {
            cv.visitIntInsn(BIPUSH, lastCount + 1);
        }
        cv.visitTypeInsn(ANEWARRAY, internalClassName);

        for (int i = 0; i < enumValues.length; i++) {
            String enumValue = enumValues[i];
            cv.visitInsn(DUP);
            if (i <= 5) {
                cv.visitInsn(ICONST[i]);
            }
            else {
                cv.visitIntInsn(BIPUSH, i);
            }
            cv.visitFieldInsn(GETSTATIC, internalClassName, enumValue, "L" + internalClassName
                + ";");
            cv.visitInsn(AASTORE);
        }
        cv.visitFieldInsn(PUTSTATIC, internalClassName, "$VALUES", "[L" + internalClassName + ";");
        cv.visitInsn(RETURN);
        cv.visitMaxs(4, 0);

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
    protected boolean isCompatible(DynamicClassWriter writer) {
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
