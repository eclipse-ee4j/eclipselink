/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.metadata;

import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.cache.CacheIndexMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitDefaults;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitMetadata;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ValueBasedMetadataTest {

    @Test
    void cacheIndexComparesNullableBooleanValues() {
        for (Boolean left : values()) {
            for (Boolean right : values()) {
                CacheIndexMetadata first = new CacheIndexMetadata();
                CacheIndexMetadata second = new CacheIndexMetadata();
                first.setColumnNames(List.of("ID"));
                second.setColumnNames(List.of("ID"));
                first.setUpdateable(left);
                second.setUpdateable(right);
                assertEquals(sameValue(left, right), first.equals(second));
                assertEquals(sameValue(left, right), second.equals(first));
                second.setColumnNames(List.of("OTHER"));
                assertFalse(first.equals(second));
            }
        }
    }

    @Test
    void persistenceUnitComparesBothNullableBooleanValues() {
        for (Boolean complete : values()) {
            for (Boolean otherComplete : values()) {
                for (Boolean exclude : values()) {
                    for (Boolean otherExclude : values()) {
                        XMLPersistenceUnitMetadata first = metadata(complete, exclude);
                        XMLPersistenceUnitMetadata second = metadata(otherComplete, otherExclude);
                        boolean equal = sameValue(complete, otherComplete) && sameValue(exclude, otherExclude);
                        assertEquals(equal, first.equals(second));
                        assertEquals(equal, second.equals(first));
                    }
                }
            }
        }
    }

    @Test
    void persistenceUnitStillRequiresMatchingDefaults() {
        XMLPersistenceUnitMetadata first = metadata(null, null);
        XMLPersistenceUnitMetadata second = metadata(null, null);
        second.getPersistenceUnitDefaults().setSchema("different");
        assertFalse(first.equals(second));
        second.setPersistenceUnitDefaults(null);
        assertFalse(first.equals(second));
        first.setPersistenceUnitDefaults(null);
        assertFalse(first.equals(second));
    }

    private static XMLPersistenceUnitMetadata metadata(Boolean complete, Boolean exclude) {
        XMLPersistenceUnitMetadata metadata = new XMLPersistenceUnitMetadata();
        metadata.setXMLMappingMetadataComplete(complete);
        metadata.setExcludeDefaultMappings(exclude);
        metadata.setPersistenceUnitDefaults(new XMLPersistenceUnitDefaults());
        return metadata;
    }

    private static boolean sameValue(Boolean left, Boolean right) {
        return left == null ? right == null : right != null && left.booleanValue() == right.booleanValue();
    }

    @SuppressWarnings("removal")
    private static Boolean[] values() {
        // Deliberately bypass Boolean singletons to expose identity-based comparisons.
        Boolean distinctTrue = new Boolean(true);
        Boolean distinctFalse = new Boolean(false);
        assertNotSame(Boolean.TRUE, distinctTrue);
        assertNotSame(Boolean.FALSE, distinctFalse);
        return new Boolean[] {null, Boolean.TRUE, Boolean.FALSE, distinctTrue, distinctFalse};
    }
}
