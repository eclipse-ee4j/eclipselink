/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * <p><b>Purpose:</b>Provide a policy that specifies how new XML Elements are added to an
 * existing XML Document.
 *
 * @see org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy
 * @author mmacivor
 * @author rbarkhouse - changed to abstract superclass
 */
public abstract class NodeOrderingPolicy {

    /**
     * Default constructor.
     */
    protected NodeOrderingPolicy() {
    }

    public abstract void appendNode(Node parent, Node newChild, Node previousSibling);

}
