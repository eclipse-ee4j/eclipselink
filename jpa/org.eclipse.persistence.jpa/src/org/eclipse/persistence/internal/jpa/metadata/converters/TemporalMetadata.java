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
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_TEMPORAL_DATE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_TEMPORAL_TIME;

/**
 * INTERNAL:
 * Abstract converter class that parents both the JPA and Eclipselink
 * converters.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public class TemporalMetadata extends MetadataConverter {
    private String m_temporalType;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public TemporalMetadata() {
        super("<temporal>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public TemporalMetadata(MetadataAnnotation temporal, MetadataAccessor accessor) {
        super(temporal, accessor);

        m_temporalType = temporal.getAttributeString("value");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof TemporalMetadata) {
            TemporalMetadata enumerated = (TemporalMetadata) objectToCompare;
            return valuesMatch(m_temporalType, enumerated.getTemporalType());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return m_temporalType != null ? m_temporalType.hashCode() : 0;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getTemporalType() {
        return m_temporalType;
    }

    /**
     * INTERNAL:
     * Returns true if the given class is a valid temporal type and must be
     * marked temporal.
     */
    public static boolean isValidTemporalType(MetadataClass cls) {
        return (cls.equals(java.util.Date.class) ||
                cls.equals(java.util.Calendar.class) ||
                cls.equals(java.util.GregorianCalendar.class));
    }

    /**
     * INTERNAL:
     * Every converter needs to be able to process themselves.
     */
    public void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey) {
        if (isValidTemporalType(referenceClass)) {
            // Set a TypeConversionConverter on the mapping.
            if (m_temporalType.equals(JPA_TEMPORAL_DATE)) {
                setFieldClassification(mapping, java.sql.Date.class, isForMapKey);
            } else if(m_temporalType.equals(JPA_TEMPORAL_TIME)) {
                setFieldClassification(mapping,java.sql.Time.class, isForMapKey);
            } else {
                // Through annotation and XML validation, it must be
                // TemporalType.TIMESTAMP and can't be anything else.
                setFieldClassification(mapping, java.sql.Timestamp.class, isForMapKey);
            }

            setConverter(mapping, new TypeConversionConverter(mapping), isForMapKey);
        } else {
            throw ValidationException.invalidTypeForTemporalAttribute(accessor.getAttributeName(), referenceClass, accessor.getJavaClass());
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTemporalType(String temporalType) {
        m_temporalType = temporalType;
    }
}
