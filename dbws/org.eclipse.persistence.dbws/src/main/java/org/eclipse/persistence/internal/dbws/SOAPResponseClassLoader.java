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
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.dbws;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.asm.ClassWriter;
import org.eclipse.persistence.asm.EclipseLinkASMClassWriter;
import org.eclipse.persistence.asm.MethodVisitor;
import org.eclipse.persistence.asm.Opcodes;

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
      ClassWriter cw = new EclipseLinkASMClassWriter();
      cw.visit(Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, className, null, SOAP_RESPONSE_CLASSNAME_SLASHES, null);

      MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitMethodInsn(Opcodes.INVOKESPECIAL, SOAP_RESPONSE_CLASSNAME_SLASHES, "<init>", "()V", false);
      mv.visitInsn(Opcodes.RETURN);
      mv.visitMaxs(0, 0);
      mv.visitEnd();

      cw.visitEnd();
      return cw.toByteArray();
    }
}
