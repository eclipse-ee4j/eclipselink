/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - March 24/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.compositecollection;

import java.util.List;

public class Employee {
    public int id;
    public List<Address> addresses;
    public List<Address> readOnlyAddressList;
    public List<Address> writeOnlyAddressList;

    @jakarta.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @jakarta.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public List<Address> getAddresses() {
        wasGetCalled = true;
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        wasSetCalled = true;
        this.addresses = addresses;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Employee empObj;
        try {
            empObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }
        if (id != empObj.id) {
            return false;
        }
        if (addresses == null) {
            if (empObj.addresses != null) {
                return false;
            }
        } else {
            if (empObj.addresses == null) {
                return false;
            }
            for (Address add : addresses) {
                if (!addressExistsInList(add, empObj.addresses)) {
                    return false;
                }
            }
        }
        if (readOnlyAddressList == null) {
            if (empObj.readOnlyAddressList != null) {
                return false;
            }
        } else {
            if (empObj.readOnlyAddressList == null) {
                return false;
            }
            for (Address add : readOnlyAddressList) {
                if (!addressExistsInList(add, empObj.readOnlyAddressList)) {
                    return false;
                }
            }
        }
        if (writeOnlyAddressList == null) {
            if (empObj.writeOnlyAddressList != null) {
                return false;
            }
        } else {
            if (empObj.writeOnlyAddressList == null) {
                return false;
            }
            for (Address add : writeOnlyAddressList) {
                if (!addressExistsInList(add, empObj.writeOnlyAddressList)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean addressExistsInList(Address address, List<Address> addList) {
        for (Address listAddress : addList) {
            if (listAddress.equals(address)) {
                return true;
            }
        }
        return false;
    }
}
