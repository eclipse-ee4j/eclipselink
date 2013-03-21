/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - allow for zero ids
 ******************************************************************************/
package org.eclipse.persistence.annotations;

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Configures what type of Id value is used to store the object in the cache.
 * This can either be the basic Id value for simple singleton Ids,
 * or an optimized CacheId type.
 * 
 * @see PrimaryKey#cacheKeyType()
 * @see ClassDescriptor#setCacheKeyType(CacheKeyType)
 * @author James Sutherland
 * @since EclipseLink 2.1
 */
public enum CacheKeyType {
    /**
     * This can only be used for simple singleton Ids, such as long/int/String.
     * This is the default for simple singleton Ids.
     */
    ID_VALUE,

    /**
     * Optimized cache key type that allows composite and complex values.
     * This is the default for composite or complex Ids.
     */
    CACHE_ID,

    /**
     * The cache key type is automatically configured depending on what is optimal for the class.
     */
    AUTO
}
