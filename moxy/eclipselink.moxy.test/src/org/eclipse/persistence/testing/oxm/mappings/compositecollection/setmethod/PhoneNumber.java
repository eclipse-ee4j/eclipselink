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
