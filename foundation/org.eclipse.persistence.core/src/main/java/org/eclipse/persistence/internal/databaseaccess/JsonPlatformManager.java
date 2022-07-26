/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     13/01/2022-4.0.0 Tomas Kraus
//       - 1391: JSON support in JPA
package org.eclipse.persistence.internal.databaseaccess;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import org.eclipse.persistence.internal.databaseaccess.spi.JsonPlatformProvider;

/**
 * Java service manager and service loader for {@link DatabaseJsonPlatform} interface.
 */
public class JsonPlatformManager {

    // Lazy singleton initialization in nested class
    private static final class Instance {
        private static final JsonPlatformManager INSTANCE = new JsonPlatformManager();
    }

    /**
     * Get {@link DatabaseJsonPlatform} implementations manager instance.
     *
     * @return {@link DatabaseJsonPlatform} implementations manager instance
     */
    public static final JsonPlatformManager getInstance() {
        return Instance.INSTANCE;
    }

    // Loaded {@link DatabaseJsonPlatform} instance initializers for registered database platforms
    private final Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> platforms;

    // Initialize singleton instance of {@link DatabaseJsonPlatform} implementations manager.
    // Manager builds DatabasePlatform -> Supplier<DatabaseJsonPlatform>> mapping based
    // on all returned mappings from SPI providers. 1st returned mapping wins for specific
    // DatabasePlatform, but no providers order is specified.
    private JsonPlatformManager() {
        final Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> converters = new HashMap<>();
        final ServiceLoader<JsonPlatformProvider> providers = ServiceLoader.load(JsonPlatformProvider.class);
        for (final JsonPlatformProvider provider : providers) {
            final Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> providerConverters = provider.platforms();
            for (final Class<? extends DatabasePlatform> type : providerConverters.keySet()) {
                if (!converters.containsKey(type)) {
                    converters.put(type, providerConverters.get(type));
                }
            }
        }
        this.platforms = Collections.unmodifiableMap(converters);
    }

    /**
     * Creates {@link DatabaseJsonPlatform} implementation instance registered for provided database platform.
     * Returned instance is specific platform dependent implementation is registered via SPI. Default JSON extension
     * will be returned when specific platform implementation is missing. Empty platform with JSON support disabled
     * is returned when no JSON support is available.
     *
     * @param type database platform used to search for JSON extension
     * @return JSON extension instance mapped to provided database platform
     */
    public DatabaseJsonPlatform createPlatform(final Class<? extends DatabasePlatform> type) {
        // Try database specific JSON platform.
        Supplier<DatabaseJsonPlatform> supplier = platforms.get(type);
        if (supplier != null) {
            return supplier.get();
        }
        // Try default JSON platform.
        supplier = platforms.get(DatabasePlatform.class);
        return supplier != null
                ? supplier.get()
        // Empty platform as a fallback (disable JSON support).
                : new DatabaseJsonPlatform() {};
    }

}
