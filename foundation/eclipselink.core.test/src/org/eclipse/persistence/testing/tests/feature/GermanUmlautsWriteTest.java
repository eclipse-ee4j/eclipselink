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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.framework.*;

// Test for bug 3286022: BINDING CLOBS DOESN'T WORK WITH NON ASCII CHARACTERS.
// It is possible to test Strings instead of CLOBs, because both handled with the same piece of code:
// the same problem occurred with Strings before the fix, too.
//
// German umlauts are: O with : above it.
public class GermanUmlautsWriteTest extends InsertObjectTest {
    boolean usesStringBindingOriginal;
    int stringBindingSizeOriginal;

    public GermanUmlautsWriteTest() {
        super();
        Employee employee = new Employee();

        // for some reason "" are not read correctly on testing Linux machines anymore:
        // String str = "";
        // System.out.println("str = " + str + codes(str));
        // prints:
        // str = ????(65533,65533,65533,65533)
        // To make the test pass on testing Linux machines set the umlauts using their codes.	
        //employee.setFirstName("");
        char[] umlautsCharCodes = { 246, 228, 252, 223 };
        employee.setFirstName(new String(umlautsCharCodes));
        this.originalObject = employee;
        setDescription("Verifies that 'some german Umlat characters' are written correctly in case string binding is used");
    }

    protected void setup() {
        if (getSession().getPlatform().isTimesTen()) {
            throw new TestWarningException("This test is not supported on this platform.");
        }

        super.setup();
        usesStringBindingOriginal = getSession().getPlatform().usesStringBinding();
        if (!usesStringBindingOriginal) {
            getSession().getPlatform().setUsesStringBinding(true);
        }
        stringBindingSizeOriginal = getSession().getPlatform().getStringBindingSize();
        if (stringBindingSizeOriginal != 1) {
            getSession().getPlatform().setStringBindingSize(1);
        }
    }

    protected void test() {
        String strOriginal = ((Employee)originalObject).getFirstName();
        getSession().logMessage("original firstName = " + strOriginal + codes(strOriginal));
        super.test();
    }

    protected void verify() {
        try {
            super.verify();
        } catch (TestErrorException ex) {
            String strOriginal = ((Employee)originalObject).getFirstName();
            String strFromDatabase = ((Employee)objectFromDatabase).getFirstName();
            if (!strOriginal.equals(strFromDatabase)) {
                throw new TestErrorException("String read is " + strFromDatabase + codes(strFromDatabase) + " instead of expected " + strOriginal + codes(strOriginal));
            } else {
                throw ex;
            }
        }
    }

    public void reset() {
        if (getSession().getPlatform().usesStringBinding() != usesStringBindingOriginal) {
            getSession().getPlatform().setUsesStringBinding(usesStringBindingOriginal);
        }
        if (getSession().getPlatform().getStringBindingSize() != stringBindingSizeOriginal) {
            getSession().getPlatform().setStringBindingSize(stringBindingSizeOriginal);
        }
        super.reset();
    }

    protected String codes(String str) {
        String strCodes = "(";
        for (int i = 0; i < str.length(); i++) {
            if (i != 0) {
                strCodes = strCodes + ",";
            }
            int code = str.charAt(i);
            strCodes = strCodes + code;
        }
        strCodes = strCodes + ")";
        return strCodes;
    }
}
