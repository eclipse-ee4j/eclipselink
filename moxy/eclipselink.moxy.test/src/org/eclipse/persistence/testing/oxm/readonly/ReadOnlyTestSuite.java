/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.readonly;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ReadOnlyTestSuite extends TestCase {
  
  public ReadOnlyTestSuite(String name) {
    super(name);
  }
  public static void main(String[] args) {
    junit.textui.TestRunner.main(new String[] {"-c", "org.eclipse.persistence.testing.oxm.readonly.ReadOnlyTestSuite"});
  }
  public static Test suite() {
    TestSuite suite = new TestSuite("ReadOnly Test Cases");
		suite.addTestSuite(DirectMappingTestCases.class);
    suite.addTestSuite(DirectCollectionMappingTestCases.class);
    suite.addTestSuite(CompositeObjectMappingTestCases.class);
		suite.addTestSuite(CompositeCollectionMappingTestCases.class);
		suite.addTestSuite(TransformationMappingTestCases.class);
    return suite;
  }
}