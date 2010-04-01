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
 * dmccann - March 24/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.compositecollection;

import java.util.List;

public class Employee {
    public int id;
    public List<Address> addresses;
    
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public int getId() {
        wasGetCalled = true;
        return id;
    }

    public void setId(int id) {
        wasSetCalled = true;
        this.id = id;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Employee empObj;
        try {
            empObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }
        if (id != empObj.id) { return false; }
        if (addresses == null && empObj.addresses != null) {
            return false;
        }
        for (Address add : addresses) {
            if (!addressExistsInList(add, empObj.addresses)) {
                return false;
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