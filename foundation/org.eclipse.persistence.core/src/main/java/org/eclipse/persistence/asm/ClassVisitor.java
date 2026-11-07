/*
 * Copyright (c) 2023, 2026 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.asm;

public abstract class ClassVisitor {

    protected ClassVisitor cv;
    protected ClassVisitor customClassVisitor;

    public ClassVisitor() {
    }

    public ClassVisitor(final int api) {
        this.cv = ASMFactory.createClassVisitor(api);
    }

    public ClassVisitor(final int api, final ClassVisitor classVisitor) {
        this.cv = ASMFactory.createClassVisitor(api, classVisitor);
    }

    public void setCustomClassVisitor(ClassVisitor classVisitor) {
        this.cv.customClassVisitor = classVisitor;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.cv.visit(version, access, name, signature, superName, interfaces);
    }

    public void visitSuper(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.cv.visitSuper(version, access, name, signature, superName, interfaces);
    }

    /** Forward unhandled member-class entries without redispatching to the custom visitor. */
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        this.cv.visitInnerClassSuper(name, outerName, innerName, access);
    }

    public void visitInnerClassSuper(String name, String outerName, String innerName, int access) {
        this.cv.visitInnerClassSuper(name, outerName, innerName, access);
    }

    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        return this.cv.visitAnnotation(descriptor, visible);
    }

    public AnnotationVisitor visitAnnotationSuper(final String descriptor, final boolean visible) {
        return this.cv.visitAnnotationSuper(descriptor, visible);
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return this.cv.visitField(access, name, desc, signature, value);
    }

    public FieldVisitor visitFieldSuper(int access, String name, String desc, String signature, Object value) {
        return this.cv.visitFieldSuper(access, name, desc, signature, value);
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return this.cv.visitMethod(access, name, desc, signature, exceptions);
    }

    public MethodVisitor visitMethodSuper(int access, String name, String desc, String signature, String[] exceptions) {
        return this.cv.visitMethodSuper(access, name, desc, signature, exceptions);
    }

    public void visitEnd() {
        this.cv.visitEnd();
    }

    public <T> T unwrap() {
        return this.cv.unwrap();
    }
}
