/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
        addSpecialTest(new SelectComplexReverseSqrtTest());
        addTest(new SelectComplexReverseSubstringTest());
        addSpecialTest(new SelectComplexSqrtTest());
        addTest(new SelectComplexStringInTest());
        addTest(new SelectComplexStringNotInTest());
        addTest(new SelectComplexSubstringTest());
    }

    public void addSpecialTest(JPQLTestCase theTest) {
        theTest.addUnsupportedPlatform(org.eclipse.persistence.platform.database.TimesTenPlatform.class);
        addTest(theTest);
    }
}
