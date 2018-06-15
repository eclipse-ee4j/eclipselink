/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.documentpreservation;

import java.util.Map;

import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.w3c.dom.Node;

abstract class AbstractDocumentPreservationPolicy extends DocumentPreservationPolicy {

    protected Map nodesToObjects;
    protected Map objectsToNodes;

    public AbstractDocumentPreservationPolicy() {
    }

    @Override
    public void addObjectToCache(Object obj, Node node) {
        addObjectToCache(obj, node, (Mapping) null);
    }

    @Override
    public void addObjectToCache(Object obj, Node node, Mapping selfRecordMapping) {
        objectsToNodes.put(obj, node);
        if(selfRecordMapping != null) {
            XMLBinderCacheEntry entry = (XMLBinderCacheEntry)nodesToObjects.get(node);
            if(entry != null) {
                entry.addSelfMappingObject(selfRecordMapping, obj);
            }
        } else {
            XMLBinderCacheEntry entry = new XMLBinderCacheEntry(obj);
            nodesToObjects.put(node, entry);
        }
    }

    @Override
    public Node getNodeForObject(Object obj) {
        return (Node)objectsToNodes.get(obj);
    }

    @Override
    public Object getObjectForNode(Node node) {
        return getObjectForNode(node, (Mapping) null);
    }

    @Override
    public Object getObjectForNode(Node node, Mapping selfRecordMapping) {
        XMLBinderCacheEntry entry = (XMLBinderCacheEntry)nodesToObjects.get(node);
        if(entry != null) {
            if(selfRecordMapping != null) {
                return entry.getSelfMappingObject(selfRecordMapping);
            } else {
                return entry.getRootObject();
            }
        }
        return null;
    }

    @Override
    public boolean shouldPreserveDocument() {
        return true;
    }

}
