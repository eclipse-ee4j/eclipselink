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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.util.UUID;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;

/**
 * INTERNAL:
 * Abstract converter class that parents both the JPA and Eclipselink
 * converters.
 * <p>
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class UUIDMetadata extends MetadataConverter {

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public UUIDMetadata() {
        super("<uuid>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public UUIDMetadata(MetadataAnnotation uuid, MetadataAccessor accessor) {
        super(uuid, accessor);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return super.equals(objectToCompare) && objectToCompare instanceof UUIDMetadata;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * INTERNAL:
     * Returns true if the given class is a valid UUID type and must be
     * marked UUID.
     */
    public static boolean isValidUUIDType(MetadataClass cls) {
        return cls.isClass(java.util.UUID.class);
    }

    /**
     * INTERNAL:
     * Every converter needs to be able to process themselves.
     */
    @Override
    public void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey) {
        if (isValidUUIDType(referenceClass)) {
            setFieldClassification(mapping, java.util.UUID.class, isForMapKey);
            TypeConversionConverter converter = new TypeConversionConverter(mapping);
            converter.setObjectClass(UUID.class);
            converter.setDataClass(byte[].class);
            setConverter(mapping, converter, isForMapKey);
        } else {
            throw ValidationException.invalidTypeForSerializedAttribute(mapping.getAttributeName(), accessor.getReferenceClass(), accessor.getJavaClass());
        }
    }
}
