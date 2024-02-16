/*
 * Copyright (c) 2023,2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.junit.asm;

import org.eclipse.persistence.asm.ASMFactory;
import org.eclipse.persistence.asm.ClassVisitor;
import org.eclipse.persistence.asm.Opcodes;
import org.eclipse.persistence.config.SystemProperties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ASMFactoryTest {

    @Test
    public void getAsmServiceTest() {
        String asmServiceSysProperty = System.getProperty(SystemProperties.ASM_SERVICE, "");
        String asmService = ASMFactory.getAsmService();
        switch (asmServiceSysProperty) {
            case ASMFactory.ASM_SERVICE_OW2:
                assertEquals(ASMFactory.ASM_SERVICE_OW2, asmService);
                break;
            case ASMFactory.ASM_SERVICE_ECLIPSELINK:
            default:
                assertEquals(ASMFactory.ASM_SERVICE_ECLIPSELINK, asmService);
        }
    }

    @Test
    public void createClassVisitorTest() {
        String asmServiceSysProperty = System.getProperty(SystemProperties.ASM_SERVICE, "");
        ClassVisitor classVisitor = ASMFactory.createClassVisitor(Opcodes.ASM_API_SELECTED);
        switch (asmServiceSysProperty) {
            case ASMFactory.ASM_SERVICE_OW2:
                assertEquals(org.eclipse.persistence.asm.internal.platform.ow2.ClassVisitorImpl.class, classVisitor.getClass());
                break;
            case ASMFactory.ASM_SERVICE_ECLIPSELINK:
            default:
                assertEquals(org.eclipse.persistence.asm.internal.platform.eclipselink.ClassVisitorImpl.class, classVisitor.getClass());
        }
    }
}
