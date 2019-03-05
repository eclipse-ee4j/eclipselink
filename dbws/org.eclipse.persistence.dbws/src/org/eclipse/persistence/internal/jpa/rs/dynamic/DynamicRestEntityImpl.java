/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.rs.dynamic;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.jpa.rs.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.internal.weaving.RelationshipInfo;

/**
 * <b>INTERNAL:</b> DynamicRestEntityImpl is used to model weaved entity for JPA-RS
 *
 * @since EclipseLink 3.0
 */
public abstract class DynamicRestEntityImpl extends DynamicEntityImpl implements PersistenceWeavedRest {

    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.jpa.rs.weaving.PersistenceWeavedRest#_persistence_getRelationships()
     */
    @Override
    public List<RelationshipInfo> _persistence_getRelationships() {
        List<RelationshipInfo> relationships = (List<RelationshipInfo>) get("_persistence_relationshipInfo");
        if (relationships == null) {
            relationships = new ArrayList<RelationshipInfo>();
            _persistence_setRelationships(relationships);
        }
        return relationships;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.jpa.rs.weaving.PersistenceWeavedRest#_persistence_setRelationships(java.util.List)
     */
    @Override
    public void _persistence_setRelationships(List<RelationshipInfo> relationships) {
        set("_persistence_relationshipInfo", relationships, false);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.jpa.rs.weaving.PersistenceWeavedRest#getPersistence_href()
     */
    @Override
    public Link _persistence_getHref() {
        return get("_persistence_href");
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.jpa.rs.weaving.PersistenceWeavedRest#_persistence_setHref(org.eclipse.persistence.internal.jpa.rs.metadata.model.Link)
     */
    @Override
    public void _persistence_setHref(Link href) {
        set("_persistence_href", href, false);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.jpa.rs.weaving.PersistenceWeavedRest#_persistence_getLinks()
     */
    @Override
    public ItemLinks _persistence_getLinks() {
        return get("_persistence_links");
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.jpa.rs.weaving.PersistenceWeavedRest#_persistence_setLinks(org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks)
     */
    @Override
    public void _persistence_setLinks(ItemLinks links) {
        set("_persistence_links", links, false);
    }


}
