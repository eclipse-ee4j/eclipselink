/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Node;

@XmlRootElement(name="root", namespace="")
public class DefaultNamespaceRoot {

    @XmlAnyElement(lax=true)
    public Object any;

    @Override
    public boolean equals(Object obj)  {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        DefaultNamespaceRoot test = (DefaultNamespaceRoot) obj;

        Node controlDOM = (Node) any;
        Node testDOM = (Node) test.any;

        XMLComparer comparer = new XMLComparer();
        return comparer.isNodeEqual(controlDOM, testDOM);
    }

}
