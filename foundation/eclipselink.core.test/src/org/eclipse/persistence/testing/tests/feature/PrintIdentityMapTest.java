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
