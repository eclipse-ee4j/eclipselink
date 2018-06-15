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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyontarget.eis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyontarget.eis.roottoroot.RootToRootTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyontarget.eis.ownedtoexternalroot.OwnedToExternalRootTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyontarget.eis.nestedownedtoexternalroot.NestedOwnedToExternalRootTestCases;

public class OneToManyKeyOnTargetEISTestCases extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("One To Many KeyOnTarget EIS Test Cases");

    suite.addTestSuite(RootToRootTestCases.class);
    suite.addTestSuite(OwnedToExternalRootTestCases.class);
    suite.addTestSuite(NestedOwnedToExternalRootTestCases.class);

    return suite;
  }

  public static void main(String[] args)
  {
    String[] arguments = {"-c", "org.eclipse.persistence.testing.oxm.mappings.onetomany.keyontarget.eis.OneToManyKeyOnTargetEISTestCases"};
    junit.textui.TestRunner.main(arguments);
  }
}
