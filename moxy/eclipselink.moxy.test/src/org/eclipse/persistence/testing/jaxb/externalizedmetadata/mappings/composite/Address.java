/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - March 19/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite;

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
