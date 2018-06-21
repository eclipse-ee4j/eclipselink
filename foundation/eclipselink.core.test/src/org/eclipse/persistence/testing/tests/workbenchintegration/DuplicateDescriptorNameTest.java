/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 * Test to ensure that ProjectClassGenerator works correctly for projects with descriptors
 * with classes that have identical short names.
 * Added for code coverage
 */
public class DuplicateDescriptorNameTest extends ProjectClassGeneratorResultFileTest {
    String testString2 = null;

    public DuplicateDescriptorNameTest() {
        super(new EmployeeSubProject(), "buildorgeclipsepersistencetestingmodelsemployeedomainEmployee");
        testString2 = "buildorgeclipsepersistencetestingtestsworkbenchintegrationEmployee";
    }

    public void verify() {
        if (generationException != null) {
            throw new TestErrorException("Exception thrown while generating Java source. " +
                                         generationException.toString());
        }
        if (!findStringInFile(testString, fileName) && !findStringInFile(testString2, fileName)) {
            String exceptionString = "Duplicately named descriptors incorrectly generated in Project java file.";
            if (fileReadException != null) {
                exceptionString =
                        exceptionString + " Exception thrown while reading file. - " + fileReadException.toString();
            }
            throw new TestErrorException(exceptionString);
        }
    }
}
