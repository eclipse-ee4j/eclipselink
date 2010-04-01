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
 * dmccann - April 019/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choicecollection;

import java.util.List;

public class Employee {
    public List<Object> things;

    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public List<Object> getThings() {
        wasGetCalled = true;
        return things;
    }

    public void setThings(List<Object> things) {
        wasSetCalled = true;
        this.things = things;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Employee empObj;
        try {
            empObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }

        if (things == null) {
            return empObj.things == null;
        }
        for (Object thing : things) {
            if (!thingExistsInList(thing, empObj.things)) {
                return false;
            }
        }
        return true;
    }

    private boolean thingExistsInList(Object thing, List<Object> things) {
        for (Object listThing : things) {
            if (listThing.equals(thing)) {
                return true;
            }
        }
        return false;
    }
}