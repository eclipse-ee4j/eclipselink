/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
