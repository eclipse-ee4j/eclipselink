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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOXMLHelperLoadAndSaveTestSuite {
    public SDOXMLHelperLoadAndSaveTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper Tests");


        suite.addTest(new TestSuite(LoadAndSaveValuePropTestCases.class));        
        suite.addTest(new TestSuite(LoadAndSaveWithDefaultsTestCases.class));        
        suite.addTest(new TestSuite(LoadAndSaveInheritanceBug6043501TestCases.class));        
        suite.addTest(new TestSuite(LoadAndSaveNamespacesBugTestCases.class));  
        suite.addTest(new TestSuite(LoadAndSaveBug6130541TestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveAttributeGroupTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveGroupTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveWithTypeBug6522867TestCases.class));

        //read-only
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveWithReadOnlyTestCases.class));

        // nillable
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nillable.ListPropertyNillableElementTestCases.class));

        // mixed text
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.mixed.LoadAndSaveMixedContentTestCases.class));

        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.staticclasses.LoadAndSaveStaticClassesTestCases.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveIncludeWithExtensionTestCases.class));
        suite.addTestSuite(GlobalAttributeTestCases.class);
        suite.addTest(new TestSuite(LoadAndSaveExceptionBug325353TestCases.class));
        suite.addTestSuite(ListEmptyElementTestCases.class);
        suite.addTestSuite(ListEmptyElementNullableTestCases.class);
        return suite;
    }
}
