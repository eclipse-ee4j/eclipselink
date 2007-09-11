/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection;

import java.util.List;

public class Customer {
    private List contactMethods;

    public Customer() {
    }

    public void setContactMethods(List theContactMethods) {
        this.contactMethods = theContactMethods;
    }

    public List getContactMethods() {
        return contactMethods;
    }

    public boolean equals(Object theCustomer) {
        if (theCustomer instanceof Customer) {
            return contactMethods.equals(((Customer)theCustomer).getContactMethods());
        }
        return false;
    }

    public String toString() {
        return "Customer: " + contactMethods.toString();
    }
}