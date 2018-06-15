/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     11/06/2014-2.6 Tomas Kraus
//       - 449818: Initial API and implementation.
package org.eclipse.persistence.testing.tests.junit.mapping.conversion;

import org.eclipse.persistence.internal.mappings.converters.AttributeNamePrefix;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test attribute name prefix enumeration. This test case is registered
 * in {@link org.eclipse.persistence.testing.tests.mapping.MappingTestModel} test model.
 * @author Tomas Kraus
 */
public class AttributeNamePrefixTest {

    /** All valid attribute name prefixes names. */
    private static final String[] NAMES = new String[AttributeNamePrefix.LENGTH];

    // All valid attribute name prefixes names array initialization.
    static {
        int i = 0;
        for (AttributeNamePrefix value : AttributeNamePrefix.values()) {
            NAMES[i++] = value.getName();
        }
    }

    /**
     * Verify {@code String} to AttributeNamePrefix conversion.
     */
    @Test
    public void testToValue() {
        final String[] wrongValuesFirstLetter = new String[AttributeNamePrefix.LENGTH];
        final String[] wrongValuesUpperCase = new String[AttributeNamePrefix.LENGTH];
       for (int i = 0; i < AttributeNamePrefix.LENGTH; i++) {
            final int length = NAMES[i].length();
            final StringBuilder sbFl = new StringBuilder(NAMES.length);
            if (length > 0) {
                sbFl.append(Character.toUpperCase(NAMES[i].charAt(0)));
                if (length > 1) {
                    sbFl.append(NAMES[i].substring(1));
                }
            }
            wrongValuesFirstLetter[i] = sbFl.toString();
            wrongValuesUpperCase[i] = NAMES[i].toUpperCase();
        }
        // Verify valid attribute name prefixes names.
        for (int i = 0; i < AttributeNamePrefix.LENGTH; i++) {
            AttributeNamePrefix prefix = AttributeNamePrefix.toValue(NAMES[i]);
            Assert.assertNotNull("No attribute prefix enumeration value was found.", prefix);
        }
        // Verify invalid attribute name prefixes names (1st letter of valid name is capital).
        for (int i = 0; i < AttributeNamePrefix.LENGTH; i++) {
            AttributeNamePrefix prefix = AttributeNamePrefix.toValue(wrongValuesFirstLetter[i]);
            if (i == 0) {
                Assert.assertNotNull("No attribute prefix enumeration value was found for NULL name.", prefix);
            } else {
                Assert.assertNull("Attribute prefix enumeration value was found for invalid name.", prefix);
            }
        }
        // Verify invalid attribute name prefixes names (all letters are upper case).
        for (int i = 0; i < AttributeNamePrefix.LENGTH; i++) {
            AttributeNamePrefix prefix = AttributeNamePrefix.toValue(wrongValuesFirstLetter[i]);
            if (i == 0) {
                Assert.assertNotNull("No attribute prefix enumeration value was found for NULL name.", prefix);
            } else {
                Assert.assertNull("Attribute prefix enumeration value was found for invalid name.", prefix);
            }
        }
    }

}
