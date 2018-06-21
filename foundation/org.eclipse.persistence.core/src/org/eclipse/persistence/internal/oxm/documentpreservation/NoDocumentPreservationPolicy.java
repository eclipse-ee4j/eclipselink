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

import org.w3c.dom.Node;

import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.oxm.documentpreservation.AppendNewElementsOrderingPolicy;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide a DocumentPreservationPolicy to indicate that no document preservation
 * work should be done
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement all abstract methods from DocumentPreservationPolicy to do nothing.
 * <li>Always use appendNodeOrdering policy
 * </ul>
 *
 * @since Oracle TopLink 11g
 * @author mmacivor
 *
 */
public class NoDocumentPreservationPolicy extends DocumentPreservationPolicy {

    public NoDocumentPreservationPolicy() {
        setNodeOrderingPolicy(new AppendNewElementsOrderingPolicy());
    }

    @Override
    public void addObjectToCache(Object obj, Node node) {
        //No op
    }

    @Override
    public void addObjectToCache(Object obj, Node node, Mapping selfRecordMapping) {
    }

    @Override
    public Node getNodeForObject(Object obj) {
        return null;
    }

    @Override
    public Object getObjectForNode(Node node) {
        return getObjectForNode(node, (Mapping) null);
    }

    @Override
    public Object getObjectForNode(Node node, Mapping selfRecordNodeValue) {
        return null;
    }

    @Override
    public boolean shouldPreserveDocument() {
        return false;
    }
}
