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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import org.eclipse.persistence.testing.jaxb.xmlvariablenode.method.XmlVariableNodeMethodGetOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvariablenode.method.XmlVariableNodeMethodTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllVariableElementTestCases extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("XML Variable Node Test Suite");
        suite.addTestSuite(XmlVariableNodeChildTestCases.class);
        suite.addTestSuite(XmlVariableNodeChildTypeTestCases.class);
        suite.addTestSuite(XmlVariableNodeDuplicatesTestCases.class);
        suite.addTestSuite(XmlVariableNodeInvalidTestCases.class);
        suite.addTestSuite(XmlVariableNodeObjectsTestCases.class);
        suite.addTestSuite(XmlVariableNodeTestCases.class);
        suite.addTestSuite(XmlVariableNodeQNameTestCases.class);
        suite.addTestSuite(XmlVariableNodeQNameNSTestCases.class);
        suite.addTestSuite(XmlVariableNodeTestCases.class);
        suite.addTestSuite(XmlVariableNodeArrayTestCases.class);
        suite.addTestSuite(XmlVariableNodeMapTestCases.class);
        suite.addTestSuite(XmlVariableNodeBindingsTestCases.class);
        suite.addTestSuite(XmlVariableNodeNullValueTestCases.class);
        suite.addTestSuite(XmlVariableNodeAdapterTestCases.class);
        suite.addTestSuite(XmlVariableNodeWithReferenceTestCase.class);

        suite.addTestSuite(XmlVariableNodeXmlValueTestCases.class);
        suite.addTestSuite(XmlVariableNodeXmlValueAttributeTestCases.class);
        suite.addTestSuite(XmlVariableNodeXmlValueCollectionTestCases.class);

        suite.addTestSuite(XmlVariableNodeMethodTestCases.class);
        suite.addTestSuite(XmlVariableNodeMethodGetOnlyTestCases.class);

        suite.addTestSuite(XmlVariableNodeSingleTestCases.class);
        suite.addTestSuite(XmlVariableNodeObjectXPathTestCases.class);
        suite.addTestSuite(XmlVariableNodeCollectionNullValueTestCases.class);
        suite.addTestSuite(XmlVariableNodeCollectionXPathTestCases.class);
        suite.addTestSuite(XmlVariableNodeElementWrapperTestCases.class);
        return suite;
    }
}
