/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 27 November 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ByXPathTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Get/Set/CreateByXPath Test Suite");

        suite.addTestSuite(GetByXPathTests.class);
        suite.addTestSuite(SetByXPathTests.class);
        suite.addTestSuite(CreateByXPathTests.class);

        return suite;
    }

}
