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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ChangeSummaryXSDWithCSOnRootTestSuite {
    public ChangeSummaryXSDWithCSOnRootTestSuite() {
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());

    }

    /**
    *  Inherited suite mthod for generating all test cases.
    * @return
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("ChangeSummary XSD Root Test Cases");
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootDeleteComplexManyBelowRootTest.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootDetachComplexManyBelowRootTest.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUnsetComplexManyBelowRootTest.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootDeleteComplexSingleBelowRootTest.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootDetachComplexSingleBelowRootTest.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUnsetComplexSingleBelowRootTest.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUnsetComplexSingleBelowRootWithComplexManyChildrenTest.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootDeleteComplexManyLeafBelowComplexManyBelowRootTest.class));//  DetachItem2Price1
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootDeleteComplexSingleAtRootTest.class));// npe on modified root
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootDetachComplexSingleAtRootTest.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUnsetComplexSingleAtRootTest.class));

        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUnsetSimpleManyAtRootAfterSetNullTest.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUnsetSimpleSingleAtRootAfterUnsetTest.class));

        return suite;

    }
}
