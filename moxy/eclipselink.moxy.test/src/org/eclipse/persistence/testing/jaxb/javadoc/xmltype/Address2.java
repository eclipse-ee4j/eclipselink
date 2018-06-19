/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

import javax.xml.bind.annotation.*;

@XmlType(propOrder = {})
@XmlRootElement
public class Address2 {

    public String name, street, city, state;

    String getName() {
        return name;
    }

    void setName(String n) {
        this.name = n;
    }

    String getStreet() {
        return street;
    }

    void setStreet(String str) {
        this.street = str;
    }

    String getCity() {
        return city;
    }

    void setCity(String c) {
        this.city = c;
    }

    String getState() {
        return state;
    }

    void setState(String s) {
        this.state = s;
    }

    public boolean equals(Object obj) {
        Address2 addr = (Address2) obj;
        return name.equals(addr.name) && city.equals(addr.city)
                && street.equals(addr.street) && state.equals(addr.state);
    }
}
