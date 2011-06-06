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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.load.SDOXMLHelperLoadTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.save.SDOXMLHelperSaveTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.threadsafety.SDOXMLHelperThreadSafetyTestSuite;

public class SDOXMLHelperTestSuite {
    public SDOXMLHelperTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper Tests");
        suite.addTest(new SDOXMLHelperLoadTestSuite().suite());
        suite.addTest(new SDOXMLHelperSaveTestSuite().suite());
        suite.addTest(new SDOXMLHelperThreadSafetyTestSuite().suite());
        suite.addTestSuite(SDOXMLHelperExceptionTestCases.class);
        return suite;
    }
}
