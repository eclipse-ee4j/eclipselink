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
package org.eclipse.persistence.testing.oxm.xmlbinder.keybasedmappingtests;

public class Address {
    public String id;
    public String street;
    public String city;
    public String country;
    public String zip;

    public String getKey() {
        return id;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Address)) {
            return false;
        }
        Address tgtAddress = (Address) obj;
        return (tgtAddress.city.equals(city) &&
                tgtAddress.country.equals(country) &&
                tgtAddress.id.equals(id) &&
                tgtAddress.street.equals(street) &&
                tgtAddress.zip.equals(zip));
    }
}
