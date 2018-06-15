/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOXMLHelperLoadAndSavePurchaseOrderTestSuite {
    public SDOXMLHelperLoadAndSavePurchaseOrderTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper Tests");

        suite.addTest(new TestSuite(LoadAndSavePurchaseOrderWChangeSummaryTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveImportsDefaultNamespaceTestCases.class));

        // one expected failure
        suite.addTest(new TestSuite(LoadAndSaveWithImportsTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveImportsElementOrderTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveIDRefTestCases.class));
        suite.addTest(new TestSuite(LoadAndSavePurchaseOrderComplexTestCases.class));
        suite.addTest(new TestSuite(LoadAndSavePurchaseOrderComplexDefaultNSTestCases.class));
        suite.addTest(new TestSuite(LoadAndSavePurchaseOrderTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveSimpleAttributeTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveSimpleElementTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveSchemaTypesTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveOpenContentTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveSchemaTypesEmptyStringTestCases.class));


        // test DirectMapping
        suite.addTest(new TestSuite(LoadAndSaveNillableOptionalNodeNullPolicyTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveNillableIsSetNodeNullPolicyTrueTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveNillableIsSetNodeNullPolicyFalseTestCases.class));

        return suite;
    }
}
