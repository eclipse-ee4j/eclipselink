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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.inheritance.typetests.any.collection;

import java.util.List;

public class Customer {
    private List contacts;

    public Customer() {
    }

    public void setContactMethods(List theContacts) {
        this.contacts = theContacts;
    }

    public List getContactMethods() {
        return contacts;
    }

    public boolean equals(Object theCustomer) {
        if (theCustomer instanceof Customer) {
            return contacts.equals(((Customer)theCustomer).getContactMethods());
        }
        return false;
    }

    public String toString() {
        return "Customer: " + contacts.toString();
    }
}
