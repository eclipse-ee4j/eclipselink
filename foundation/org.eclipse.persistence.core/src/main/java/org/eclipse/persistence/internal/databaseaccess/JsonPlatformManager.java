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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import org.eclipse.persistence.internal.databaseaccess.spi.JsonPlatformProvider;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Java service manager and service loader for {@link DatabaseJsonPlatform} interface.
 */
public class JsonPlatformManager {

    /**
     * Get {@link DatabaseJsonPlatform} implementations manager instance.
     *
     * @return {@link DatabaseJsonPlatform} implementations manager instance
     */
    public static JsonPlatformManager getInstance() {
        return new JsonPlatformManager();
    }

    // Initialize Map of known DatabasePlatform to DatabaseJsonPlatform mappings.
    private static Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> initDirectMap() {
        final Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> fallbackMap = new HashMap<>();
        addProvider(fallbackMap, "org.eclipse.persistence.pgsql.PostgreSQLJsonPlatformProvider");
        addProvider(fallbackMap, "org.eclipse.persistence.platform.database.oracle.json.OracleJsonPlatformProvider");
        addProvider(fallbackMap, "org.eclipse.persistence.json.DefaultJsonPlatformProvider");
        return fallbackMap;
    }

    // Add Map of known JsonPlatformProvider.
    private static void addProvider(
            final Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> fallbackMap, final String className) {
        final Class<JsonPlatformProvider> providerClass = getProvider(className);
        if (providerClass != null) {
            try {
                final JsonPlatformProvider provider  = providerClass.getConstructor().newInstance();
                final Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> platformsMap = provider.platforms();
                if (platformsMap != null) {
                    fallbackMap.putAll(platformsMap);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                AbstractSessionLog.getLog().log(
                        SessionLog.FINE,
                        String.format("Invocation of static platform method on class %s failed: %s", className, ex.getMessage()));
            }
        }
    }

    // Get known JsonPlatformProvider by name.
    @SuppressWarnings("unchecked")
    private static Class<JsonPlatformProvider> getProvider(final String className) {
        try {
            Class<?> candidate = Class.forName(className);
            if (JsonPlatformProvider.class.isAssignableFrom(candidate)) {
                return (Class<JsonPlatformProvider>) candidate;
            } else {
                AbstractSessionLog.getLog().log(
                        SessionLog.FINE,
                        String.format("JsonPlatformProvider candidate class %s does not implement JsonPlatformProvider", className));
            }
        } catch (ClassNotFoundException ex) {
            AbstractSessionLog.getLog().log(
                    SessionLog.FINE,
                    String.format("JsonPlatformProvider class %s was not found", className));
        }
        return null;
    }

    // All {@link DatabaseJsonPlatform} instance initializers for registered database platforms
    private final Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> platforms;

    // Initialize singleton instance of {@link DatabaseJsonPlatform} implementations manager.
    // Manager builds DatabasePlatform -> Supplier<DatabaseJsonPlatform>> mapping based
    // on all returned mappings from SPI providers. 1st returned mapping wins for specific
    // DatabasePlatform, but no providers order is specified.
    private JsonPlatformManager() {
        final Map<Class<? extends DatabasePlatform>, Supplier<DatabaseJsonPlatform>> converters = initDirectMap();
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
        // Try database specific JSON platform lookup.
        Supplier<DatabaseJsonPlatform> supplier = platforms.get(type);
        if (supplier != null) {
            return supplier.get();
        }
        // Try default JSON platform lookup.
        supplier = platforms.get(DatabasePlatform.class);
        return supplier != null
                ? supplier.get()
        // Empty platform as latest possible option (disable JSON support).
                : new DatabaseJsonPlatform() {};
    }

}
