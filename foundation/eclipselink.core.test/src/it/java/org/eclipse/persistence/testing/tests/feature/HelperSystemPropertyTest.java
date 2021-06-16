/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

    public void verify() {
        if (!org.eclipse.persistence.internal.helper.Helper.cr().equals(System.getProperty("line.separator"))) {
            throw new TestErrorException("Helper.cr() returns the incorrect value.");
        }

        if (!org.eclipse.persistence.internal.helper.Helper.pathSeparator().equals(System.getProperty("path.separator"))) {
            throw new TestErrorException("Helper.pathSeparator() returns the incorrect value.");
        }

        if (!org.eclipse.persistence.internal.helper.Helper.fileSeparator().equals(System.getProperty("file.separator"))) {
            throw new TestErrorException("Helper.fileSeparator() returns the incorrect value.");
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
