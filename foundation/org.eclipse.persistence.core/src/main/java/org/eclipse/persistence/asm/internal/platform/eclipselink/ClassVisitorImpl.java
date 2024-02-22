/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.asm.internal.platform.eclipselink;

import org.eclipse.persistence.asm.AnnotationVisitor;
import org.eclipse.persistence.asm.FieldVisitor;
import org.eclipse.persistence.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.ClassVisitor;

public class ClassVisitorImpl extends org.eclipse.persistence.asm.ClassVisitor {

    private class ElClassVisitor extends ClassVisitor {

        public ElClassVisitor(final int api) {
            super(api, null);
        }

        public ElClassVisitor(final int api, final ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (customClassVisitor == null) {
                super.visit(version, access, name, signature, superName, interfaces);
            } else {
                customClassVisitor.visit(version, access, name, signature, superName, interfaces);
            }
        }

        public void visitSuper(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
            if (customClassVisitor == null) {
                return super.visitAnnotation(descriptor, visible);
            } else {
                AnnotationVisitor annotationVisitor = customClassVisitor.visitAnnotation(descriptor, visible);
                //In case of lazy like org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass.m_isLazy == true annotationVisitor is null
                return (annotationVisitor != null) ? annotationVisitor.unwrap() : null;
            }
        }

        public org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor visitAnnotationSuper(final String descriptor, final boolean visible) {
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public org.eclipse.persistence.internal.libraries.asm.FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (customClassVisitor == null) {
                return super.visitField(access, name, desc, signature, value);
            } else {
                FieldVisitor fieldVisitor = customClassVisitor.visitField(access, name, desc, signature, value);
                //In case of lazy like org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass.m_isLazy == true fieldVisitor is null
                return (fieldVisitor != null) ? fieldVisitor.unwrap() : null;
            }
        }

        public org.eclipse.persistence.internal.libraries.asm.FieldVisitor visitFieldSuper(int access, String name, String desc, String signature, Object value) {
            return super.visitField(access, name, desc, signature, value);
        }

        @Override
        public org.eclipse.persistence.internal.libraries.asm.MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (customClassVisitor == null) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            } else {
                MethodVisitor methodVisitor = customClassVisitor.visitMethod(access, name, desc, signature, exceptions);
                //In case of lazy like org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass.m_isLazy == true methodVisitor is null
                return (methodVisitor != null) ? methodVisitor.unwrap() : null;
            }
        }

        public org.eclipse.persistence.internal.libraries.asm.MethodVisitor visitMethodSuper(int access, String name, String desc, String signature, String[] exceptions) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public  void visitEnd() {
            super.visitEnd();
            if (customClassVisitor != null) {
                //should lead into infinite loop if visitEnd() is not implemented in customClassVisitor
                customClassVisitor.visitEnd();
            }
        }
    }

    private ElClassVisitor elClassVisitor;

    public ClassVisitorImpl(final int api) {
        super.cv = this;
        this.elClassVisitor = new ElClassVisitor(api);
    }

    public ClassVisitorImpl(final int api, final org.eclipse.persistence.asm.ClassVisitor classVisitor) {
        super.cv = this;
        this.elClassVisitor = new ElClassVisitor(api, (classVisitor != null) ? classVisitor.unwrap() : null);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.elClassVisitor.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitSuper(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.elClassVisitor.visitSuper(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotationSuper(final String descriptor, final boolean visible) {
        return new AnnotationVisitorImpl(this.elClassVisitor.visitAnnotationSuper(descriptor, visible));
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new FieldVisitorImpl(this.elClassVisitor.visitField(access, name, desc, signature, value));
    }

    @Override
    public FieldVisitor visitFieldSuper(int access, String name, String desc, String signature, Object value) {
        return new FieldVisitorImpl(this.elClassVisitor.visitFieldSuper(access, name, desc, signature, value));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodVisitorImpl(this.elClassVisitor.visitMethod(access, name, desc, signature, exceptions));
    }

    @Override
    public MethodVisitor visitMethodSuper(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodVisitorImpl(this.elClassVisitor.visitMethodSuper(access, name, desc, signature, exceptions));
    }

    @Override
    public  void visitEnd() {
        this.elClassVisitor.visitEnd();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T unwrap() {
        return (T)this.elClassVisitor;
    }
}
