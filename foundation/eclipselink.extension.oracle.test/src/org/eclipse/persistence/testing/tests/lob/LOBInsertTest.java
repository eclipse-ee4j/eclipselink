/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.lob;

import org.eclipse.persistence.testing.tests.writing.ComplexUpdateTest;

/**
 * <p>
 * <b>Purpose</b>: Define a test for inserting CLOB/BLOB into the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Execute the insert object query and verify no errors occurred.
 * <li> Verify the object written matches the object that was written.
 * </ul>
 *
 * @author King Wang (Aug. 2002)
 * @since TopLink/Java 5.0
 */
public class LOBInsertTest extends ComplexUpdateTest {
    public LOBInsertTest(Object originalObject) {
        this(originalObject, false);
    }

    public LOBInsertTest(Object originalObject, boolean usesUnitOfWork) {
        super(originalObject);
        this.usesUnitOfWork = usesUnitOfWork;
        setDescription("The test writing of the intended object with LOB values, from the database and checks if it was inserted properly");
        if (usesUnitOfWork) {
            setDescription(getDescription() + " usesUOW");
        }
    }

    public String getName() {
        String strOriginal = "";
        if (((Image)originalObject).getPicture() != null && ((Image)originalObject).getScript() != null) {
            strOriginal = ((Image)originalObject).getPicture().length + ", " + ((Image)originalObject).getScript().length();
        }
        String str = "LOBInsertTest(" + strOriginal + ")";
        if (usesUnitOfWork) {
            str = str + " usesUOW";
        }
        return str;
    }
}
