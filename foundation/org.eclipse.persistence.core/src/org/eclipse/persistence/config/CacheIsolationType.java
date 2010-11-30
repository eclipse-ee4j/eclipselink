/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Gordon Yorke - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.config;

import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.annotations.PrimaryKey;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Options for how Entity instances should be shared within an EclipseLink Persistence Unit / ServerSession
 * @see ClassDescriptor#setCacheIsolationType(CacheIsolationType)
 * @author Gordon Yorke
 * @since EclipseLink 2.2
 */

public enum CacheIsolationType {
    // These enums are ordered with ascending protective requirements
    
    /**
     * Entity instances will be shared within the second level cache. Any user
     * access to shared cache instances (ie Read-Only) will return an Entity
     * instance that may be shared by multiple clients. 
     * 
     * This setting effectively enables second level caching.
     */
    SHARED,

    /**
     * Entity state information will be cached in the shared cache but Entity
     * instances will not be shared. Any user access to shared cache instances
     * (ie Read-Only) will return a new Entity instance with the cached state.
     * This will ensure the instance is <i>protected</i> from any concurrent
     * state change. To achieve this protection there will be a slight
     * performance impact above what <i>SHARED</i> requires.
     */
    PROTECTED,

    /**
     * The Entity and its data is not stored in the shared cache but is
     * <i>isolated</i> to the Persistence Context/UnitOfWork or
     * IsolatedClientSession. This setting effectively disables second level
     * caching for this entity.
     */
    ISOLATED;
}
