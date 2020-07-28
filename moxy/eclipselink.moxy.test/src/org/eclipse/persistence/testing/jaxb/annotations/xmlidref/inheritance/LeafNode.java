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
import java.util.Objects;

@XmlRootElement(name="LeafNode")
@XmlAccessorType(XmlAccessType.FIELD)
public class LeafNode extends Node {
	
	@XmlAttribute
	private boolean isValid;
	
	@XmlIDREF
	@XmlElements({
		@XmlElement(name="LNComplexNodeID", type=ComplexNode.class),
		@XmlElement(name="LNLeafNodeID", type=LeafNode.class),
	})
	private Node targetNode;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LeafNode leafNode = (LeafNode) o;
        return isValid() == leafNode.isValid() &&
                Objects.equals(getId(), leafNode.getId()) &&
                Objects.equals(getTargetNode(), leafNode.getTargetNode());
    }

}
