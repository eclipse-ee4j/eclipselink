/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.dataobject.containment;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ContainmentTestSuite extends TestCase {
    public ContainmentTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     *  Inherited suite method for generating all test cases.
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("All DataObject Containment Tests");

        suite.addTest(new TestSuite(CreateDataObjectTestCases.class));
        suite.addTest(new TestSuite(DeleteTestCases.class));
        suite.addTest(new TestSuite(DetachTestCases.class));
        suite.addTest(new TestSuite(SetTestCases.class));
        suite.addTest(new TestSuite(ContainmentManyTestCases.class));
        return suite;
    }
}