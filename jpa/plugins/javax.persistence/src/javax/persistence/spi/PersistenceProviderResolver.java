/*******************************************************************************
 * Copyright (c) 2008, 2009 Sun Microsystems. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.0 - Version 2.0 (October 1, 2009)
 *     Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 ******************************************************************************/
package javax.persistence.spi;

import java.util.List;

/**
 * Determine the list of persistence providers available in the 
 * runtime environment.
 * 
 * <p> Implementations must be thread-safe.
 *
 * <p> Note that the <code>getPersistenceProviders</code> method can potentially
 * be called many times: it is recommended that the implementation
 * of this method make use of caching.
 *
 * @see PersistenceProvider
 * @since Java Persistence 2.0
 */
public interface PersistenceProviderResolver {

    /**
     * Returns a list of the <code>PersistenceProvider</code> implementations 
     * available in the runtime environment.
     *
     * @return list of the persistence providers available 
     *         in the environment
     */
    List<PersistenceProvider> getPersistenceProviders();

    /**
     * Clear cache of providers.
     *
     */
    void clearCachedProviders();
} 
