/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

import java.io.StringWriter;

public class PrintIdentityMapTest extends AutoVerifyTestCase {
    protected Class<?> classToPrint;
    protected StringWriter outWriter;

    /**
     * PrintIdentityMapTestModel constructor comment.
     */
    public PrintIdentityMapTest() {
        super();
        outWriter = new StringWriter();
    }

    /**
     * PrintIdentityMapTestModel constructor comment.
     */
    public PrintIdentityMapTest(Class<?> classToPrint) {
        this();
        this.classToPrint = classToPrint;
    }

    /**
     * Do nothing
     */
    @Override
    protected void setup() {
    }

    /**
     * The actual test code goes in here.
     */
    @Override
    protected void test() {
        if (classToPrint == null) {
            //printAllIdentityMaps
            //NOTE this does not use the helper method as it needs a handle on the log.
            getSession().getIdentityMapAccessor().printIdentityMaps();
        } else {
            getSession().getIdentityMapAccessor().printIdentityMap(classToPrint);
        }
    }

    /**
     * Parse the stream and veerify that everything is okay...
     * Eventually.  Right now do nothing.
     */
    @Override
    protected void verify() {
    }
}
