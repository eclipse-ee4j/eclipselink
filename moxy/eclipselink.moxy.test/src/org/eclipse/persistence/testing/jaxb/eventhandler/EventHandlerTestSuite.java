/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - May 2012
package org.eclipse.persistence.testing.jaxb.eventhandler;

import junit.framework.Test;
import junit.framework.TestSuite;

public class EventHandlerTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Eventhandler Test Suite");
        suite.addTestSuite(AttributeEnumInvalidTypeTestCases.class);
        suite.addTestSuite(AttributeEnumInvalidValueTestCases.class);
        suite.addTestSuite(ElementEnumInvalidTypeTestCases.class);
        suite.addTestSuite(ElementEnumInvalidValueTestCases.class);
        suite.addTestSuite(ElementInvalidTypeTestCases.class);
        suite.addTestSuite(AttributeInvalidTypeTestCases.class);
        return suite;
    }

}
