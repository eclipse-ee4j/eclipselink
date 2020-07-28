/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     11/05/2014-2.6 Tomas Kraus
//       - 449818: Initial API and implementation.
package org.eclipse.persistence.internal.mappings.converters;

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL:
 * Attribute name prefixes.
 * @author Tomas Kraus
 */
public enum AttributeNamePrefix {

    /** INTERNAL: No attribute name prefix. */
    NULL(""),

    /** INTERNAL: Attribute name prefix is {@code "key"}. */
    KEY("key"),

    /** INTERNAL: Attribute name prefix is {@code "value"}. */
    VALUE("value");

    /** INTERNAL: Attribute name prefixes enumeration length. */
    public static final int LENGTH = AttributeNamePrefix.values().length;

    /** Stored {@code String} values for backward {@code String} conversion. */
    private static final Map<String, AttributeNamePrefix> stringValuesMap
            = new HashMap<String, AttributeNamePrefix>(LENGTH);

    // Initialize backward String conversion Map.
    static {
        for (AttributeNamePrefix prefix : AttributeNamePrefix.values()) {
            stringValuesMap.put(prefix.getName(), prefix);
        }
    }

    /**
     * INTERNAL:
     * Returns an {@code AttributeNamePrefix} instance with {@code name} equal to the specified {@code String}.
     * The {@code AttributeNamePrefix} returned represents existing value only if specified {@code String} matches
     * any {@code String} returned by {@code getName} method. Search for {@code name} is case sensitive. Otherwise
     * {@code null} value is returned.
     * @param name {@code getName()} value to search for corresponding {@code AttributeNamePrefix} instance.
     * @return {@code AttributeNamePrefix} value represented by {@code String} or {@code null} if value was
     *         not recognized.
     */
    public static AttributeNamePrefix toValue(final String name) {
        if (name != null) {
            return (stringValuesMap.get(name));
        } else {
            return null;
        }
    }

    /** Attribute name prefix identifier. */
    private final String name;

    /**
     * Creates an instance of attribute name prefix enumeration element.
     * @param name Attribute name prefix identifier.
     */
    private AttributeNamePrefix(final String name) {
        this.name = name;
    }

    /**
     * INTERNAL:
     * Get attribute name prefix identifier.
     * @return Attribute name prefix identifier.
     */
    public String getName() {
        return name;
    }

    /**
     * INTERNAL:
     * Get instance value in human readable form.
     * @return Instance value in human readable form.
     */
    public String toString() {
        return name;
    }

}
