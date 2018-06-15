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
// Matt MacIvor - 2.3.1
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
