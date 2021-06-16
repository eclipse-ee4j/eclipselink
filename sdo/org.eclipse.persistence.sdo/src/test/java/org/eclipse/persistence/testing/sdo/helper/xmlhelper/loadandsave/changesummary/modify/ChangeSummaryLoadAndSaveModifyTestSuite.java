/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
/* $Header: ChangeSummaryLoadAndSaveModifyTestSuite.java 19-jun-2007.13:35:09 dmahar Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    dmahar      12/18/06 -
    mfobrien    12/05/06 - Creation
 */

/**
 *  @version $Header: ChangeSummaryLoadAndSaveModifyTestSuite.java 19-jun-2007.13:35:09 dmahar Exp $
 *  @author  mfobrien
 *  @since   11.1
 */

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.modify;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ChangeSummaryLoadAndSaveModifyTestSuite {
    public ChangeSummaryLoadAndSaveModifyTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper ChangeSummary Modify Tests");

        //logging cs on child
        suite.addTest(new TestSuite(ChangeSummaryRootLoggingOnModifyComplexAtCSLoadAndSaveTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryChildLoggingOnModifyComplexAtCSLoadAndSaveTestCases.class));
        suite.addTest(new TestSuite(SDOChangeSummaryUnsetTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryModifyBug6346754TestCases.class));

        return suite;
    }
}
