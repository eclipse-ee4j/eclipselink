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
// Matt MacIvor - 2.3.1
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
