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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.create;
import junit.framework.Test;
import junit.framework.TestSuite;

public class ChangeSummaryLoadAndSaveCreateTestSuite {
public ChangeSummaryLoadAndSaveCreateTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper Create ChangeSummary Tests");

        suite.addTestSuite(ChangeSummaryRootCreateAlreadySetTestCases.class);
        suite.addTestSuite(ChangeSummaryCreateBug6120161TestCases.class);
        suite.addTestSuite(ChangeSummaryCreateBug6346754TestCases.class);

        return suite;
    }
}
