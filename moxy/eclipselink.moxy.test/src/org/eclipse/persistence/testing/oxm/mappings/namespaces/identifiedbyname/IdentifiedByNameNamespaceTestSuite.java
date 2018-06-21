/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
