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
 * dmccann - February 16/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Node;

@javax.xml.bind.annotation.XmlRootElement
public class EmployeeWithList {
    public int a;
    public String b;
    
    //@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(value=org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.MyDomAdapter.class)
    //@javax.xml.bind.annotation.XmlAnyElement(lax=false, value=org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.MyDomHandler.class)
    public java.util.List<Object> stuff;

    public EmployeeWithList() {}
    
    public boolean equals(Object obj) {
        EmployeeWithList empObj;
        try {
            empObj = (EmployeeWithList) obj;
        } catch (ClassCastException e) { return false; }
        
        if (empObj.stuff.size() != this.stuff.size()) { return false; }
        // assumes size of list is 2, and order is not relevant
		XMLComparer comparer = new XMLComparer();

        for (int i=0; i<2; i++) {
            Object stuffStr = empObj.stuff.get(i);
            if(stuff.get(i) instanceof Node){
            	if(!(stuffStr instanceof Node)){
            		return false;
            	}
            	if(!comparer.isNodeEqual((Node)stuff.get(i), (Node)stuffStr)){
            		return false;
            	}
            }else if (!(stuff.get(0).equals(stuffStr) || stuff.get(1).equals(stuffStr))) {
                return false;
            }
        }
        return empObj.a == this.a && empObj.b.equals(this.b);
    }
}
