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
package org.eclipse.persistence.asm.internal.platform.ow2;

import org.eclipse.persistence.asm.ASMFactory;
import org.eclipse.persistence.asm.Opcodes;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.SerialVersionUIDAdder;

public class SerialVersionUIDAdderImpl extends org.eclipse.persistence.asm.SerialVersionUIDAdder {

    private class OW2SerialVersionUIDAdder extends SerialVersionUIDAdder {

        public OW2SerialVersionUIDAdder(final ClassVisitor classVisitor) {
            super(Opcodes.ASM_API_SELECTED, classVisitor);
        }
    }

    private OW2SerialVersionUIDAdder ow2SerialVersionUIDAdder;

    public SerialVersionUIDAdderImpl(final org.eclipse.persistence.asm.ClassVisitor classVisitor) {
        this.ow2SerialVersionUIDAdder = new OW2SerialVersionUIDAdder((classVisitor != null) ? classVisitor.unwrap() : null);
    }

        @Override
    public <T> T unwrap() {
        return (T)this.ow2SerialVersionUIDAdder;
    }

}
