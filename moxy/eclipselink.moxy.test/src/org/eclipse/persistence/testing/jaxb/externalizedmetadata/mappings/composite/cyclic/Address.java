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
// dmccann - March 19/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite.cyclic;

public class Address {
    public String city = "";
    public String street = "";
    public String province = "";
    public String postalCode = "";

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Address addObj;
        try {
            addObj = (Address) obj;
        } catch (ClassCastException e) {
            return false;
        }

        return (city.equals(addObj.city) &&
                street.equals(addObj.street) &&
                province.equals(addObj.province) &&
                postalCode.equals(addObj.postalCode));
    }
}
