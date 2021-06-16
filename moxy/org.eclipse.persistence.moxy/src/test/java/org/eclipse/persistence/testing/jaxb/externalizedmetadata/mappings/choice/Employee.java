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
// dmccann - April 01/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice;

public class Employee {
    public Object thing;
    public Object readOnlyThing;
    public Object writeOnlyThing;

    @jakarta.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @jakarta.xml.bind.annotation.XmlTransient
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
