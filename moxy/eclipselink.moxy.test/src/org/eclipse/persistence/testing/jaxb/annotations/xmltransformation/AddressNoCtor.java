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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

public class AddressNoCtor {
    public String street;
    public String city;

    public AddressNoCtor(String street, String city){
        this.street = street;
        this.city = city;
    }

    public boolean equals(Object obj) {
        AddressNoCtor addr = (AddressNoCtor)obj;
        return addr.street.equals(this.street) && addr.city.equals(this.city);
    }
}
