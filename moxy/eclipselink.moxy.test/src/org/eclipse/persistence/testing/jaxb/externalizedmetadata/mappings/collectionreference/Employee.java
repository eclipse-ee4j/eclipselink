/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - March 25/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.collectionreference;

import java.util.List;

public class Employee {
    public List<Address> workAddresses;

    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public List<Address> getWorkAddresses() {
        wasGetCalled = true;
        return workAddresses;
    }

    public void setWorkAddresses(List<Address> workAddresses) {
        wasSetCalled = true;
        this.workAddresses = workAddresses;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Employee theObj;
        try {
            theObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }

        if (workAddresses == null) {
            return theObj.workAddresses == null;
        }

        if (theObj.workAddresses == null) {
            return false;
        }

        if (workAddresses.size() != theObj.workAddresses.size()) {
            return false;
        }

        for (Address add : workAddresses) {
            if (!addExistsInList(add, theObj.workAddresses)) {
                return false;
            }
        }
        return true;
    }

    private boolean addExistsInList(Address add, List<Address> addList) {
        for (Address listAdd : addList) {
            if (listAdd.equals(add)) {
                return true;
            }
        }
        return false;
    }
}
