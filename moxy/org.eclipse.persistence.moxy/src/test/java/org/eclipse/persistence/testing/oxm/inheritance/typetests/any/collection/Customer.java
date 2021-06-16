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
