/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.libraries.asm;

import java.io.InputStream;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class EclipseLinkClassReaderTest {

    private static final String CLASS_FILE_NAME = "org/eclipse/persistence/internal/libraries/asm/AnnotationWriter.class";

    /**
     * Basic functional test for ASM and {@link EclipseLinkClassReader}.
     * @throws Exception
     */
    @Test
    public void verifyASMTest() throws Exception {
        try (InputStream stream = EclipseLinkClassReader.class.getClassLoader().getResourceAsStream(CLASS_FILE_NAME)) {
            ClassReader reader = new EclipseLinkClassReader(stream);
            assertNotNull(reader);
            assertEquals("org/eclipse/persistence/internal/libraries/asm/AnnotationVisitor", reader.getSuperName());
            assertEquals("org/eclipse/persistence/internal/libraries/asm/AnnotationWriter", reader.getClassName());
        }
    }
}
