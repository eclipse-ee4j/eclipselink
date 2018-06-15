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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.objectreference;

public class Employee {
    public Address workAddress;

    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public Address getWorkAddress() {
        wasGetCalled = true;
        return workAddress;
    }

    public void setWorkAddress(Address workAddress) {
        wasSetCalled = true;
        this.workAddress = workAddress;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Employee theObj;
        try {
            theObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }

        if (workAddress == null) {
            return theObj.workAddress == null;
        }

        if (theObj.workAddress == null) {
            return false;
        }

        return workAddress.equals(theObj.workAddress);
    }
}
