/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

package org.eclipse.persistence.internal.dynamicpersist;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_STATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_SUPER;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ANEWARRAY;
import static org.eclipse.persistence.internal.libraries.asm.Constants.GETSTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_1;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKEVIRTUAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.IRETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.PUTFIELD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.PUTSTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.RETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.V1_5;

/**
 * <p>
 * <b>INTERNAL:</b> BaseEntitySubClassLoader uses ASM to dynamically
 * generate subclasses of {@link BaseEntity}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */

public class BaseEntityClassLoader extends ClassLoader {

    private static final String BASE_ENTITY_CLASSNAME_SLASHES =
        BaseEntity.class.getName().replace('.', '/');
    
    public static final String COLLECTION_WRAPPER_SUFFIX = 
        "_CollectionWrapper";
    private static final String BASE_ENTITY_COLLECTION_WRAPPER_CLASSNAME_SLASHES =
        BaseEntityCollectionWrapper.class.getName().replace('.', '/');

    private Boolean generateSubclasses = Boolean.TRUE;

    public BaseEntityClassLoader() {
        this(BaseEntityClassLoader.class.getClassLoader());
    }

    public BaseEntityClassLoader(ClassLoader parentLoader) {
        super(parentLoader);
    }

    protected Class<?> findClass(String className) throws ClassNotFoundException {

        if (!generateSubclasses.booleanValue()) {
            throw new ClassNotFoundException(className);
        }
        try {
            byte[] data = generateClassBytes(className);
            return defineClass(className, data, 0, data.length);
        } catch (ClassFormatError e) {
            throw new ClassNotFoundException(className, e);
        }
    }

    protected byte[] generateClassBytes(String className) {
        /*
         * Pattern is as follows: public class Foo extends BaseEntity {
         *
         * public static int NUM_ATTRIBUTES = 1;
         *
         * public static void setNumAttributes(Integer numAttributes) {
         *   NUM_ATTRIBUTES = numAttributes.intValue();
         *   }
         *   public static int getNumAttributes() {
         *     return NUM_ATTRIBUTES;
         *   }
         *
         * public Foo() {
         *   super();
         *   fields = new Object[getNumAttributes()];
         *   }
         * }
         * Later, via reflection, the setNumAttributes method is called with the
         * correct number of attributes
         */
        String classNameAsSlashes = className.replace('.', '/');
        ClassWriter cw = new ClassWriter(true);
        CodeVisitor cv;

        if (className.endsWith(COLLECTION_WRAPPER_SUFFIX)) {
            cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes,
                BASE_ENTITY_COLLECTION_WRAPPER_CLASSNAME_SLASHES, null, null);

            cv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitMethodInsn(INVOKESPECIAL, BASE_ENTITY_COLLECTION_WRAPPER_CLASSNAME_SLASHES,
                "<init>", "()V");
            cv.visitInsn(RETURN);
            cv.visitMaxs(0, 0);
        }

        else {
            cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes, BASE_ENTITY_CLASSNAME_SLASHES,
                null, null);
           
            cw.visitField(ACC_PUBLIC + ACC_STATIC, "NUM_ATTRIBUTES", "I", null, null);
            cv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            cv.visitInsn(ICONST_1);
            cv.visitFieldInsn(PUTSTATIC, classNameAsSlashes, "NUM_ATTRIBUTES", "I");
            cv.visitInsn(RETURN);
            cv.visitMaxs(0, 0);
    
            cv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "setNumAttributes", "(Ljava/lang/Integer;)V",
                null, null);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
            cv.visitFieldInsn(PUTSTATIC, classNameAsSlashes, "NUM_ATTRIBUTES", "I");
            cv.visitInsn(RETURN);
            cv.visitMaxs(0, 0);
    
            cv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "getNumAttributes", "()I", null, null);
            cv.visitFieldInsn(GETSTATIC, classNameAsSlashes, "NUM_ATTRIBUTES", "I");
            cv.visitInsn(IRETURN);
            cv.visitMaxs(0, 0);
    
            cv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitMethodInsn(INVOKESPECIAL, BASE_ENTITY_CLASSNAME_SLASHES, "<init>", "()V");
            cv.visitVarInsn(ALOAD, 0);
            cv.visitMethodInsn(INVOKESTATIC, classNameAsSlashes, "getNumAttributes", "()I");
            cv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            cv.visitFieldInsn(PUTFIELD, classNameAsSlashes, "fields", "[Ljava/lang/Object;");
            cv.visitInsn(RETURN);
            cv.visitMaxs(0, 0);
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    public void dontGenerateSubclasses() {
        this.generateSubclasses = Boolean.TRUE;
    }
}
