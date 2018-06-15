/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref.self;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Customer {

    @XmlElementRef
    //@XmlJavaTypeAdapter(AddressAdapter.class)
    public AddressSelfSource address;

    public PhoneNumber phoneNumber;

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Customer test = (Customer) obj;

        if(null == address) {
            if(null != test.address) {
                return false;
            }
        } else if(!address.equals(test.address)) {
            return false;
        }

        if(null == phoneNumber) {
            return null == test.phoneNumber;
        } else {
            return phoneNumber.equals(test.phoneNumber);
        }
    }

}
