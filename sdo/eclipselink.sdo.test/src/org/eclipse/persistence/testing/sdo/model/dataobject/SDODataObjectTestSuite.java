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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearMonthDayTest;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearMonthDayWithCalnTest;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearMonthTest;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearMonthWithCalnTest;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearTest;
import org.eclipse.persistence.testing.sdo.helper.datahelper.DataHelperToYearWithCalnTest;
import org.eclipse.persistence.testing.sdo.model.dataobject.xpathpositional.SDODataObjectXPathPositionalTestSuite;
import org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery.SDODataObjectXPathQueryTestSuite;

public class SDODataObjectTestSuite {
    public SDODataObjectTestSuite() {
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
    *  Inherited suite mthod for generating all test cases.
    * @return
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("All SDODataObject Tests");

        suite.addTest(new TestSuite(SDODataObjectCloneTestCases.class));

        suite.addTest(new TestSuite(SDODataObjectSetGetWithPropertyTest.class));
        suite.addTest(new TestSuite(SDODataObjectSetGetWithIndexTest.class));

        suite.addTest(new TestSuite(DataHelperToYearMonthDayTest.class));
        suite.addTest(new TestSuite(DataHelperToYearMonthDayWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToYearMonthTest.class));
        suite.addTest(new TestSuite(DataHelperToYearMonthWithCalnTest.class));
        suite.addTest(new TestSuite(DataHelperToYearTest.class));
        suite.addTest(new TestSuite(DataHelperToYearWithCalnTest.class));

        suite.addTest(new TestSuite(SDODataObjectUnsetIsSetTest.class));
        suite.addTest(new TestSuite(SDODataObjectContainerContainmentPropertyTest.class));

        // these suites are discovered and not run twice
        //suite.addTest(new SDODataObjectXPathQueryTestSuite().suite());
        //suite.addTest(new SDODataObjectXPathPositionalTestSuite().suite());

        suite.addTest(new TestSuite(SDODataObjectListWrapperTest.class));
        
        suite.addTest(new TestSuite(SDODataObjectJIRA81TestCases.class));
        suite.addTest(new TestSuite(SDODataObjectJIRA90ConversionTestCases.class));

        suite.addTest(new TestSuite(SDODataObjectJIRA102NillableDirectTestCases.class));
        suite.addTest(new TestSuite(SDODataObjectJIRA102NillableCompositeObjectTestCases.class));
        suite.addTest(new TestSuite(SDODataObjectOpenContentBug6011530TestCases.class));

        suite.addTest(new TestSuite(SetAndGetWithManyPropertyTestCases.class));
        suite.addTest(new TestSuite(SetAndGetWithManyPropertyViaPathTestCases.class));

        suite.addTest(new TestSuite(OpenSequencedTypeTestCases.class));
        suite.addTest(new TestSuite(NewerContextTestCases.class));
        return suite;
    }
}
