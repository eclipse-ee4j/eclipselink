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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.withgroupingelement.CompositeCollectionWithGroupingElementIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.withoutgroupingelement.CompositeCollectionWithoutGroupingElementIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.withgroupingelement.CompositeCollectionSingleElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.withgroupingelement.CompositeCollectionNullElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.withgroupingelement.CompositeCollectionEmptyCollectionTestCases;

public class IdentifiedByNameTestCases extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("Composite Collection:  Identified By Name Test Cases");
    suite.addTestSuite(CompositeCollectionWithGroupingElementIdentifiedByNameTestCases.class);
    suite.addTestSuite(CompositeCollectionSingleElementTestCases.class);
        suite.addTestSuite(CompositeCollectionNullElementTestCases.class);
       suite.addTestSuite(CompositeCollectionEmptyCollectionTestCases.class);
    suite.addTestSuite(CompositeCollectionWithoutGroupingElementIdentifiedByNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.withoutgroupingelement.CompositeCollectionSingleElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.withoutgroupingelement.CompositeCollectionNullElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.withoutgroupingelement.CompositeCollectionEmptyCollectionTestCases.class);
    return suite;
  }

  public static void main(String[] args)
  {
    String[] arguments = {"-c", "org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.IdentifiedByNameTestCases"};
    junit.textui.TestRunner.main(arguments);
  }
}
