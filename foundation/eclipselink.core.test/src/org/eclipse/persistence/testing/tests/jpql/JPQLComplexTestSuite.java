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
        addSpecialTest(new ComplexReverseSqrtTest());
        addTest(new ComplexReverseSubstringTest());
        addSpecialTest(new ComplexSqrtTest());
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
}
