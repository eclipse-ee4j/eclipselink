/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
