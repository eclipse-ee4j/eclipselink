/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
