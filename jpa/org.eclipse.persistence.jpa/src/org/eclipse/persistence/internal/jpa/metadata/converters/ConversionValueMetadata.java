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
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Object to hold onto conversion values.
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
 * @since EclipseLink 1.0
 */
public class ConversionValueMetadata extends ORMetadata {
    private String m_dataValue;
    private String m_objectValue;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ConversionValueMetadata() {
        super("<conversion-value");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ConversionValueMetadata(MetadataAnnotation conversionValue, MetadataAccessor accessor) {
        super(conversionValue, accessor);

        m_dataValue = conversionValue.getAttributeString("dataValue");
        m_objectValue = conversionValue.getAttributeString("objectValue");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ConversionValueMetadata) {
            ConversionValueMetadata conversionValue = (ConversionValueMetadata) objectToCompare;

            if (! valuesMatch(m_dataValue, conversionValue.getDataValue())) {
                return false;
            }

            return valuesMatch(m_objectValue, conversionValue.getObjectValue());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_dataValue != null ? m_dataValue.hashCode() : 0;
        result = 31 * result + (m_objectValue != null ? m_objectValue.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDataValue() {
        return m_dataValue;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getObjectValue() {
        return m_objectValue;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDataValue(String dataValue) {
        m_dataValue = dataValue;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setObjectValue(String objectValue) {
        m_objectValue = objectValue;
    }
}
