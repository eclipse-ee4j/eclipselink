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

import org.eclipse.persistence.internal.oxm.Context;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;

import org.w3c.dom.Node;

/**
 * <b>Purpose:</b>Provides an interface for customizing how Documents are
 * preserved.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Access objects from the cache based on node</li>
 * <li>Access nodes from the cache based on Object</li>
 * <li>Add objects and nodes to the cache</li>
 * <li>Allow the configuration of how nodes are added into the preserved doc</li>
 * </ul>
 *
 * @author mmacivor
 * @since Oracle TopLink 11g
 */
public abstract class DocumentPreservationPolicy {
    private NodeOrderingPolicy nodeOrderingPolicy;

    /**
     * PUBLIC:
     * Sets the NodeOrderingPolicy to be used by this DocumentPreservationPolicy
     * when adding new elements into a cached XML Document.
     * @see AppendNewElementsOrderingPolicy
     * @see IgnoreNewElementsOrderingPolicy
     * @see RelativePositionOrderingPolicy
     */
    public void setNodeOrderingPolicy(NodeOrderingPolicy policy) {
        this.nodeOrderingPolicy = policy;
    }
    public NodeOrderingPolicy getNodeOrderingPolicy() {
        return nodeOrderingPolicy;
    }

    public abstract void addObjectToCache(Object obj, Node node);

    /**
     * @since EclipseLink 2.5.0
     */
    public abstract void addObjectToCache(Object obj, Node node, Mapping selfRecordMapping);

    public abstract Node getNodeForObject(Object obj);

    public abstract Object getObjectForNode(Node node);

    public abstract Object getObjectForNode(Node node, Mapping selfRecordMapping);

    public abstract boolean shouldPreserveDocument();

    /**
     * INTERNAL
     */
    public void initialize(Context context) {
    }

}
