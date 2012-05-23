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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.namespaces.identifiedbyname.IdentifiedByNameNamespaceTestSuite;

import org.eclipse.persistence.oxm.XMLMarshaller;

public class NamespaceTestSuite extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("Single Attribute Test Cases");
		//this is currently not supported, the test case be readded when bug #4243476 is fixed
    //suite.addTestSuite(DefaultNamespaceTestCases.class);
		suite.addTestSuite(GloballyDefinedNodesTestCases.class);
    suite.addTestSuite(LocallyDefinedNodesTestCases.class);
    suite.addTestSuite(NoNamespacesTestCases.class);
    suite.addTestSuite(ExtraNamespacesBug6004272TestCases.class);
    suite.addTestSuite(ChildAndGeneratedPrefixClashTestCases.class);
    return suite;
  }
		
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.namespaces.NamespaceTestSuite" };
        // System.setProperty("useDeploymentXML", "true");
        // System.setProperty("useDocPres", "true");
        // System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }
}
