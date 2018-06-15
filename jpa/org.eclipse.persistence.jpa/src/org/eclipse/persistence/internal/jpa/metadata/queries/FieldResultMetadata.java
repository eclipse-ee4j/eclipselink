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
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.queries.FieldResult;

/**
 * INTERNAL:
 * Object to hold onto an field result metadata.
 *
 * Key notes:
 * - all metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - all metadata mapped from XML must be initialized in the initXMLObject
 *   method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class FieldResultMetadata extends ORMetadata {
    private String m_name;
    private String m_column;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public FieldResultMetadata() {
        super("<field-result>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public FieldResultMetadata(MetadataAnnotation fieldResult, MetadataAccessor accessor) {
        super(fieldResult, accessor);

        m_name = fieldResult.getAttributeString("name");
        m_column = fieldResult.getAttributeString("column");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof FieldResultMetadata) {
            FieldResultMetadata fieldResult = (FieldResultMetadata) objectToCompare;

            if (! valuesMatch(m_name, fieldResult.getName())) {
                return false;
            }

            return valuesMatch(m_column, fieldResult.getColumn());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_name != null ? m_name.hashCode() : 0;
        result = 31 * result + (m_column != null ? m_column.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getColumn() {
        return m_column;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }

    /**
     * INTERNAL:
     * NOTE: Both the name and column are required in XML and annotations
     * so that makes processing easier (no need for defaults).
     */
    public FieldResult process() {
        DatabaseField field = new DatabaseField();

        // Process the name (taking into consideration delimiters etc.)
        setFieldName(field, getColumn());

        // Return a new field result to the entity result.
        return new FieldResult(getName(), field);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumn(String column) {
        m_column = column;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
}
