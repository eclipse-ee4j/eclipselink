/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ComplexDefineTestSuite {
    public ComplexDefineTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
    *  Inherited suite method for generating all test cases.
    * @return
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("All complex define Tests");
        suite.addTest(new TestSuite(PurchaseOrderComplexDefineTestCases.class));
        suite.addTest(new TestSuite(PurchaseOrderDefineTestCases.class));
        suite.addTest(new TestSuite(PurchaseOrderWithAnnotationsDefineTestCases.class));
        suite.addTest(new TestSuite(DefineWithImportsTestCases.class));
        suite.addTest(new TestSuite(DefineWithIncludesTestCases.class));
        suite.addTest(new TestSuite(DefineWithImportsExceptionTestCases.class));
        suite.addTest(new TestSuite(CyclicImportsDefineTestCases.class));
        suite.addTest(new TestSuite(DefineWithBuiltInSchemaLocationTestCases.class));
        suite.addTest(new TestSuite(ClashingNamespacesTestCases.class));
        suite.addTestSuite(MultipleDefineSameTypeTestCases.class);
        suite.addTestSuite(AttributeGroupTestCases.class);
        suite.addTestSuite(CyclicElementRefTestCases.class);
        suite.addTestSuite(CyclicElementRefErrorTestCases.class);
        suite.addTestSuite(DefineWithNestedNamespacesTestCases.class);
        suite.addTestSuite(DefineWithImportsNoSchemaLocationTestCases.class);
        suite.addTestSuite(ComplexImportsAndIncludesTestCases.class);
        suite.addTestSuite(DefineFailsDontRegisterTypesTestCases.class);
        suite.addTestSuite(SchemaResolverSystemIdTestCases.class);
        // EL bug 451041
        suite.addTestSuite(TypePropertyIndexTest.class);
        return suite;
    }
}
