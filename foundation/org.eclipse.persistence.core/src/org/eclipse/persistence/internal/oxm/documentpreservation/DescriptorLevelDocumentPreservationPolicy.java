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
package org.eclipse.persistence.internal.oxm.documentpreservation;

import java.util.WeakHashMap;

import org.eclipse.persistence.oxm.documentpreservation.AppendNewElementsOrderingPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of DocumentPreservation Policy that accesses the
 * session cache to store Objects and their associated nodes.
 * <p><b>Responsibilities:</b><ul>
 * <li>Add new objects and their associated nodes into the Session cache, wrapping them in a weak
 * reference.
 * <li>Lookup the node for a given Object
 *
 * @author mmacivor
 * @since TopLink 11g
 */

public class DescriptorLevelDocumentPreservationPolicy extends AbstractDocumentPreservationPolicy {

    public DescriptorLevelDocumentPreservationPolicy() {
        super();
        nodesToObjects = new WeakHashMap();
        objectsToNodes = new WeakHashMap();
        this.setNodeOrderingPolicy(new AppendNewElementsOrderingPolicy());
    }

}
