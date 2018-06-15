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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.choice.ChoiceTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.group.GroupTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.group.nestedGroup.GroupWithNestedGroupTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.group.nestedSequence.GroupWithNestedSequenceTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.sequence.SequenceTestCases;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.sequence.nestedGroup.SequenceWithNestedGroupTestCases;

public class StructureValidationTestSuite extends TestCase {
    public StructureValidationTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Structure Validation Test Suite");
        String platform = System.getProperty("eclipselink.xml.platform");
        if (null == platform) {
            platform = "org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform";
        }
        boolean jaxpPlatform = platform.equalsIgnoreCase("org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform");
        if (!jaxpPlatform) {
            suite.addTestSuite(ChoiceTestCases.class);
            suite.addTestSuite(GroupTestCases.class);
            suite.addTestSuite(GroupWithNestedGroupTestCases.class);
            suite.addTestSuite(GroupWithNestedSequenceTestCases.class);
            suite.addTestSuite(SequenceTestCases.class);
            suite.addTestSuite(SequenceWithNestedGroupTestCases.class);
        }

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.StructureValidationTestSuite" };
        TestRunner.main(arguments);
    }
}
