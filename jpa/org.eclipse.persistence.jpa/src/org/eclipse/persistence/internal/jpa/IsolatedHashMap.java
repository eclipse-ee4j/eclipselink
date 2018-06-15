/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/06/2015-2.7 Tomas Kraus
//       - Initial API and implementation.
package org.eclipse.persistence.internal.jpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatformUtils;

/**
 * Partition isolated {@link HashMap}. Provides HashMap with partition isolation for {@link ServerPlatform}s
 * that support partitioning. Partition isolation is transparent and keeps {@link Map} API unchanged.
 */
public final class IsolatedHashMap<K, V> implements Map<K, V> {

    /** Default short enough partition ID when server does not support partitions.*/
    private static final String DEFAULT_PARTITION_ID = "0";

    /** Default initial capacity used to create {@link HashMap}s for individual partitions. */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /** Default load factor used to create {@link HashMap}s for individual partitions. */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /** Detected server platform. */
    private static final ServerPlatform serverPlatform;

    /** Does platform support partitions? */
    private static final boolean supportPartitions;

    /** Class initialization code. */
    static {
        String serverPlatformName = ServerPlatformUtils.detectServerPlatform(null);
        serverPlatform = serverPlatformName != null
                ? ServerPlatformUtils.createServerPlatform(
                        null, serverPlatformName, IsolatedHashMap.class.getClassLoader())
                : null;
        // False value also handles cases when serverPlatform is null to avoid NPE.
        supportPartitions = serverPlatform != null ? serverPlatform.usesPartitions() : false;
    }

    /**
     * INTERNAL:
     * Partition isolated {@link Map} factory. Provides {@link Map} separated for individual partitions.
     * Factory method will return {@link HashMap} on platforms without partitions support. Slower
     * {@link IsolatedHashMap} instance will be used only on platforms with partitions support.
     */
    public static final <K, V>Map<K, V> newMap() {
        return supportPartitions ? new IsolatedHashMap<>() : new HashMap<>();
    }

    /** Initial capacity used to create {@link HashMap}s for individual partitions. */
    private final int initialCapacity;

    /** Initial load factor used to create {@link HashMap}s for individual partitions. */
    private final float loadFactor;

    /** Partition ID to {@link Map} mapping. Used when platform does support partitions. */
    private final Map<String, Map<K, V>> maps;

    /**
     * Constructs an empty {@code IsolatedHashMap} with the default initial capacity {@code 16} and the default
     * load factor {@code 0.75} for every partition.
     */
    private IsolatedHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty {@code IsolatedHashMap} with the initial capacity and the default
     * load factor specified as arguments.
     * @param initialCapacity Initial capacity used to create {@link HashMap}s for individual partitions.
     * @param loadFactor      Initial load factor used used to create {@link HashMap}s for individual partitions.
     */
    private IsolatedHashMap(final int initialCapacity, final float loadFactor) {
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        maps = new ConcurrentHashMap<>(8);
    }

    /**
     * Get {@link Map} for current partition.
     * @return {@link Map} for current partition. Will never return {@code null}.
     */
    private Map<K, V> getMap() {
        String partitionId = supportPartitions ? serverPlatform.getPartitionID() : DEFAULT_PARTITION_ID;
        Map<K, V> partitionMap = maps.get(partitionId);
        // First null check to skip locking when map is already initialized.
        if (partitionMap == null) {
            // FindBugs would be complaining about locking on maps so this is used to shut it up.
            synchronized(this) {
                // Second null check while having lock.
                partitionMap = maps.get(partitionId);
                if (partitionMap == null) {
                    partitionMap = new HashMap<>(initialCapacity, loadFactor);
                    maps.put(partitionId, partitionMap);
                }
            }
        }
        return partitionMap;
    }

    // All Map interface methods are delegated to Map mapped to current partition.
    /** {@inheritDoc} */
    @Override
    public int size() {
        return getMap().size();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return getMap().isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsKey(Object key) {
        return getMap().containsKey(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsValue(Object value) {
        return getMap().containsValue(value);
    }

    /** {@inheritDoc} */
    @Override
    public V get(Object key) {
        return getMap().get(key);
    }

    /** {@inheritDoc} */
    @Override
    public V put(K key, V value) {
        return getMap().put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public V remove(Object key) {
        return getMap().remove(key);
    }

    /** {@inheritDoc} */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        getMap().putAll(m);
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        getMap().clear();
    }

    /** {@inheritDoc} */
    @Override
    public Set<K> keySet() {
        return getMap().keySet();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<V> values() {
        return getMap().values();
    }

    /** {@inheritDoc} */
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return getMap().entrySet();
    }

}
