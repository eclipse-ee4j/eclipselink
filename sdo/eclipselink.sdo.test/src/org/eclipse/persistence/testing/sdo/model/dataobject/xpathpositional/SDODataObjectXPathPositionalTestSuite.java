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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathpositional;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDateByPositionalPathTest;
import org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetIntByPositionalPathTest;
import org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetListByPositionalPathTest;
import org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetShortByPositionalPathTest;
import org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery.SDODataObjectGetByXPathQueryTest;

public class SDODataObjectXPathPositionalTestSuite {
    public SDODataObjectXPathPositionalTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     *  Inherited suite mthod for generating all test cases.
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("All Helper Tests");
        suite.addTest(new TestSuite(SDODataObjectGetByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBooleanByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetByteByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBytesByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetCharacterByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDataObjectByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDateByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDecimalByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDoubleByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetFloatByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntegerByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetListByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetLongByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetShortByPositionalPathTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetStringByPositionalPathTest.class));
        return suite;
    }
}
