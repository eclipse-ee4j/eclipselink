/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.internal.weaving;

import java.util.List;

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
}
