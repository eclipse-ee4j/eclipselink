/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
        return suite;
    }
}
