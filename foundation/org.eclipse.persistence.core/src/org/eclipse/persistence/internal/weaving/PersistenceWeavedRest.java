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
//      tware - initial
package org.eclipse.persistence.internal.weaving;

import java.util.List;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;

/**
 * Used by JPA-RS to build links for relationships.
 *
 * @author tware
 */
public interface PersistenceWeavedRest {
    public List<RelationshipInfo> _persistence_getRelationships();

    public void _persistence_setRelationships(List<RelationshipInfo> relationships);

    Link _persistence_getHref();

    void _persistence_setHref(Link href);

    public ItemLinks _persistence_getLinks();

    public void _persistence_setLinks(ItemLinks links);
}
