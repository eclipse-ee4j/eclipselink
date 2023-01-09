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

public abstract class FieldVisitor {

    protected FieldVisitor customFieldVisitor;

    public void setCustomFieldVisitor(FieldVisitor fieldVisitor) {
        this.customFieldVisitor = fieldVisitor;
    }

    public abstract void visitEnd();

    public abstract AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible);

    public abstract <T> T unwrap();
}
