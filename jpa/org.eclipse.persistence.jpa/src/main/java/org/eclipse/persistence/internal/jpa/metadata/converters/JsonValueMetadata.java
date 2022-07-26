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
//     13/01/2022-4.0.0 Tomas Kraus - 1391: JSON support in JPA
package org.eclipse.persistence.internal.jpa.metadata.converters;

import jakarta.json.JsonValue;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * INTERNAL:
 * This class processes JSON converter.
 */
public class JsonValueMetadata extends MetadataConverter {

    // This matadata class fully qualified name.
    private static final String TYPE_NAME = JsonValueMetadata.class.getName();

    /**
     * INTERNAL:
     * Used for defaulting case.
     */
    public JsonValueMetadata() {
    }

    @Override
    public boolean equals(Object objectToCompare) {
        return super.equals(objectToCompare) && objectToCompare instanceof JsonValueMetadata;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey) {
        if (accessor.getReferenceClass().extendsInterface(JsonValue.class)
                || accessor.getReferenceClass().isArray()
                || accessor.getReferenceClass().isInterface()) {
            if (ConverterManager.getInstance().hasConverter(TYPE_NAME)) {
                setConverter(mapping, ConverterManager.getInstance().createConverter(TYPE_NAME), isForMapKey);
            } else {
                throw new IllegalStateException("Missing JSON type converter on classpath");
            }
        } else {
            // 266912: relax validation for MappedSuperclass descriptors
            if (!accessor.getClassAccessor().isMappedSuperclass()) {
                throw ValidationException.invalidTypeForSerializedAttribute(mapping.getAttributeName(), accessor.getReferenceClass(), accessor.getJavaClass());
            }
        }
    }

}
