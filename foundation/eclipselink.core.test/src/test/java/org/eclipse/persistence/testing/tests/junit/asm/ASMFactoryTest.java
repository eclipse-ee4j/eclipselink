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
package org.eclipse.persistence.testing.tests.junit.asm;

import org.eclipse.persistence.asm.ASMFactory;
import org.eclipse.persistence.asm.ClassVisitor;
import org.eclipse.persistence.config.SystemProperties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ASMFactoryTest {

    @Test
    public void getAsmServiceTest() {
        String asmServiceSysProperty = System.getProperty(SystemProperties.ASM_SERVICE, "");
        String asmService = ASMFactory.getAsmService();
        assertEquals(ASMFactory.ASM_SERVICE_OW2, asmService);
    }

    @Test
    public void createClassVisitorTest() {
        String asmServiceSysProperty = System.getProperty(SystemProperties.ASM_SERVICE, "");
        ClassVisitor classVisitor = ASMFactory.createClassVisitor(ASMFactory.ASM_API_SELECTED);
        assertEquals(org.eclipse.persistence.asm.internal.platform.ow2.ClassVisitorImpl.class, classVisitor.getClass());
    }
}
