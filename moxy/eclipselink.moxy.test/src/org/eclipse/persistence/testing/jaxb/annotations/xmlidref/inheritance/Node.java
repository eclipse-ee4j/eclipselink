/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref.inheritance;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlPaths;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "Node")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Node {

    @XmlID
    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute
    private String name;

    @XmlIDREF
    @XmlPaths({
            @XmlPath("refNodes/ComplexNodeID/text()"),
            @XmlPath("refNodes/LeafNodeID/text()"),
    })
    @XmlElements({
            @XmlElement(name = "ComplexNodeID", type = ComplexNode.class),
            @XmlElement(name = "LeafNodeID", type = LeafNode.class),
    })
    private List<Node> refNodes = new ArrayList<Node>();


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getRefNodes() {
        return refNodes;
    }

    public void setRefNodes(List<Node> refNodes) {
        this.refNodes = refNodes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        if (getRefNodes() == null && node.getRefNodes() != null) return false;
        if (node.getRefNodes() == null && getRefNodes() != null) return false;
        if (getRefNodes() != null || node.getRefNodes() != null) {
            if (getRefNodes().size() != node.getRefNodes().size()) return false;
            Iterator<Node> iterator1 = getRefNodes().iterator();
            Iterator<Node> iterator2 = node.getRefNodes().iterator();
            //Compare just IDs to avoid circular references
            while (iterator1.hasNext()) {
                if (!iterator1.next().getId().equals(iterator2.next().getId())) return false;
            }
        }
        return Objects.equals(getId(), node.getId()) &&
                Objects.equals(getName(), node.getName());
    }

}
