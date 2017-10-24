/*******************************************************************************
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/24/2017-3.0 Tomas Kraus
 *       - 526419: Modify EclipseLink to reflect changes in JTA 1.1.
 ******************************************************************************/
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
