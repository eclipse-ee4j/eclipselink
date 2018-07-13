/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
