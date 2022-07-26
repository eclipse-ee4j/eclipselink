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
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.spi.ConverterProvider;

/**
 * Manage field value to JDBC data type converters loaded from SPI providers.
 */
public class ConverterManager {

    // Lazy singleton initialization in nested class
    private static final class Instance {
        private static final ConverterManager INSTANCE = new ConverterManager();
    }

    /**
     * Get singleton instance of converters manager.
     *
     * @return singleton instance of converters manager
     */
    public static final ConverterManager getInstance() {
        return Instance.INSTANCE;
    }

    // Converter metadata class name to converter instance Supplier mapping.
    private final Map<String, Supplier<Converter>> converters;

    // Creates singleton instance of converters manager.
    // Loads SPI service ConverterProvider implementations and builds converter metadata class name
    // to converter instance Supplier mapping.
    private ConverterManager() {
        final Map<String, Supplier<Converter>> converters = new HashMap<>();
        final ServiceLoader<ConverterProvider> providers = ServiceLoader.load(ConverterProvider.class);
        for (final ConverterProvider provider : providers) {
            final Map<String, Supplier<Converter>> providerConverters = provider.converters();
            for (final String type : providerConverters.keySet()) {
                if (!converters.containsKey(type)) {
                    converters.put(type, providerConverters.get(type));
                }
            }
        }
        this.converters = Collections.unmodifiableMap(converters);
    }

    /**
     * Check whether converter instance {@code Supplier} exists for provided {@code Converter} metadata
     * class name.
     *
     * @param type {@code Converter} metadata fully qualified class name
     * @return value of {@code true} when there exists mapping for provided class name
     *         or {@code false} otherwise
     */
    public boolean hasConverter(final String type) {
        return converters.containsKey(type);
    }

    /**
     * Create an instance of field value to JDBC data type converter mapped to provided {@code Converter}
     * metadata class name.
     *
     * @param type {@code Converter} metadata fully qualified class name
     * @return new instance of field value to JDBC data type converter
     */
    public Converter createConverter(final String type) {
        return converters.get(type).get();
    }

}
