/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDODataObjectXPathEngineTestSuite {
    public SDODataObjectXPathEngineTestSuite() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All DataObject XPathEngine Tests");
        suite.addTest(new TestSuite(XPathEngineSimpleTestCases.class));
        suite.addTest(new TestSuite(XPathHelperTestCases.class));
        suite.addTest(new TestSuite(XPathHelperRelationalOPTestCases.class));
        suite.addTest(new TestSuite(XPathHelperLogicalOPTestCases.class));
        suite.addTest(new TestSuite(XPathExpressionTestCases.class));
        suite.addTest(new TestSuite(XPathEngineBug242108TestCases.class));
        suite.addTestSuite(XPathCharacterTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
