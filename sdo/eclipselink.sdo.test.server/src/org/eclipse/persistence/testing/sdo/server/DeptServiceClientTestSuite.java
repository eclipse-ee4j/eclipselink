/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     etang - April 12/2010 - 2.1 - Initial implementation
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
