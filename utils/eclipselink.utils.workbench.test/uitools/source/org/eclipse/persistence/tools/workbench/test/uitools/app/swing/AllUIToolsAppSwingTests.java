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
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 *
 */
public class AllUIToolsAppSwingTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllUIToolsAppSwingTests.class));

        suite.addTest(CheckBoxModelAdapterTests.suite());
        suite.addTest(ComboBoxModelAdapterTests.suite());
        suite.addTest(DateSpinnerModelAdapterTests.suite());
        suite.addTest(DocumentAdapterTests.suite());
        suite.addTest(ListModelAdapterTests.suite());
        suite.addTest(ListSpinnerModelAdapterTests.suite());
        suite.addTest(NumberSpinnerModelAdapterTests.suite());
        suite.addTest(ObjectListSelectionModelTests.suite());
        suite.addTest(PrimitiveListTreeModelTests.suite());
        suite.addTest(RadioButtonModelAdapterTests.suite());
        suite.addTest(SpinnerModelAdapterTests.suite());
        suite.addTest(TableModelAdapterTests.suite());
        suite.addTest(TreeModelAdapterTests.suite());

        return suite;
    }

    private AllUIToolsAppSwingTests() {
        super();
    }

}
