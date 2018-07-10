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
package org.eclipse.persistence.testing.sdo.helper.classgen;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOClassGenTestSuite {
    public SDOClassGenTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All SDOClassGenTestSuite Tests");
        //suite.addTest(new TestSuite(ClassGenWithJavaReservedWordCollisionTestCases.class));
        //suite.addTest(new TestSuite(ClassGenWithSDOReservedWordCollisionTestCases.class));
        suite.addTest(new TestSuite(ClassGenWithInvalidClassNameFormatTestCases.class));
        suite.addTest(new TestSuite(ListTestCases.class));
        suite.addTest(new TestSuite(UnionTestCases.class));
        suite.addTest(new TestSuite(BaseTypeTestCases.class));
        suite.addTest(new TestSuite(PurchaseOrderTestCases.class));
        suite.addTest(new TestSuite(ClassGenWithImportsTestCases.class));
        suite.addTest(new TestSuite(ClassGenWithImportsDontProcessTestCases.class));
        suite.addTest(new TestSuite(ClassGenComplexTypesTestCases.class));
        //suite.addTest(new TestSuite(ClassGenUnicodeCharacterTestCases.class));
        suite.addTest(new TestSuite(ClassGenElementsTestCases.class));
        suite.addTest(new TestSuite(SchemaTypesTestCases.class));
        suite.addTest(new TestSuite(NestedBaseTypesTestCases.class));
        suite.addTest(new TestSuite(PurchaseOrderWithAnnotationsClassGenTestCases.class));
        suite.addTest(new TestSuite(ClassGenWithJavadocsTestCases.class));
        suite.addTest(new TestSuite(ClassGenWithJavaDocsAndListenerTestCases.class));
        return suite;
    }
}
