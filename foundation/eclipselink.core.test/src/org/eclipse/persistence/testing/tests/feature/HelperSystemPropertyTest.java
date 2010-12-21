/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
