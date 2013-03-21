/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.internal.identitymaps.CacheKey;

/**
 * Define an interface for utility methods weaved into the persistence classes.
 * These general methods are used to optimize performance, such as caching the key in the object.
 * It can be disabled through the "weaving.internal" option.
 * 
 * @author  mmacivor
 * @since   10.1.3
 */
public interface PersistenceEntity {    
    Object _persistence_getId();
    void _persistence_setId(Object primaryKey);    
    CacheKey _persistence_getCacheKey();
    void _persistence_setCacheKey(CacheKey cacheKey);
}
