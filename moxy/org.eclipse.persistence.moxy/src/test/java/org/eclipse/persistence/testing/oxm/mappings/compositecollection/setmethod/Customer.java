/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
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
