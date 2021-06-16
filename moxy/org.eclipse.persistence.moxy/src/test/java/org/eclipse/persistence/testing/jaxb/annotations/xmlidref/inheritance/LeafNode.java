/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref.inheritance;

import jakarta.xml.bind.annotation.*;
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
