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
 *     etang - April 12/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.server;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class DeptServiceClientTestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("Server tests - DeptService");
        suite.addTest(new TestSuite(DeptServiceClientTestCases.class));
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.server.DeptServiceClientTestSuite" };
        TestRunner.main(arguments);
    }
}
