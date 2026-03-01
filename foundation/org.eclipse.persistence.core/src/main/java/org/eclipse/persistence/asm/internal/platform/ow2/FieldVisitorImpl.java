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
package org.eclipse.persistence.asm.internal.platform.ow2;

import org.eclipse.persistence.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;

public class FieldVisitorImpl extends org.eclipse.persistence.asm.FieldVisitor {

    private class OW2FieldVisitor extends FieldVisitor {

        public OW2FieldVisitor(final int api) {
            super(api);
        }

        public OW2FieldVisitor(final int api, final FieldVisitor fieldVisitor) {
            super(api, fieldVisitor);
        }

        @Override
        public org.objectweb.asm.AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
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

    FieldVisitor ow2FieldVisitor;

    public FieldVisitorImpl(FieldVisitor fieldVisitor) {
        this.ow2FieldVisitor = fieldVisitor;
    }

    public FieldVisitorImpl(final int api) {
        this.ow2FieldVisitor = new OW2FieldVisitor(api);
    }

    public FieldVisitorImpl(final int api, final org.eclipse.persistence.asm.FieldVisitor fieldVisitor) {
        this.ow2FieldVisitor = new OW2FieldVisitor(api, (fieldVisitor != null) ? fieldVisitor.unwrap() : null);
    }

    public FieldVisitor getInternal(final org.eclipse.persistence.asm.FieldVisitor fieldVisitor) {
        this.customFieldVisitor = fieldVisitor;
        return this.ow2FieldVisitor;
    }

    @Override
    public void visitEnd() {
        this.ow2FieldVisitor.visitEnd();
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        return new AnnotationVisitorImpl(this.ow2FieldVisitor.visitAnnotation(descriptor, visible));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T unwrap() {
        return (T)this.ow2FieldVisitor;
    }
}
