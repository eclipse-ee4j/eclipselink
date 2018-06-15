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
package org.eclipse.persistence.testing.jaxb.xmladapter.direct.objectlist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Customer {

    private List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

    @XmlJavaTypeAdapter(ObjectListAdapter.class)
    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public boolean equals(Object object) {
        if(null == object || Customer.class != object.getClass()) {
            return false;
        }
        Customer test = (Customer) object;
        if(null == phoneNumbers) {
            return null == test.getPhoneNumbers();
        } else {
            return phoneNumbers.equals(test.getPhoneNumbers());
        }
    }

}
