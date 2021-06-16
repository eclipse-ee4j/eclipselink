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
//      tware - initial
package org.eclipse.persistence.internal.jpa.rs.weaving;

import java.util.List;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;

/**
 * Used by JPA-RS to build links for relationships.
 *
 * @author tware
 */
public interface PersistenceWeavedRest {
    List<RelationshipInfo> _persistence_getRelationships();

    void _persistence_setRelationships(List<RelationshipInfo> relationships);

    Link _persistence_getHref();

    void _persistence_setHref(Link href);

    ItemLinks _persistence_getLinks();

    void _persistence_setLinks(ItemLinks links);
}
