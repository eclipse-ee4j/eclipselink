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
package org.eclipse.persistence.testing.oxm.mappings.choice;

import org.eclipse.persistence.testing.oxm.mappings.choice.converter.ConverterTestCases;
import org.eclipse.persistence.testing.oxm.mappings.choice.ref.XMLChoiceWithReferenceTestCases;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XMLChoiceMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("XMLChoiceMapping Test Suite");
        suite.addTestSuite(XMLChoiceMappingComplexValueTestCases.class);
        suite.addTestSuite(XMLChoiceMappingSimpleValueTestCases.class);
        suite.addTestSuite(XMLChoiceMappingNonStringValueTestCases.class);
        suite.addTestSuite(ConverterTestCases.class);
        suite.addTestSuite(XMLChoiceWithReferenceTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.binarydata.XMLChoiceMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
