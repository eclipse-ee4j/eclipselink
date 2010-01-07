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
package org.eclipse.persistence.testing.oxm.mappings.namespaces.identifiedbyname;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.oxm.XMLMarshaller;

public class IdentifiedByNameNamespaceTestSuite extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("Single Attribute Test Cases");
		//this is currently not supported, the test case be readded when bug #4243476 is fixed
    suite.addTestSuite(DefaultNamespaceTestCases.class);
    suite.addTestSuite(DefaultNamespaceXMLRootTestCases.class);
		suite.addTestSuite(GloballyDefinedNodesTestCases.class);
    suite.addTestSuite(LocallyDefinedNodesTestCases.class);
    suite.addTestSuite(NoNamespacesTestCases.class);
    return suite;
  }
		
}
