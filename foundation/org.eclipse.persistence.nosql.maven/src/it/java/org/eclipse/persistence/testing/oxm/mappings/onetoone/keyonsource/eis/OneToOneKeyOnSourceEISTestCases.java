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
package org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource.eis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource.eis.compositekey.CompositeKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource.eis.nestedownedtoexternalroot.NestedOwnedToExternalRootTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource.eis.nullkey.NullKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource.eis.ownedtoexternalroot.OwnedToExternalRootTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource.eis.roottoroot.RootToRootTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource.eis.nointeraction.NoInteractionTestCases;

public class OneToOneKeyOnSourceEISTestCases extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite("One To One KeyOnSource EIS Test Cases");

    suite.addTestSuite(RootToRootTestCases.class);
    suite.addTestSuite(OwnedToExternalRootTestCases.class);
    suite.addTestSuite(NestedOwnedToExternalRootTestCases.class);
        suite.addTestSuite(CompositeKeyTestCases.class);
        suite.addTestSuite(NullKeyTestCases.class);
        suite.addTestSuite(NoInteractionTestCases.class);
    return suite;
  }

  public static void main(String[] args)
  {
    String[] arguments = {"-c", "org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource.eis.OneToOneKeyOnSourceEISTestCases"};
    junit.textui.TestRunner.main(arguments);
  }
}
