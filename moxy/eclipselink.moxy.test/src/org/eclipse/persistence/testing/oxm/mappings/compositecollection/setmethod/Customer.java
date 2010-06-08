/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.setmethod;

import java.util.Iterator;
import java.util.Vector;

public class Customer {
    private Vector phoneNumbers;
    private int collectionSizeWhenSetPhoneNumbersWasCalled;

    public Customer() {
        super();
        collectionSizeWhenSetPhoneNumbersWasCalled = 0;
        phoneNumbers = new Vector(2);
    }

    public Vector getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Vector phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        collectionSizeWhenSetPhoneNumbersWasCalled = phoneNumbers.size();
    }

    public String toString() {
        String string = "Customer(";
        Iterator iterator = phoneNumbers.iterator();
        while (iterator.hasNext()) {
            string += iterator.next().toString();
        }
        string += ")";
        return string;
    }

    public boolean equals(Object object) {
        try {
            if (this == object) {
                return true;
            }
            Customer customer = (Customer)object;
            if (customer.getPhoneNumbers() == this.getPhoneNumbers()) {
                return true;
            } else if (null == customer.getPhoneNumbers()) {
                return false;
            } else {
                return customer.getPhoneNumbers().equals(this.getPhoneNumbers());
            }
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int getCollectionSizeWhenSetPhoneNumbersWasCalled() {
        return collectionSizeWhenSetPhoneNumbersWasCalled;
    }
}
