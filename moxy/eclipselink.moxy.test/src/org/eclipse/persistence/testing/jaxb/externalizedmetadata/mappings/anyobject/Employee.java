/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyobject;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Employee {
    public Object stuff;

    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public Object getStuff() {
        wasGetCalled = true;
        return stuff;
    }

    public void setStuff(Object stuff) {
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
        
        if (stuff == null) {
            if (empObj.stuff != null) {
                return false;
            }
        } else {
            if (empObj.stuff == null) {
                return false;
            }
            // 'stuff' should be an Element
            if (this.stuff instanceof Node) {
                if (!(empObj.stuff instanceof Node)) {
                    return false;
                }
                // just make sure each has the same number of child nodes 
                try {
                    Element elt = (Element) this.stuff;
                    Element empelt = (Element) empObj.stuff;
                    if (elt.getChildNodes().getLength() != empelt.getChildNodes().getLength()) {
                        return false;
                    }
                } catch (Exception x) {
                    return false;
                }
            }
        }

        return true;
    }
}
