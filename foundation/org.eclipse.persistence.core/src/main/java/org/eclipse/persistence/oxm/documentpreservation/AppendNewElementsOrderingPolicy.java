/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm.documentpreservation;

import org.w3c.dom.Node;

import org.eclipse.persistence.oxm.documentpreservation.NodeOrderingPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of NodeOrderingPolicy that simply appends the new child
 * element to the parent. This is the default for DescriptorLevelDocumentPreservationPolicy and
 * NoDocumentPreservationPolicy
 *
 * @see org.eclipse.persistence.oxm.documentpreservation.NodeOrderingPolicy
 * @author mmacivor
 * @author rbarkhouse - changed to subclass (NodeOrderingPolicy changed to abstract superclass)
 */
public class AppendNewElementsOrderingPolicy extends NodeOrderingPolicy {

    @Override
    public void appendNode(Node parent, Node newChild, Node previousSibling) {
        parent.appendChild(newChild);
    }

}
