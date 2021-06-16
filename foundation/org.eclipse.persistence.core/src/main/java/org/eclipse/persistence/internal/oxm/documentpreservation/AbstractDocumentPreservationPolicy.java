/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
        addObjectToCache(obj, node, null);
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
        return getObjectForNode(node, null);
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
