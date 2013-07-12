/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.3 - initial implementation
 ******************************************************************************/
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
