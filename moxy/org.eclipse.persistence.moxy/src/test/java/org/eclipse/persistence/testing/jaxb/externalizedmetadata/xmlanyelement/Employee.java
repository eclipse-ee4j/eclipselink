/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - October 27/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

@jakarta.xml.bind.annotation.XmlRootElement
public class Employee {
    public int a;
    public String b;

    //@jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(value=org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.MyDomAdapter.class)
    //@jakarta.xml.bind.annotation.XmlAnyElement(lax=false, value=org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.MyDomHandler.class)
    public Object stuff;

    public Employee() {}

    public boolean equals(Object obj) {
        Employee empObj;
        try {
            empObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }

        if (empObj.stuff == null) {
            if (this.stuff != null) {
                return false;
            }
            return empObj.a == this.a && empObj.b.equals(this.b);
        }
        if (this.stuff == null) {
            return false;
        }

        // if 'stuff' is an Element, don't bother with comparison
        if (empObj.stuff instanceof Node) {
            return empObj.a == this.a && empObj.b.equals(this.b) && this.stuff instanceof Node;
        }
        // here 'stuff' should be text or an Employee
        return empObj.a == this.a && empObj.b.equals(this.b) && empObj.stuff.equals(this.stuff);
    }

    public String toString() {
        return "Employee[a="+a+", b="+b+", stuff="+stuff+"]";
    }
}
