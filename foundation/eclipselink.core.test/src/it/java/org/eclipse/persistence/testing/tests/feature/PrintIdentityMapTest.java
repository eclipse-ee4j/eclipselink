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

import java.io.*;
import org.eclipse.persistence.testing.framework.*;

public class PrintIdentityMapTest extends AutoVerifyTestCase {
    protected Class classToPrint;
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
    public PrintIdentityMapTest(Class classToPrint) {
        this();
        this.classToPrint = classToPrint;
    }

    /**
     * Do nothing
     */
    protected void setup() {
    }

    /**
     * The actual test code goes in here.
     */
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
    protected void verify() {
    }
}
