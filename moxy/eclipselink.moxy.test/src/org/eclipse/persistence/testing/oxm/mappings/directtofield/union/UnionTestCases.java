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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.union;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.union.xmlattribute.SimpleUnionXMLAttributeTestCases;

public class UnionTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Union Test Cases");

        suite.addTestSuite(SimpleUnionTestCases.class);
        suite.addTestSuite(SimpleUnionNoConversionTestCases.class);
        suite.addTestSuite(UnionDateTimeToDateTestCases.class);
        suite.addTestSuite(UnionWithTypeAttributeTestCases.class);
        suite.addTestSuite(UnionWithTypeAttributeNotSetTestCases.class);
        suite.addTestSuite(SimpleUnionXMLAttributeTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.union.UnionTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
