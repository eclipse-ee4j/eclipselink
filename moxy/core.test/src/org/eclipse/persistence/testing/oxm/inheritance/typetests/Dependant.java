/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

public class Dependant {
    private Address address;
    //private CanadianAddress address;

    public Dependant() {
    }

    //public void setAddress(CanadianAddress theAddress) {
    public void setAddress(Address theAddress) {
        this.address = theAddress;
    }

    //public CanadianAddress getAddress() {
    public Address getAddress() {
        return address;
    }

    public boolean equals(Object theDependant) {
        if (theDependant instanceof Dependant) {
            return address.equals(((Dependant)theDependant).getAddress());
        }
        return false;
    }

    public String toString() {
        return "Dependant: " + address.toString();
    }
}