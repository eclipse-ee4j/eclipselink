/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - May 2012
 ******************************************************************************/
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
