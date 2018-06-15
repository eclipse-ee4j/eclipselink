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
//     bdoughan - August 18/2009 - 1.2 - Initial implementation
package org.eclipse.persistence.testing.sdo.instanceclass;

import junit.framework.Test;
import junit.framework.TestSuite;

public class InstanceClassTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("All Instance Class Tests");
        suite.addTest(new TestSuite(InstanceClassTestCases.class));
        return suite;
    }

}
