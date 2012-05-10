/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.PurchaseOrderGenerateTestCases;

public class XSDHelperGenerateTestSuite {
    public XSDHelperGenerateTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
    *  Inherited suite method for generating all test cases.
    * @return
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("All XSDHelper Generate Tests");

        suite.addTest(new TestSuite(PurchaseOrderComplexGenerateTestCases.class));
        suite.addTest(new TestSuite(PurchaseOrderGenerateTestCases.class));
        suite.addTest(new TestSuite(XSDHelperGenerateExceptionTestCases.class));
        suite.addTest(new TestSuite(ImportsTestCases.class));
        suite.addTest(new TestSuite(ImportBug6311853TestCases.class));
        suite.addTest(new TestSuite(CyclicImportsTestCases.class));
        suite.addTest(new TestSuite(PurchaseOrderGenerateWithAnnotationsTestCases.class));
        suite.addTest(new TestSuite(PurchaseOrderGenerateWithAnnotationsTNSTestCases.class));

        suite.addTest(new TestSuite(SchemaLocationResolverTestCases.class));
        suite.addTest(new TestSuite(SchemaLocationResolverPart2TestCases.class));
        suite.addTest(new TestSuite(SchemaLocationResolverPart3TestCases.class));
        suite.addTest(new TestSuite(SchemaLocationResolverPart4TestCases.class));        
        //suite.addTest(new TestSuite(DontModifyListJIRA254TestCases.class));        
        return suite;
    }
}
