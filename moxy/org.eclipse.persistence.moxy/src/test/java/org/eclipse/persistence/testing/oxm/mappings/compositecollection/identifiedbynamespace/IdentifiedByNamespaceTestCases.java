/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withgroupingelement.CompositeCollectionWithGroupingByNamespaceNoRefClassTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withgroupingelement.CompositeCollectionWithGroupingElementIdentifiedByNamespaceTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withgroupingelement.CompositeCollectionWithGroupingTextTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.withoutgroupingelement.CompositeCollectionWithoutGroupingElementIdentifiedByNamespaceTestCases;

public class IdentifiedByNamespaceTestCases extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("Composite Collection:  Identified By Namespace Test Cases");
    suite.addTestSuite(CompositeCollectionWithGroupingElementIdentifiedByNamespaceTestCases.class);
    suite.addTestSuite(CompositeCollectionWithGroupingByNamespaceNoRefClassTestCases.class);
    suite.addTestSuite(CompositeCollectionWithoutGroupingElementIdentifiedByNamespaceTestCases.class);
    suite.addTestSuite(CompositeCollectionWithGroupingTextTestCases.class);
    return suite;
  }

  public static void main(String[] args)
  {
    String[] arguments = {"-c", "org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.IdentifiedByNamespaceTestCases"};
    junit.textui.TestRunner.main(arguments);
  }

}
