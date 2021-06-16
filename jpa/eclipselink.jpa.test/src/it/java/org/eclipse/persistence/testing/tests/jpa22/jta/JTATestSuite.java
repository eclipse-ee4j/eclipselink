/*
 * Copyright (c) 2017, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     10/24/2017-3.0 Tomas Kraus
//       - 526419: Modify EclipseLink to reflect changes in JTA 1.1.
package org.eclipse.persistence.testing.tests.jpa22.jta;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JTA tests suite.
 */
public class JTATestSuite {

    /**
     * Creates jUnit test suite for JTA tests.
     *
     * @return jUnit JTA tests suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JTA_TestSuite");
        suite.addTest(JTA11TransactionControllerTest.suite());
        return suite;
    }

}
