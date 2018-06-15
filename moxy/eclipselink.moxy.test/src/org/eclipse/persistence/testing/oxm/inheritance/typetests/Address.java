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
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

public class Address extends ContactMethod {
    private String street;

    public Address() {
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public boolean equals(Object theAddress) {
        if (theAddress instanceof Address) {
            return street.equals(((Address)theAddress).getStreet());
        }
        return false;
    }

    public String toString() {
        return "Address: " + street;
    }
}
