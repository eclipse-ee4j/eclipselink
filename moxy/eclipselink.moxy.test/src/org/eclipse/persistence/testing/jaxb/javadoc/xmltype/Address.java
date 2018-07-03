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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

import javax.xml.bind.annotation.*;

@XmlType(propOrder={"street", "city" , "state", "name" })
@XmlRootElement
public class Address {

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
        Address addr = (Address)obj;
        return name.equals(addr.name) && city.equals(addr.city) && street.equals(addr.street) && state.equals(addr.state);
    }
}

