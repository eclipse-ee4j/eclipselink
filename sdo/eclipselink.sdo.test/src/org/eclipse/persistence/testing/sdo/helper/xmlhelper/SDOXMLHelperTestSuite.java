/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.load.SDOXMLHelperLoadTestSuite;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.save.SDOXMLHelperSaveTestSuite;

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
        return suite;
    }
}