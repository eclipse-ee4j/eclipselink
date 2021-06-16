/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
