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
import org.eclipse.persistence.queries.*;

// Domain Imports
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.ReadAllCallTest;
import org.eclipse.persistence.testing.framework.ReadObjectCallTest;

public class JPQLSimpleSelectTestSuite extends TestSuite {
    public JPQLSimpleSelectTestSuite() {
        setDescription("The unit tests for EJBQL");
    }

    public void addTests() {
        addSpecialTest(new SelectSimpleAbsTest());
        addTest(new SelectSimpleBetweenTest());
        addTest(new SelectSimpleBetweenAndTest());
        addTest(SelectSimpleBooleanTest.getSimpleTrueTest());
        addTest(SelectSimpleBooleanTest.getSimpleFalseTest());
        addTest(SelectSimpleBooleanTest.getSimpleNotEqualsTrueTest());
        addTest(SelectSimpleBooleanTest.getSimpleNotEqualsFalseTest());
        addTest(new SelectSimpleConcatTest());
        addTest(new SelectSimpleDoubleOrTest());
        addTest(new SelectSimpleEqualsTest());
        addTest(new SelectSimpleEqualsBracketsTest());
        addTest(new SelectSimpleEqualsMultipleDots());
        addTest(new SelectSimpleEqualsWithAs());
        addTest(new SelectSimpleFromFailed());
        addTest(new SelectSimpleInTest());
        addTest(new SelectSimpleInOneDotTest());
        //        addTest(new SelectSimpleInClauseInFromEmployeeManagerAndPhoneNumbers());
        addTest(new SelectSimpleLengthTest());
        addTest(new SelectSimpleLikeTest());
        addTest(new SelectSimpleLikeEscapeTest());
        addTest(new SelectSimpleNotBetweenTest());
        addTest(new SelectSimpleNotEqualsVariablesIngeter());
        addTest(new SelectSimpleNotInTest());
        addTest(new SelectSimpleNotLikeTest());
        addTest(new SelectSimpleOrTest());
        addTest(new SelectSimpleParameterTest());
        addSpecialTest(new SelectSimpleReverseAbsTest());
        addTest(new SelectSimpleReverseConcatTest());
        addTest(new SelectSimpleReverseEqualsTest());
        addTest(new SelectSimpleReverseLengthTest());
        addTest(new SelectSimpleReverseParameterTest());
        addSpecialTest2(new SelectSimpleReverseSqrtTest());
        addTest(new SelectSimpleReverseSubstringTest());
        addSpecialTest2(new SelectSimpleSqrtTest());
        addTest(new SelectSimpleSubstringTest());

        //SELECT tests
        //        addTest(new SimpleSelectPhoneNumber());
        //BAD EJBQL
        //addTest(new SimpleSelectPhoneNumberFullyQualifiedInSELECT());
        //        addTest(new SimpleSelectPhoneNumberWithEmployee());
        //BAD EJBQL
        //addTest(new SimpleSelectPhoneNumberWithEmployeeWithExplicitJoin());
        //BAD EJBQL
        //addTest(new SimpleSelectPhoneNumberWithEmployeeWithFirstNameFirst());
        //SELECT changing the reference class tests
        //        addTest(new SimpleSelectPhoneNumberOwnerAddressesUsingInClause());
        //SELECT attribute tests
        //        addTest(new SimpleSelectPhoneNumberAreaCode());
        //EJBQL no longer valid
        //addTest(new SimpleSelectPhoneNumberAreaCodeFullyQualifiedInSELECT());
        //        addTest(new SimpleSelectPhoneNumberAreaCodeWithEmployee());
        //EJBQL no longer valid
        //addTest(new SimpleSelectPhoneNumberNumberWithEmployeeWithExplicitJoin());
        //        addTest(new SimpleSelectPhoneNumberNumberWithEmployeeWithFirstNameFirst());
        //SELECT from CALLS
        addTest(new ReadObjectCallTest(Employee.class, new JPQLCall("SELECT OBJECT(emp) FROM Employee emp")));
        addTest(new ReadAllCallTest(Employee.class, 12, new JPQLCall("SELECT OBJECT(emp) FROM Employee emp")));
        addTest(new SelectSimpleMemberOfTest());
        addTest(new SelectSimpleMemberOfWithParameterTest());
        addTest(new SelectSimpleNotMemberOfWithParameterTest());
        addTest(new SelectSimpleBetweenWithParametersTest());
        addTest(SelectSimpleNullTest.getSimpleNullTest());
        addTest(SelectSimpleNullTest.getSimpleNotNullTest());
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
