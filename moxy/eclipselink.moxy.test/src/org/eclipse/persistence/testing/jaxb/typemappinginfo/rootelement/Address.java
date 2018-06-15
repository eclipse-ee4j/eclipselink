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
//     bdoughan - January 13/2010 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo.rootelement;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="annotation-address")
public class Address {

    private String street;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Address testAddress = (Address) obj;
            if(null == street) {
                return null == testAddress.getStreet();
            } else {
                return street.equals(testAddress.getStreet());
            }
        } catch(ClassCastException e) {
            return false;
        }
    }

}
