/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyposition;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyposition.withgroupingelement.CompositeCollectionWithGroupingElementIdentifiedByPositionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyposition.withoutgroupingelement.CompositeCollectionWithoutGroupingElementIdentifiedByPositionTestCases;

public class IdentifiedByPositionTestCases extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("Composite Collection:  Identified By Position Test Cases");
    suite.addTestSuite(CompositeCollectionWithGroupingElementIdentifiedByPositionTestCases.class);
    //suite.addTestSuite(CompositeCollectionWithoutGroupingElementIdentifiedByPositionTestCases.class);
    return suite;
  }

  public static void main(String[] args)
  {
    String[] arguments = {"-c", "org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyposition.IdentifiedByPositionTestCases"};
    junit.textui.TestRunner.main(arguments);
  }
}
