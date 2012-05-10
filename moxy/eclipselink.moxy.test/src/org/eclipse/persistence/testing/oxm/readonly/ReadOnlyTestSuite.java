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
