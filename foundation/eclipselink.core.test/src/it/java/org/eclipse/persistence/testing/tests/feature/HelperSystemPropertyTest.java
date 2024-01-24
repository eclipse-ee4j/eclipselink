/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;

/**
 * Parital test for Bug 2756643.
 * Tests to ensure the Helper statics which access System Properties
 * are correctly set.
 */
public class HelperSystemPropertyTest extends AutoVerifyTestCase {
    public HelperSystemPropertyTest() {
        setDescription("Tests to ensure the Helper statics which access System Properties are correctly set.");
    }

    @Override
    public void verify() {
        if (!System.lineSeparator().equals(System.getProperty("line.separator"))) {
            throw new TestErrorException("System.lineSeparator() returns the incorrect value.");
        }

        if (!org.eclipse.persistence.internal.helper.Helper.currentWorkingDirectory().equals(System.getProperty("user.dir"))) {
            throw new TestErrorException("Helper.currentWorkingDirectory() returns the incorrect value.");
        }

        Object tlTempDirectory = org.eclipse.persistence.internal.helper.Helper.tempDirectory();
        Object sysTempDirectory = System.getProperty("java.io.tmpdir");
        if (((tlTempDirectory == null) && (sysTempDirectory != null)) || !tlTempDirectory.equals(sysTempDirectory)) {
            throw new TestErrorException("Helper.tempDirectory() returns the incorrect value.");
        }
    }
}
