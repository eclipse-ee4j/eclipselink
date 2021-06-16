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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.framework.*;

public class JPQLComplexTestSuite extends TestSuite {
    public JPQLComplexTestSuite() {
        setDescription("The unit tests for EJBQL");
    }

    public void addTests() {
        addSpecialTest(new ComplexAbsTest());
        addTest(new ComplexInTest());
        addTest(new ComplexLengthTest());
        addTest(new ComplexLikeTest());
        addTest(new ComplexNotInTest());
        addTest(new ComplexNotLikeTest());
        addTest(new ComplexParameterTest());
        addSpecialTest(new ComplexReverseAbsTest());
        addTest(new ComplexReverseLengthTest());
        addTest(new ComplexReverseParameterTest());
        addSpecialTest2(new ComplexReverseSqrtTest());
        addTest(new ComplexReverseSubstringTest());
        addSpecialTest2(new ComplexSqrtTest());
        addTest(new ComplexStringInTest());
        addTest(new ComplexStringNotInTest());
        addTest(new ComplexSubstringTest());
        addTest(new ComplexNotLikeTest());
        addTest(new ComplexNestedOneToManyUsingInClause());
        addTest(new ComplexInheritanceTest());
        addTest(new ComplexInheritanceUsingNamedQueryTest());
        addTest(AggregateTest.getComplexAggregateTestSuit());
    }

    //{{DECLARE_CONTROLS
    //}}
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
