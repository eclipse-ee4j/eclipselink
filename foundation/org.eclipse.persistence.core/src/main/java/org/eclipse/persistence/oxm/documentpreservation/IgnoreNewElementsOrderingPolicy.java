/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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

/**
 * INTERNAL:
 * <p><b>Purpose:</b>An implementation of NodeOrderingPolicy that ignores any new elements when
 * updating a cached document. This is used for the JAXB 2.0 Binder implementation.
 *
 * @see org.eclipse.persistence.oxm.documentpreservation.NodeOrderingPolicy
 * @author mmacivor
 * @author rbarkhouse - changed to subclass (NodeOrderingPolicy changed to abstract superclass)
 */
public class IgnoreNewElementsOrderingPolicy extends NodeOrderingPolicy {

    @Override
    public void appendNode(Node parent, Node newChild, Node previousSibling) {
        // No-op
    }

}
