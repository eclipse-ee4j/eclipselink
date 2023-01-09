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

public abstract class EclipseLinkClassVisitor extends ClassVisitor {

    public EclipseLinkClassVisitor() {
        super(ASMFactory.ASM_API_SELECTED);
    }

    public EclipseLinkClassVisitor(ClassVisitor classVisitor) {
        super(ASMFactory.ASM_API_SELECTED, classVisitor);
    }

    public void visit(
            final int access,
            final String name,
            final String signature,
            final String superName,
            final String[] interfaces) {
        visit(ASMFactory.ASM_API_SELECTED, access, name, signature, superName, interfaces);
    }

    public abstract AnnotationVisitor visitAnnotation(String desc, boolean visible);
}
