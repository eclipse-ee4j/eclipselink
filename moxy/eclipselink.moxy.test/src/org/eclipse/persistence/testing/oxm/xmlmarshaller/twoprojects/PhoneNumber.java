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
//     Oracle - initial API and implementation from Oracle TopLink
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
