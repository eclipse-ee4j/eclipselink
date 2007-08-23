/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller.twoprojects;

public class PhoneNumber {
    private String value;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean equals(Object object) {
        try {
            if (null == object) {
                return false;
            }
            PhoneNumber phoneNumber = (PhoneNumber)object;
            if (phoneNumber.getValue() == value) {
                return true;
            }
            if (null == value) {
                return false;
            } else {
                return value.equals(phoneNumber.getValue());
            }
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String string = "PhoneNumber(value='";
        string += value;
        string += "')";
        return string;
    }
}