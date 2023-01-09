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
package org.eclipse.persistence.asm;

public abstract class AnnotationVisitor {

    protected AnnotationVisitor customAnnotationVisitor;

    public void setCustomAnnotationVisitor(AnnotationVisitor annotationVisitor) {
        this.customAnnotationVisitor = annotationVisitor;
    }

    public abstract void visit(final String name, final Object value);

    public abstract void visitEnum(String name, String desc, String value);

    public abstract AnnotationVisitor visitAnnotation(String name, String desc);

    public abstract AnnotationVisitor visitArray(String name);

    public abstract void visitEnd();

    public abstract <T> T unwrap();
}
