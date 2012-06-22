/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - September 25/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper;

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
}
