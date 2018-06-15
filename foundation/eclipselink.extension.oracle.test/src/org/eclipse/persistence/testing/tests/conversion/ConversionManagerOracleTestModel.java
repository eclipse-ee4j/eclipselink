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
package org.eclipse.persistence.testing.tests.conversion;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.conversion.ConversionManagerSystem;

public class ConversionManagerOracleTestModel extends TestModel {

    public ConversionManagerOracleTestModel() {
        setDescription("This suite tests Oracle-specific conversions through direct field mapping.");
    }

    public void addForcedRequiredSystems() {
        addForcedRequiredSystem(new ConversionManagerSystem());
    }

    public void addTests() {
        addTest(getSupportedTypesTestSuite());
    }

    public TestSuite getSupportedTypesTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConversionManagerSupportedTypesTestSuite");
        suite.setDescription("This test suite tests the getDataTypesConvertedFrom and getDataTypesConvertedTo method.");


        try{
            Class testCaseClass = Class.forName("org.eclipse.persistence.testing.tests.conversion.DataTypesConvertedFromAClassTest");
            junit.framework.Test testCase = (junit.framework.Test)testCaseClass.newInstance();
            suite.addTest(testCase);

            testCaseClass = Class.forName("org.eclipse.persistence.testing.tests.conversion.DataTypesConvertedToAClassTest");
            testCase = (junit.framework.Test)testCaseClass.newInstance();
            suite.addTest(testCase);

            testCaseClass = Class.forName("org.eclipse.persistence.testing.tests.conversion.DataTypesConvertedFromAClassForOracle9Test");
            testCase = (junit.framework.Test)testCaseClass.newInstance();
            suite.addTest(testCase);

            testCaseClass = Class.forName("org.eclipse.persistence.testing.tests.conversion.DataTypesConvertedToAClassForOracle9Test");
            testCase = (junit.framework.Test)testCaseClass.newInstance();
            suite.addTest(testCase);
        } catch (Exception e){
            getSession().logMessage("Unable to load Oracle-specific conversion tests.  This usually occurs when the tests were compiled " +
                    " on a non-Oracle environment. If you are not running on Oracle, this is not a problem.");
            if (getSession().getPlatform().isOracle9()){
                throw new TestProblemException("Could not load: Oracle-specific conversion tests", e);
            }
        }

        return suite;
    }
}
