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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute;

import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.Root;

public class Employee {
    public Map<QName, String> stuff;

    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public Map<QName, String> getStuff() {
        wasGetCalled = true;
        return stuff;
    }

    public void setStuff(Map<QName, String> stuff) {
        wasSetCalled = true;
        this.stuff = stuff;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Employee empObj;
        try {
            empObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }

        Map thisStuff = this.stuff;
        Map otherStuff = empObj.stuff;

        if (thisStuff == null) {
            return (otherStuff == null || otherStuff.size() == 0);
        }

        if (otherStuff == null) {
            return (thisStuff.size() == 0);
        }

        if (thisStuff.size() != otherStuff.size()) {
            return false;
        }

        Iterator values1 = thisStuff.keySet().iterator();
        while(values1.hasNext()) {
            Object key1 = values1.next();
            Object value1 = thisStuff.get(key1);
            Object value2 = otherStuff.get(key1);

            if (!(value1.equals(value2))) {
                return false;
            }
        }
        return true;
    }
}
