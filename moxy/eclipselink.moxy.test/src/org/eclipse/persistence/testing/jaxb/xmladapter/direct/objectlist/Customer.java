/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
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