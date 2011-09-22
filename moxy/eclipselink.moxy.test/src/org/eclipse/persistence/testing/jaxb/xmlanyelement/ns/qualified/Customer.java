/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - 2.3.1
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Node;

@XmlRootElement
public class Customer {
    @XmlAnyElement
    public Object anyElem;
    
    public boolean equals(Object obj) {
        if(!(obj instanceof Customer)) {
            return false;
        }
        Node node1 = (Node)anyElem;
        Node node2 = (Node)((Customer)obj).anyElem;
        
        return node1.getNamespaceURI().equals(node2.getNamespaceURI()) && node1.getLocalName().equals(node2.getLocalName());
    }
}
