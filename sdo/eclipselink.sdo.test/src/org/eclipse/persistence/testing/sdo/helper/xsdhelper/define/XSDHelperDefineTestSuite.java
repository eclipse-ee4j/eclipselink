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
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.attributes.SDOAttributeXSDTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.complextypes.SDOComplexTypeXSDTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.elements.SDOElementXSDTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.simpletypes.SDOSimpleTypeXSDTestSuite;


public class XSDHelperDefineTestSuite {
    public XSDHelperDefineTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XSDHelper Define Tests");

        suite.addTest(new SDOSimpleTypeXSDTestSuite().suite());
        suite.addTest(new SDOComplexTypeXSDTestSuite().suite());
        suite.addTest(new SDOAttributeXSDTestSuite().suite());
        suite.addTest(new SDOElementXSDTestSuite().suite());
        suite.addTest(new ComplexDefineTestSuite().suite());
        suite.addTestSuite(ImportTypeWithSameNameAsElementTestSuite.class);
        suite.addTestSuite(ExtendTypeWithSimpleContentTest.class);
        suite.addTestSuite(RedefineTestCases.class);
        return suite;
    }
}
