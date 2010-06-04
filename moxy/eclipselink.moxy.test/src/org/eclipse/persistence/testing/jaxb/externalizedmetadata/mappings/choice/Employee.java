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
 * dmccann - April 01/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice;

public class Employee {
    public Object thing;
    public Object readOnlyThing;
    public Object writeOnlyThing;

    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public Object getThing() {
        wasGetCalled = true;
        return thing;
    }

    public void setThing(Object thing) {
        wasSetCalled = true;
        this.thing = thing;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Employee empObj;
        try {
            empObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }

        if (thing == null) {
            if (empObj.thing != null) {
                return false;
            }
        } else if (!thing.equals(empObj.thing)){
            return false;
        }
        if (readOnlyThing == null) {
            if (empObj.readOnlyThing != null) {
                return false;
            }
        } else if (!readOnlyThing.equals(empObj.readOnlyThing)){
            return false;
        }
        if (writeOnlyThing == null) {
            if (empObj.writeOnlyThing != null) {
                return false;
            }
        } else if (!writeOnlyThing.equals(empObj.writeOnlyThing)){
            return false;
        }
        
        return true;
    }
}