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
import org.eclipse.persistence.internal.libraries.asm.FieldVisitor;

public class FieldVisitorImpl extends org.eclipse.persistence.asm.FieldVisitor {

    private class ELFieldVisitor extends FieldVisitor {

        public ELFieldVisitor(final int api) {
            super(api);
        }

        public ELFieldVisitor(final int api, final FieldVisitor fieldVisitor) {
            super(api, fieldVisitor);
        }

        @Override
        public org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
            if (customFieldVisitor == null) {
                return super.visitAnnotation(descriptor, visible);
            } else {
                AnnotationVisitor annotationVisitor = customFieldVisitor.visitAnnotation(descriptor, visible);
                //In case of lazy like org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass.m_isLazy == true annotationVisitor is null
                return (annotationVisitor != null) ? annotationVisitor.unwrap() : null;
            }
        }

        @Override
        public void visitEnd() {
            if (customFieldVisitor == null) {
                super.visitEnd();
            } else {
                //should lead into infinite loop if visitEnd() is not implemented in customClassVisitor
                customFieldVisitor.visitEnd();
            }
        }
    }

    FieldVisitor elFieldVisitor;

    public FieldVisitorImpl(FieldVisitor fieldVisitor) {
        this.elFieldVisitor = fieldVisitor;
    }

    public FieldVisitorImpl(final int api) {
        this.elFieldVisitor = new ELFieldVisitor(api);
    }

    public FieldVisitorImpl(final int api, final org.eclipse.persistence.asm.FieldVisitor fieldVisitor) {
        this.elFieldVisitor = new ELFieldVisitor(api, (fieldVisitor != null) ? fieldVisitor.unwrap() : null);
    }

    public FieldVisitor getInternal(final org.eclipse.persistence.asm.FieldVisitor fieldVisitor) {
        this.customFieldVisitor = fieldVisitor;
        return this.elFieldVisitor;
    }

    @Override
    public void visitEnd() {
        elFieldVisitor.visitEnd();
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        return new AnnotationVisitorImpl(elFieldVisitor.visitAnnotation(descriptor, visible));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T unwrap() {
        return (T)this.elFieldVisitor;
    }
}
