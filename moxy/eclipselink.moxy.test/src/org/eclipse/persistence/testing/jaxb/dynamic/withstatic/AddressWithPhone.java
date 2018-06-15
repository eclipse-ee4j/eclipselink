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
//     Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.dynamic.withstatic;

import javax.xml.bind.annotation.XmlAttribute;

import org.eclipse.persistence.dynamic.DynamicEntity;

public class AddressWithPhone {

    @XmlAttribute
    public String street;

    public String city;

    public Object phone;

    public byte[] picture;

    public boolean equals(Object obj) {
       AddressWithPhone address = (AddressWithPhone)obj;

       return street.equals(address.street) && city.equals(address.city) && this.phone instanceof DynamicEntity && address.phone instanceof DynamicEntity;

    }
}
