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

public class JPQLUnitTestSuite extends org.eclipse.persistence.testing.framework.TestSuite {
    public JPQLUnitTestSuite() {
        setDescription("The unit tests for EJBQL");
    }

    public void addTests() {
        //SELECT tests
        //invalid EJBQL-cannot change reference class
        //addTest(new SimpleSelectPhoneNumber());
        //addTest(new SimpleSelectPhoneNumberWithEmployee());
        //addTest(new SimpleSelectPhoneNumberWithEmployeeWithExplicitJoin());
        //addTest(new SimpleSelectPhoneNumberWithEmployeeWithFirstNameFirst());
        //addTest(new SimpleSelectPhoneNumberFullyQualifiedInSELECT());
        //SELECT attribute tests
        addTest(new SimpleSelectPhoneNumberAreaCode());
        addTest(new SimpleSelectPhoneNumberAreaCodeWithEmployee());
        addTest(new SimpleSelectPhoneNumberNumberWithEmployeeWithExplicitJoin());
        addTest(new SimpleSelectPhoneNumberNumberWithEmployeeWithFirstNameFirst());

        //invalid EBJQL
        //addTest(new SimpleSelectPhoneNumberAreaCodeFullyQualifiedInSELECT());
    }
}
