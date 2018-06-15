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
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

public class Customer {
    private ContactMethod contact;

    public Customer() {
    }

    public void setContact(ContactMethod theContact) {
        this.contact = theContact;
    }

    public ContactMethod getContact() {
        return contact;
    }

    public boolean equals(Object theCustomer) {
        if (theCustomer instanceof Customer) {
            return contact.equals(((Customer)theCustomer).getContact());
        }
        return false;
    }

    public String toString() {
        return "Customer: " + contact.toString();
    }
}
