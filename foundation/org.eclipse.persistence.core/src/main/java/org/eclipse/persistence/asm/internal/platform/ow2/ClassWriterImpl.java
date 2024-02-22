/*
 * Copyright (c) 2023, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.asm.internal.platform.ow2;

import org.eclipse.persistence.asm.AnnotationVisitor;
import org.eclipse.persistence.asm.FieldVisitor;
import org.eclipse.persistence.asm.MethodVisitor;
import org.eclipse.persistence.asm.Opcodes;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class ClassWriterImpl extends org.eclipse.persistence.asm.ClassWriter {

    private class OW2ClassWriter extends ClassWriter {

        public OW2ClassWriter(final int flags) {
            super(flags);
        }

        public OW2ClassWriter(final ClassReader classReader, final int flags) {
            super(classReader, flags);
        }

        @Override
        protected String getCommonSuperClass(final String type1, final String type2) {
            if (customClassWriter == null) {
                return super.getCommonSuperClass(type1, type2);
            } else {
                return customClassWriter.getCommonSuperClass(type1, type2);
            }
        }

        public org.objectweb.asm.AnnotationVisitor visitAnnotationSuper(final String descriptor, final boolean visible) {
            return super.visitAnnotation(descriptor, visible);
        }

        public org.objectweb.asm.FieldVisitor visitFieldSuper(int access, String name, String desc, String signature, Object value) {
            return super.visitField(access, name, desc, signature, value);
        }

        public org.objectweb.asm.MethodVisitor visitMethodSuper(int access, String name, String desc, String signature, String[] exceptions) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public byte[] toByteArray() {
            if (customClassWriter == null) {
                return super.toByteArray();
            } else {
                return customClassWriter.toByteArray();
            }
        }

        public byte[] toByteArraySuper() {
            return super.toByteArray();
        }
    }

    private OW2ClassWriter ow2ClassWriter;

    public ClassWriterImpl() {
        this.ow2ClassWriter = new OW2ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_FRAMES);
    }

    public ClassWriterImpl(final int flags) {
        this(null, flags);
    }

    public ClassWriterImpl(final org.eclipse.persistence.asm.ClassReader classReader, final int flags) {
        ow2ClassWriter = new OW2ClassWriter((classReader != null) ? classReader.unwrap() : null, flags);
    }

    public ClassWriter getInternal(final org.eclipse.persistence.asm.ClassWriter classWriter) {
        this.customClassWriter = classWriter;
        return this.ow2ClassWriter;
    }

    @Override
    public String getCommonSuperClass(final String type1, final String type2) {
        return this.ow2ClassWriter.getCommonSuperClass(type1, type2);
    }

    @Override
    public void visit(final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.visit(Opcodes.JAVA_CLASS_VERSION, access, name, signature, superName, interfaces);
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.ow2ClassWriter.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return new AnnotationVisitorImpl(ow2ClassWriter.visitAnnotation(descriptor, visible));
    }

    @Override
    public AnnotationVisitor visitAnnotationSuper(String descriptor, boolean visible) {
        return new AnnotationVisitorImpl(ow2ClassWriter.visitAnnotationSuper(descriptor, visible));
    }

    @Override
    public FieldVisitor visitField(final int access, final String name, final String descriptor, final String signature, final Object value) {
        return new FieldVisitorImpl(ow2ClassWriter.visitField(access, name, descriptor, signature, value));
    }

    @Override
    public FieldVisitor visitFieldSuper(final int access, final String name, final String descriptor, final String signature, final Object value) {
        return new FieldVisitorImpl(ow2ClassWriter.visitFieldSuper(access, name, descriptor, signature, value));
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
        return new MethodVisitorImpl(ow2ClassWriter.visitMethod(access, name, descriptor, signature, exceptions));
    }

    @Override
    public MethodVisitor visitMethodSuper(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
        return new MethodVisitorImpl(ow2ClassWriter.visitMethodSuper(access, name, descriptor, signature, exceptions));
    }

    @Override
    public void visitEnd() {
        ow2ClassWriter.visitEnd();
    }

    @Override
    public byte[] toByteArray() {
        return ow2ClassWriter.toByteArray();
    }

    @Override
    public byte[] toByteArraySuper() {
        return ow2ClassWriter.toByteArraySuper();
    }

        @Override
    public <T> T unwrap() {
        return (T)this.ow2ClassWriter;
    }
}
