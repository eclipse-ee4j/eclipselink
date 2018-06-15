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
//     mmacivor - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

public class Address {

    @XmlAttribute
    @XmlID
    public String id;
    public String street;
    public String city;

    public boolean equals(Object obj) {
        if(!(obj instanceof Address)) {
            return false;
        }
        Address addr = (Address)obj;
        return addr.id.equals(id) && addr.street.equals(street) && addr.city.equals(city);
    }
}
