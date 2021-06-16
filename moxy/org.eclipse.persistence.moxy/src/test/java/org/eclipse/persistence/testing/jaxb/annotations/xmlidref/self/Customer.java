/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref.self;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
