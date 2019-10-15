/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// dmccann - January 06/2010 - 2.0 - Initial implementation
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
