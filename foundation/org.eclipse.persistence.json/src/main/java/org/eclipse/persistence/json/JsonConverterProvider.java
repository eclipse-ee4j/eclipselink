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
package org.eclipse.persistence.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.spi.ConverterProvider;

/**
 * JSON converter service provider.
 */
public class JsonConverterProvider implements ConverterProvider {

    // Converter metadata class name to converter instance Supplier maping.
    private static final Map<String, Supplier<Converter>> CONVERTERS = initConverters();

    // Initialize metadata class name to converter instance Supplier mapping.
    private static Map<String, Supplier<Converter>> initConverters() {
        final Map<String, Supplier<Converter>> converters = new HashMap<>(2);
        converters.put("org.eclipse.persistence.internal.jpa.metadata.converters.JsonValueMetadata", JsonTypeConverter::new);
        return Collections.unmodifiableMap(converters);
    }

    /**
     * Get {@code Converter} medatada class name to converter instance {@code Supplier} mapping.
     *
     * @return {@code Converter} medatada class name to converter instance {@code Supplier} mapping
     */
    @Override
    public Map<String, Supplier<Converter>> converters() {
        return CONVERTERS;
    }
}
