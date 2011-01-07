/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - October 27/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

@javax.xml.bind.annotation.XmlRootElement
public class Employee {
    public int a;
    public String b;
    
    //@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(value=org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.MyDomAdapter.class)
    //@javax.xml.bind.annotation.XmlAnyElement(lax=false, value=org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.MyDomHandler.class)
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
