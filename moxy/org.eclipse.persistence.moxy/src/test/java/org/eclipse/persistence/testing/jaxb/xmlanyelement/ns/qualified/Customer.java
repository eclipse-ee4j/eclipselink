/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// Matt MacIvor - 2.3.1
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
