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
// dmccann - September 25/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class Employee {
    @XmlElementWrapper(name="my-digits", nillable=true, required=true)
    public int[] digits;

    public Object thing;

    /**
     * Assumes the contents of the array is relevant, but not ordering.
     *
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee) obj;

        if (emp.digits == null && digits == null) {
            return true;
        }

        if (emp.digits == null && digits != null) {
            return false;
        }
        if (digits == null && emp.digits != null) {
            return false;
        }
        if (digits.length != emp.digits.length) {
            return false;
        }
        boolean foundMatch;
        for (int i=0; i<digits.length; i++) {
            foundMatch = false;
            for (int j=0; j<emp.digits.length; j++) {
                if (emp.digits[j] == digits[i]) {
                    foundMatch = true;
                }
            }
            if (!foundMatch) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Employee [digits=" + Arrays.toString(digits) + ", thing=" + thing + "]";
    }
}
