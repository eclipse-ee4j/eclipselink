/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.internal.oxm;

/**
 * Handles Json type configuration.
 *
 * @author Martin Vojtek
 *
 */
public class JsonTypeConfiguration {

    /**
     * If there should be xsd prefix when using simple types, e.g. xsd.int.
     */
    private boolean useXsdTypesWithPrefix = false;

    /**
     * Allows system property override with context property.
     */
    private boolean useXsdTypesWithPrefixSet = false;

    /**
     * If we should treat unqualified type property in JSON as MOXy type discriminator.
     */
    private boolean jsonTypeCompatibility = false;

    /**
     * Allows system property override with context property.
     */
    private boolean jsonTypeCompatibilitySet = false;

    /**
     * Getter for useXsdTypesWithPrefix property.
     *
     * @return value of useXsdTypesWithPrefix property
     * @since 2.6.0
     */
    public boolean isUseXsdTypesWithPrefix() {
        return useXsdTypesWithPrefix;
    }

    /**
     * If there should be xsd prefix when using simple types, e.g. xsd.int.
     *
     * @return true if there should be xsd prefix when using simple types, e.g. xsd.int.
     * @since 2.6.0
     */
    public boolean useXsdTypesWithPrefix() {
        if (useXsdTypesWithPrefixSet) {
            return useXsdTypesWithPrefix;
        } else {
            return OXMSystemProperties.jsonUseXsdTypesPrefix;
        }
    }

    /**
     * If there should be xsd prefix when using simple types, e.g. xsd.int.
     *
     * @param useXsdTypesWithPrefix If there should be xsd prefix when using simple types, e.g. xsd.int.
     * @since 2.6.0
     */
    public void setUseXsdTypesWithPrefix(boolean useXsdTypesWithPrefix) {
        this.useXsdTypesWithPrefix = useXsdTypesWithPrefix;
        this.useXsdTypesWithPrefixSet = true;
    }

    /**
     * Getter for jsonTypeCompatibility
     *
     * @return value of jsonTypeCompatibility property
     * @since 2.6.0
     */
    public boolean isJsonTypeCompatibility() {
        return jsonTypeCompatibility;
    }

    /**
     * If we should treat unqualified type property in JSON as MOXy type discriminator.
     *
     * @return true if we should treat unqualified type property in JSON as MOXy type discriminator.
     * @since 2.6.0
     */
    public boolean useJsonTypeCompatibility() {
        if (jsonTypeCompatibilitySet) {
            return jsonTypeCompatibility;
        } else {
            return OXMSystemProperties.jsonTypeCompatiblity;
        }
    }

    /**
     * If we should treat unqualified type property in JSON as MOXy type discriminator.
     *
     * @param jsonTypeCompatibility If we should treat unqualified type property in JSON as MOXy type discriminator.
     * @since 2.6.0
     */
    public void setJsonTypeCompatibility(boolean jsonTypeCompatibility) {
        this.jsonTypeCompatibility = jsonTypeCompatibility;
        this.jsonTypeCompatibilitySet = true;
    }

}
