/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm.documentpreservation;

import org.w3c.dom.Node;

import org.eclipse.persistence.oxm.documentpreservation.NodeOrderingPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>An implementation of NodeOrderingPolicy that adds new elements to an XML Document
 * based on the last updated sibling in their context.
 * <p><b>Responsibilities:</b>Add a new element as a child based on the provided last updated sibling.
 *
 * @see org.eclipse.persistence.oxm.documentpreservation.NodeOrderingPolicy
 * @author mmacivor
 * @author rbarkhouse - changed to subclass (NodeOrderingPolicy changed to abstract superclass)
 */
public class RelativePositionOrderingPolicy extends NodeOrderingPolicy {

    public void appendNode(Node parent, Node newChild, Node previousSibling){
        if (previousSibling != null) {
            Node nextSibling = previousSibling.getNextSibling();
            if (nextSibling != null) {
                parent.insertBefore(newChild, nextSibling);
            } else {
                parent.appendChild(newChild);
            }
        }
        else {
            if (parent.hasChildNodes()) {
                parent.insertBefore(newChild, parent.getFirstChild());
            } else {
                parent.appendChild(newChild);
            }
        }
    }

}
