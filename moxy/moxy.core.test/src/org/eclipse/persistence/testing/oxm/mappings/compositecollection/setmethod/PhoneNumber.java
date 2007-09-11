/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.setmethod;

public class PhoneNumber {
    private String value;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return "PhoneNumber(" + value + ")";
    }

    public boolean equals(Object object) {
        try {
            if (this == object) {
                return true;
            }
            PhoneNumber phoneNumber = (PhoneNumber)object;
            if (phoneNumber.getValue() == this.getValue()) {
                return true;
            } else if (null == phoneNumber.getValue()) {
                return false;
            } else {
                return phoneNumber.getValue().equals(this.getValue());
            }
        } catch (ClassCastException e) {
            return false;
        }
    }
}