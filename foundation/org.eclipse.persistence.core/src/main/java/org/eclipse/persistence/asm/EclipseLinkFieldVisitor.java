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

public class EclipseLinkFieldVisitor extends FieldVisitor {

    private FieldVisitor fieldVisitor;

    public EclipseLinkFieldVisitor() {
        this.fieldVisitor = ASMFactory.createFieldVisitor(Opcodes.ASM_API_SELECTED);
    }

    public EclipseLinkFieldVisitor(FieldVisitor fieldVisitor) {
        this.fieldVisitor = ASMFactory.createFieldVisitor(Opcodes.ASM_API_SELECTED, fieldVisitor);
    }

    @Override
    public void visitEnd() {
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return null;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T unwrap() {
        if (this.fieldVisitor instanceof org.eclipse.persistence.asm.internal.platform.ow2.FieldVisitorImpl) {
            return (T)((org.eclipse.persistence.asm.internal.platform.ow2.FieldVisitorImpl)this.fieldVisitor).getInternal(this.customFieldVisitor);
        } else {
            return null;
        }
    }
}
