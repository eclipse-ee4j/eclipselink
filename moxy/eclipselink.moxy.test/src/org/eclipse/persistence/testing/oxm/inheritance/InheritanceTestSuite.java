/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.inheritance.classextractor.CarClassExtractorTestCases;
import org.eclipse.persistence.testing.oxm.inheritance.typetests.TypeTestSuite;

public class InheritanceTestSuite extends TestCase {
    public InheritanceTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.InheritanceTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Inheritance Test Suite");
        suite.addTestSuite(InheritanceTestCases.class);
        suite.addTestSuite(InheritanceMissingDescriptorTestCases.class);
        suite.addTestSuite(InheritanceCarTestCases.class);
        suite.addTestSuite(InheritanceCarDiffPrefixTestCases.class);
        suite.addTestSuite(InheritanceVehicleTestCases.class);
        suite.addTestSuite(InheritanceVehicleDiffPrefixTestCases.class);
        suite.addTestSuite(InheritanceDiffPrefixNonRootTestCases.class);
        suite.addTest(TypeTestSuite.suite());
        suite.addTestSuite(CarClassExtractorTestCases.class);
        return suite;
    }
}