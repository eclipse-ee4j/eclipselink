/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - January 06/2010 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes;

public class Employee {
    public java.util.GregorianCalendar hireDate;
    public java.math.BigDecimal lengthOfEmployment;
    
    public boolean equals(Object obj) {
        Employee testEmp;
        try {
            testEmp = (Employee) obj;
        } catch (ClassCastException cce) {
            // not an instance of Employee
            return false;
        }
        if (hireDate == null) {
            if (testEmp.hireDate != null) {
                // both hireDates are not null
                return false;
            }
        } else if (hireDate.compareTo(testEmp.hireDate) != 0) {
            // dates are not equal
            return false;
        }
        if (lengthOfEmployment == null) {
            if (testEmp.lengthOfEmployment != null) {
                // both lengthOfEmployments are not null
                return false;
            }
        } else if (lengthOfEmployment.compareTo(testEmp.lengthOfEmployment) != 0) {
            // lengthOfEmployments are not equal
            return false;
        }
        return true;
    }
}
