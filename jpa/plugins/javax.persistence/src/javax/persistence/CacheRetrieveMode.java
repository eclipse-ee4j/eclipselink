/*******************************************************************************
 * Copyright (c) 2008 - 2012 Oracle Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.1
 *     Linda DeMichiel - Java Persistence 2.0
 *
 ******************************************************************************/
package javax.persistence;

/**
 * Used as the value of the
 * <code>javax.persistence.cache.retrieveMode</code> property to
 * specify the behavior when data is retrieved by the
 * <code>find</code> methods and by queries.
 *
 * @since Java Persistence 2.0
 */
public enum CacheRetrieveMode {

    /**
     * Read entity data from the cache: this is 
     * the default behavior.
     */
    USE,

    /**
     * Bypass the cache: get data directly from
     * the database.
     */
    BYPASS  
}
