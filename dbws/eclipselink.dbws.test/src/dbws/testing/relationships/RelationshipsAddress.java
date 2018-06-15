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
//     Mike Norman - May 2008, created DBWS test package
package dbws.testing.relationships;

public class RelationshipsAddress {

    public int addressId;
    public String street;
    public String city;
    public String province;
    public String country;
    public String postalCode;

    public RelationshipsAddress() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RelationshipsAddress");
        sb.append("[");
        sb.append(addressId);
        sb.append("]");
        sb.append(street);
        sb.append(" ");
        sb.append(city);
        sb.append(" ");
        sb.append(province);
        sb.append(" ");
        sb.append(postalCode);
        sb.append(" ");
        sb.append(country);
        return sb.toString();
    }
}
