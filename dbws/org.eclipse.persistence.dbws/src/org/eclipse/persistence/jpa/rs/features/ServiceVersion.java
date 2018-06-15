/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - initial implementation
package org.eclipse.persistence.jpa.rs.features;

import java.util.HashMap;
import java.util.Map;

/**
 * JPARS service version.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public enum ServiceVersion {
    /**
     * Added only for legacy reasons. In early versions version number was not supported.
     */
    NO_VERSION(null),

    /**
     * The same as NO_VERSION, but v1.0 is specified in the URL.
     */
    VERSION_1_0("v1.0"),

    /**
     * Version 2.0. Supports pagination and other new features.
     */
    VERSION_2_0("v2.0"),

    /**
     * The latest version.
     */
    LATEST("latest");

    /**
     * String representation of the version number. As it appears in the URL. Ex. "v2.0"
     */
    private final String version;

    private static final Map<String, ServiceVersion> values;

    static {
        values = new HashMap<>();
        for (final ServiceVersion e : ServiceVersion.values()) {
            values.put(e.getCode(), e);
        }
    }

    private ServiceVersion(final String version) {
        this.version = version;
    }

    /**
     * Returns the version as in appears in URI.
     *
     * @return the version.
     */
    public String getCode() {
        return version;
    }

    /**
     * Returns enumeration value by version number as it appears in URI.
     *
     * @param version version as it appears in URI.
     * @return ServiceVersion.
     * @throws IllegalArgumentException in case that the passed code does not match any enumeration value.
     */
    public static ServiceVersion fromCode(final String version) throws IllegalArgumentException {
        final ServiceVersion e = values.get(version);
        if (e == null) {
            throw new IllegalArgumentException("Unsupported version " + version);
        }
        return e;
    }

    /**
     * Gets a {@link FeatureSet} related to this service version.
     *
     * @return {@link FeatureSet} related to this version.
     */
    public FeatureSet getFeatureSet() {
        if (this.compareTo(VERSION_2_0) >= 0)
            return new FeatureSetV2();
        else {
            return new FeatureSetPreV2();
        }
    }

    /**
     * Checks if ServiceVersion with given code exists.
     *
     * @param code Code to check.
     * @return true if exists, false if not.
     */
    public static boolean hasCode(String code) {
        return values.containsKey(code);
    }
}
