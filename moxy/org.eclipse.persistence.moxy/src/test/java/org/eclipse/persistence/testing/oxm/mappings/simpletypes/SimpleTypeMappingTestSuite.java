/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.simpletypes;


// JUnit imports
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement.RootElementWithCommentTestCases;

public class SimpleTypeMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Simple Type Test Suite");
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement.RootElementTestCases.class);
                suite.addTestSuite(RootElementWithCommentTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement.RootElementNullTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.simpletypes.childelement.ChildElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.simpletypes.childcollection.ChildCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.simpletypes.documentpreservation.DocumentPreservationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.simpletypes.typetranslator.childelement.TypeTranslatorTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.simpletypes.typetranslator.rootelement.TypeTranslatorTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.simpletypes.inheritance.InheritanceRootTestCases.class);
        //remove this test for now as we don't currently support mapping to positional text nodes
        //suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement.positional.PositionalRootElementTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.simpletypes.SimpleTypeMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
