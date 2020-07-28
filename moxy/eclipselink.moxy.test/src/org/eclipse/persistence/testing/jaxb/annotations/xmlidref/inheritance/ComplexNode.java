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

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name="ComplexNode")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComplexNode extends Node {
	
	@XmlElementWrapper(name="ownedNodes")
	@XmlElements({
		@XmlElement(name="ComplexNode", type=ComplexNode.class),
		@XmlElement(name="LeafNode", type=LeafNode.class),
	})
	private List<Node> ownedNodes = new ArrayList<Node>();


    public List<Node> getOwnedNodes() {
        return ownedNodes;
    }

    public void setOwnedNodes(List<Node> ownedNodes) {
        this.ownedNodes = ownedNodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ComplexNode that = (ComplexNode) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getOwnedNodes(), that.getOwnedNodes());
    }

}
