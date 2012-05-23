/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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

/**
 * Used by JPA-RS to build links for relationships.
 * 
 * @author tware
 */
public interface PersistenceWeavedRest {
    List<RelationshipInfo> _persistence_getRelationships();
    void _persistence_setRelationships(List<RelationshipInfo> relationships);
}
