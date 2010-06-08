/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - March 25/2010 - 2.1 - Initial implementation
 ******************************************************************************/
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