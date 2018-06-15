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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectSetGetWithPropertyTest;
import org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectTestSuite;
import org.eclipse.persistence.testing.sdo.model.type.SDOTypeTestSuite;

public class SDODataObjectXPathQueryTestSuite {
    public SDODataObjectXPathQueryTestSuite() {
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

        //suite.addTest(new TestSuite(SDODataObjectGetByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDataObjectConversionWithXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBooleanConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetByteConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetBytesConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetCharacterConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDateConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDecimalConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetDoubleConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetFloatConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetIntegerConversionWithXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetListConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetLongConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetShortConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectGetStringConversionByXPathQueryTest.class));
        suite.addTest(new TestSuite(SDODataObjectUnsetIsSetByXPathQueryTest.class));
        return suite;
    }
}
