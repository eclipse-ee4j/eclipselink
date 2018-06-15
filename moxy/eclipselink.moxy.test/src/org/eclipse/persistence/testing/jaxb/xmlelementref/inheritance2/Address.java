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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.inheritance2;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Address extends ContactInfo {

    private String street;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public boolean equals(Object object) {
        if(null == object || object.getClass() != Address.class) {
            return false;
        }
        Address test = (Address) object;
        if(null == street) {
            return null == test.getStreet();
        } else {
            return street.equals(test.getStreet());
        }
    }

}
