/*******************************************************************************
* Copyright (c) 1998, 2008 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms
* of the Eclipse Public License v1.0 and Eclipse Distribution License v1.0
* which accompanies this distribution.
* 
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation
******************************************************************************/

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOXMLHelperDatatypeTestSuite {

    public SDOXMLHelperDatatypeTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLHelper Simple (Primitive) Document Tests");

        suite.addTest(new TestSuite(SDOXMLHelperDatatypeBase64TestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeHexTestCases.class));        
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeFloatTestCases.class));
        suite.addTest(new TestSuite(SDOXMLHelperDatatypeUnionTestCases.class));

        return suite;
    }

}