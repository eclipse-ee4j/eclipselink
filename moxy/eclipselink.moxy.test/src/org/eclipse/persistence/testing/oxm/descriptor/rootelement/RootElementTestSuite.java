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
package org.eclipse.persistence.testing.oxm.descriptor.rootelement;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbyname.RootElementIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbynamespace.RootElementIdentifiedByNamespaceTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsAlwaysWrapTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases2;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases3;

public class RootElementTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Root Element Test Suite");
        suite.addTestSuite(RootElementIdentifiedByNameTestCases.class);
        suite.addTestSuite(RootElementIdentifiedByNamespaceTestCases.class);
        suite.addTestSuite(MultipleRootsTestCases.class);
        suite.addTestSuite(MultipleRootsTestCases2.class);
        suite.addTestSuite(MultipleRootsTestCases3.class);
        suite.addTestSuite(MultipleRootsAlwaysWrapTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.descriptor.rootelement.RootElementTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }
}
