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
// dmccann - March 25/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute;

import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.Root;

public class Employee {
    public Map<QName, String> stuff;

    @jakarta.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @jakarta.xml.bind.annotation.XmlTransient
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
