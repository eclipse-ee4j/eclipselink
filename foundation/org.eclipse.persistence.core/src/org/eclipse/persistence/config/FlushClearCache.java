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
 
package org.eclipse.persistence.config;

/**
 * FlushClearCache persistence property
 * defines modes of cache handling after em.flush call followed by em.clear call.
 * This property could be specified while creating either EntityManagerFactory 
 * (createEntityManagerFactory or persistence.xml)
 * or EntityManager (createEntityManager); the latter overrides the former.
 * 
 * <p>JPA persistence property Usage:
 * 
 * <p><code>properties.add(PersistenceUnitProperties.FLUSH_CLEAR_CACHE, FlushClearCache.Drop);</code>
 * 
 * <p>Values are case-insensitive.
 * "" could be used instead of default value FlushClearCache.DEFAULT.
 */
public class FlushClearCache {
    /** Call to clear method causes to drop from EntityManager cache only the objects that haven't been flushed.
     * This is the most accurate mode: shared cache is perfect after commit; 
     * but the least memory effective: smbd repeatedly using flush followed by clear
     * may eventually run out of memory in a huge transaction.
     */
    public static final String  Merge = "Merge";
    
    /** Call to clear method causes to drop the whole EntityManager cache.
     * This is the fasteset and using the least memory mode - 
     * but after commit the shared cache potentially contains stale data.
     */
    public static final String  Drop = "Drop";

    /** Call to clear method causes to drops the whole EntityManager cache, 
     * on commit the classes that have at least one object updated or deleted 
     * are invalidated in the shared cache.
     * This is a compromise mode: potentially a bit slower than drop,
     * but has virtually the same memory efficiency.
     * After commit all potentially stale data is invalidated in the shared cache.
     */
    public static final String  DropInvalidate = "DropInvalidate";
 
    public static final String DEFAULT = DropInvalidate;
}
