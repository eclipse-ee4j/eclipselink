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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.framework.*;

public class JPQLComplexSelectTestSuite extends TestSuite {
    public JPQLComplexSelectTestSuite() {
        setDescription("The unit tests for EJBQL");
    }

    public void addTests() {
        addSpecialTest(new SelectComplexAbsTest());
        addTest(new SelectComplexInTest());
        addTest(new SelectComplexLengthTest());
        addTest(new SelectComplexLikeTest());
        addTest(new SelectComplexNotLikeTest());
        addTest(new SelectComplexNotInTest());
        addTest(new SelectComplexParameterTest());
        addSpecialTest(new SelectComplexReverseAbsTest());
        addTest(new SelectComplexReverseLengthTest());
        addTest(new SelectComplexReverseParameterTest());
        addSpecialTest2(new SelectComplexReverseSqrtTest());
        addTest(new SelectComplexReverseSubstringTest());
        addSpecialTest2(new SelectComplexSqrtTest());
        addTest(new SelectComplexStringInTest());
        addTest(new SelectComplexStringNotInTest());
        addTest(new SelectComplexSubstringTest());
    }

    public void addSpecialTest(JPQLTestCase theTest) {
        theTest.addUnsupportedPlatform(org.eclipse.persistence.platform.database.TimesTenPlatform.class);
        addTest(theTest);
    }

    public void addSpecialTest2(JPQLTestCase theTest) {
        theTest.addUnsupportedPlatform(org.eclipse.persistence.platform.database.TimesTenPlatform.class);
        theTest.addUnsupportedPlatform(org.eclipse.persistence.platform.database.SymfowarePlatform.class);
        addTest(theTest);
    }
}
