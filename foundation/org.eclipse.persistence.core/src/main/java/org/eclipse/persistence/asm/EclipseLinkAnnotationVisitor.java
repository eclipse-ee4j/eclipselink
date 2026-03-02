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
package org.eclipse.persistence.asm;

public abstract class EclipseLinkAnnotationVisitor extends AnnotationVisitor {

    private AnnotationVisitor annotationVisitor;

    public EclipseLinkAnnotationVisitor() {
        this.annotationVisitor = ASMFactory.createAnnotationVisitor(Opcodes.ASM_API_SELECTED);
    }

    public EclipseLinkAnnotationVisitor(AnnotationVisitor annotationVisitor) {
        this.annotationVisitor = ASMFactory.createAnnotationVisitor(Opcodes.ASM_API_SELECTED, annotationVisitor);
    }

    @Override
    public void visit(String name, Object value) {
        this.annotationVisitor.visit(name, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        this.annotationVisitor.visitEnum(name, desc, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return this.annotationVisitor.visitAnnotation(name, desc);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return this.annotationVisitor.visitArray(name);
    }

    @Override
    public void visitEnd() {
        this.annotationVisitor.visitEnd();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T unwrap() {
        if (this.annotationVisitor instanceof org.eclipse.persistence.asm.internal.platform.ow2.AnnotationVisitorImpl) {
            return (T)((org.eclipse.persistence.asm.internal.platform.ow2.AnnotationVisitorImpl)this.annotationVisitor).getInternal(this.customAnnotationVisitor);
        } else {
            return null;
        }
    }
}
