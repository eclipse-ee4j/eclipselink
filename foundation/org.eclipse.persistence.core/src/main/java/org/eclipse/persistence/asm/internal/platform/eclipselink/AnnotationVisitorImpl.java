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

import org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor;

public class AnnotationVisitorImpl extends org.eclipse.persistence.asm.AnnotationVisitor {

    private class ELAnnotationVisitor extends AnnotationVisitor {

        public ELAnnotationVisitor(int api) {
            super(api);
        }

        public ELAnnotationVisitor(int api, final AnnotationVisitor annotationVisitor) {
            super(api, annotationVisitor);
        }

        @Override
        public void visit(String name, Object value) {
            if (customAnnotationVisitor == null) {
                super.visit(name, value);
            } else {
                customAnnotationVisitor.visit(name, value);
            }
        }

        @Override
        public void visitEnum(String name, String desc, String value) {
            if (customAnnotationVisitor == null) {
                super.visitEnum(name, desc, value);
            } else {
                customAnnotationVisitor.visitEnum(name, desc, value);
            }
        }

        @Override
        public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
            if (customAnnotationVisitor == null) {
                return super.visitAnnotation(descriptor, descriptor);
            } else {
                org.eclipse.persistence.asm.AnnotationVisitor annotationVisitor = customAnnotationVisitor.visitAnnotation(name, descriptor);
                //In case of lazy like org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass.m_isLazy == true annotationVisitor is null
                return (annotationVisitor != null) ? annotationVisitor.unwrap() : null;
            }
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            if (customAnnotationVisitor == null) {
                return super.visitArray(name);
            } else {
                org.eclipse.persistence.asm.AnnotationVisitor annotationVisitor = customAnnotationVisitor.visitArray(name);
                //In case of lazy like org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass.m_isLazy == true annotationVisitor is null
                return (annotationVisitor != null) ? annotationVisitor.unwrap() : null;
            }
        }

        @Override
        public void visitEnd() {
            if (customAnnotationVisitor == null) {
                super.visitEnd();
            } else {
                //should lead into infinite loop if visitEnd() is not implemented in customClassVisitor
                customAnnotationVisitor.visitEnd();
            }
        }
    }

    AnnotationVisitor elAnnotationVisitor;

    public AnnotationVisitorImpl(final int api) {
        this.elAnnotationVisitor = new ELAnnotationVisitor(api);
    }

    public AnnotationVisitorImpl(final int api, final org.eclipse.persistence.asm.AnnotationVisitor annotationVisitor) {
        this.elAnnotationVisitor = new ELAnnotationVisitor(api, (annotationVisitor != null) ? annotationVisitor.unwrap() : null);
    }

    public AnnotationVisitorImpl(AnnotationVisitor annotationVisitor) {
        this.elAnnotationVisitor = annotationVisitor;
    }

    public AnnotationVisitor getInternal(final org.eclipse.persistence.asm.AnnotationVisitor annotationVisitor) {
        this.customAnnotationVisitor = annotationVisitor;
        return this.elAnnotationVisitor;
    }

    @Override
    public void visit(String name, Object value) {
        this.elAnnotationVisitor.visit(name, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        this.elAnnotationVisitor.visitEnum(name, desc, value);
    }

    @Override
    public org.eclipse.persistence.asm.AnnotationVisitor visitAnnotation(String name, String desc) {
        return new AnnotationVisitorImpl(this.elAnnotationVisitor.visitAnnotation(name, desc));
    }

    @Override
    public org.eclipse.persistence.asm.AnnotationVisitor visitArray(String name) {
        return new AnnotationVisitorImpl(this.elAnnotationVisitor.visitArray(name));
    }

    @Override
    public void visitEnd() {
        this.elAnnotationVisitor.visitEnd();
    }

    @Override
    public <T> T unwrap() {
        return (T) this.elAnnotationVisitor;
    }
}
