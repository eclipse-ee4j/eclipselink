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
//     bdoughan - Oct 29/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.keybased.compositekeyclass;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CompositeKeyClassMappingTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Key Class Test Cases");
        suite.addTestSuite(AttributeTestCases.class);
        suite.addTestSuite(ElementTestCases.class);
        suite.addTestSuite(SelfAttributeTestCases.class);
        suite.addTestSuite(SelfElementTestCases.class);
        suite.addTestSuite(SingleNodeTestCases.class);
        return suite;
    }

}
