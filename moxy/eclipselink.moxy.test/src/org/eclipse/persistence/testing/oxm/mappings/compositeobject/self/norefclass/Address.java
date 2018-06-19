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
// Denise Smith - September 21 /2009
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass;

public class Address {
    private String street;

    public Address() {
        super();
    }

    public String toString() {
        String returnString = " Address: " + getStreet();

        return returnString;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Address)) {
            return false;
        }
        Address addressObject = (Address) object;
        if (!(this.getStreet().equals(addressObject.getStreet()))) {
            return false;
        }

        return true;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

}
