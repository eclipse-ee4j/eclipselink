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
//     Denise Smith - September 2013
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns2;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Element;

@XmlRootElement(namespace = "namespace")
public class Root {

    private Element child;

    @XmlAnyElement
    public Element getChild() {
        return child;
    }

    public void setChild(Element child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != Root.class) {
            return false;
        }
        XMLComparer comp = new XMLComparer();
        return comp.isNodeEqual(child, ((Root)obj).child);
    }

    @Override
    public int hashCode() {
        int result = child == null ? 0 : child.getTagName() == null ? 0 : child.getTagName().hashCode();
        if (child != null && child.getNamespaceURI() != null) {
            result = result * 31 + child.getNamespaceURI().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return "Root{" +
                "child=" + (child == null ? "null_child_value" : (child.getNamespaceURI() + child.getTagName())) +
                '}';
    }
}
