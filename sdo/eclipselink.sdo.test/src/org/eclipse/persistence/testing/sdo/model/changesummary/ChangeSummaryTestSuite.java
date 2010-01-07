/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.sdo.model.changesummary.sequence.ChangeSummaryGetOldSequenceTestCases;
import org.eclipse.persistence.testing.sdo.model.changesummary.sequence.ChangeSummaryXSDWithCSonRootUndoWSeqTestCases;
import org.eclipse.persistence.testing.sdo.model.changesummary.sequence.ChangeSummaryXSDwSeqTestCases;

public class ChangeSummaryTestSuite {
    public ChangeSummaryTestSuite() {
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());

    }

    /**
    *  Inherited suite method for generating all test cases.
    * @return
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("All ChangeSummary Tests");
        suite.addTest(new TestSuite(ChangeSummaryExceptionTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryBeginLoggingEndLoggingIsLoggingTest.class));
        suite.addTest(new TestSuite(ChangeSummaryGetOldContainerContainmentPropertyTest.class));
        suite.addTest(new TestSuite(ChangeSummaryGetOldValueTest.class));
        // sequences
        suite.addTest(new TestSuite(ChangeSummaryGetOldSequenceTestCases.class));
        // 20070501 add sequences support in cs.undoChanges()
        suite.addTest(new TestSuite(ChangeSummaryXSDwSeqTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUndoWSeqTestCases.class));

        // model tests        
        suite.addTest(new TestSuite(ChangeSummaryBug6599313TestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryIsCreatedIsDeletedIsModifiedTest.class));
        suite.addTest(new TestSuite(ChangeSummaryGetChangedDataObjectsTest.class));
        suite.addTest(new TestSuite(ChangeSummaryInitalizedInCreatingDataObjectTests.class));
        suite.addTest(new TestSuite(ChangeSummaryCreatedModifiedDeletedTests.class));
        suite.addTest(new TestSuite(ChangeSummaryVolumetricsTest.class));
        suite.addTest(new TestSuite(ChangeSummarySetSameValueAfterLogonTests.class));
        suite.addTest(new TestSuite(ChangeSummaryMoveFromNullChangeSummaryToChangeSummaryTests.class));
        suite.addTest(new TestSuite(ChangeSummaryBeginLoggingEndLoggingCombiningTests.class));
        suite.addTest(new TestSuite(ChangeSummaryCopyTestCases.class));
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonChildUndoTestCases.class));// Undo tests that use an XML schema to load types
        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUndoTestCases.class));// Undo tests that use an XML schema to load types
        suite.addTest(new TestSuite(ChangeSummaryUndoChangeTest.class));// Undo tests that use DataFactory model to load types
        // 20070211: 1 extra failure until #5876860 is fixed
        suite.addTest(new TestSuite(ChangeSummaryXSDTestCases.class));// to be removed when we have all tests extracted
        // the following hybrid Model/XML tests will contain 1 model test and 10 inherited from the XML suite

        // tests against multiple changeSummaries (onRoot = at the CS level, belowRoot = on a child of a DO at the CS level)

        // the following 3 tests need to be finalized as part of bug#5837243

        //suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUnsetSimpleManyAtRootAfterUnsetTest.class));
        //      suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUnsetSimpleSingleAtRootAfterSetNullTest.class)); // Exception executing xpath
        //suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootReSetComplexManyBelowRootTest.class));        

        /*        suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootDeleteComplexSingleBelowRootTest.class));

                suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootReSetComplexManyBelowRootTest.class));
                suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootSetComplexManyBelowRootTest.class));
                suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootMoveComplexManyBelowRootTest.class));
                suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootMoveComplexSingleBelowRootTest.class));
                suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootSetComplexSingleBelowRootTest.class));
                suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootReSetComplexSingleBelowRootTest.class));
                suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootUnsetComplexSingleBelowRootTest.class));
                suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootDetachComplexSingleBelowRootTest.class));
                suite.addTest(new TestSuite(ChangeSummaryXSDWithCSonRootCreateComplexSingleBelowRootTest.class));
        */
        suite.addTest(new TestSuite(ChangeSummaryXSDQuoteDataTestCases.class));// to be removed when we have all tests extracted
        return suite;
    }
}
