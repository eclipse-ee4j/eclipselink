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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.dbws;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ACC_SUPER;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.RETURN;
import static org.eclipse.persistence.internal.libraries.asm.Opcodes.V1_5;

/**
 * <p><b>INTERNAL</b>: A subclass of {@link ClassLoader} that exposes a build method to the hidden
 * private {@link ClassLoader#defineClass} method.
 *
 * This loader is only used to define auto-generated subclasses of the {@link SOAPResponse}
 * class; it should <b>never</b> be used to load actual existing classes.
 *
 * @author Mike Norman
 */
public class SOAPResponseClassLoader extends ClassLoader {

    public static final String SOAP_RESPONSE_CLASSNAME_SLASHES =
      SOAPResponse.class.getName().replace('.', '/');

    public SOAPResponseClassLoader(ClassLoader parent) {
      super(parent);
    }

    public Class<?> buildClass(String className) {
      byte[] data = generateClassBytes(className);
      return super.defineClass(className, data, 0, data.length);
    }

    protected byte[] generateClassBytes(String className) {
      /*
       * Pattern is as follows:
       *   public class 'classname' extends org.eclipse.persistence.internal.dbws.SOAPResponse {
       *     public 'classname'() {
       *       super();
       *     }
       *   }
       */
      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
      cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, null, SOAP_RESPONSE_CLASSNAME_SLASHES, null);

      MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKESPECIAL, SOAP_RESPONSE_CLASSNAME_SLASHES, "<init>", "()V");
      mv.visitInsn(RETURN);
      mv.visitMaxs(0, 0);
      mv.visitEnd();

      cw.visitEnd();
      return cw.toByteArray();
    }
}
