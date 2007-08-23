/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.onetomany;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.OneToManyKeyOnSourceEISTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyontarget.eis.OneToManyKeyOnTargetEISTestCases;

public class OneToManyMappingTestSuite extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("One To One Mapping Test Cases");

		suite.addTest(OneToManyKeyOnSourceEISTestCases.suite());
		suite.addTest(OneToManyKeyOnTargetEISTestCases.suite());

    return suite;
  }

  public static void main(String[] args)
  {
    String[] arguments = {"-c", "org.eclipse.persistence.testing.oxm.mappings.onetoone.OneToManyMappingTestSuite"};
    junit.swingui.TestRunner.main(arguments);
  }
}
