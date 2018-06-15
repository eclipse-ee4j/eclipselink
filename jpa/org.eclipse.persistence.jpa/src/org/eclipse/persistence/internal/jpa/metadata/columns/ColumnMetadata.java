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
//     06/09/2009-2.0 Guy Pelletier
//       - 249037: JPA 2.0 persisting list item index
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL: Object to process a JPA column into an EclipseLink database field.
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
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class ColumnMetadata extends DirectColumnMetadata {
    private Boolean m_unique;
    private Integer m_scale;
    private Integer m_length;
    private Integer m_precision;
    private String m_table;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ColumnMetadata() {
        super("<column>");
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected ColumnMetadata(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     * Used for defaulting.
     */
    public ColumnMetadata(MetadataAccessor accessor) {
        this(null, accessor);
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ColumnMetadata(MetadataAnnotation column, MetadataAccessor accessor) {
        super(column, accessor);

        if (column != null) {
            // Apply the values from the column annotation.
            setUnique(column.getAttributeBooleanDefaultFalse("unique"));

            setScale(column.getAttributeInteger("scale"));
            setLength(column.getAttributeInteger("length"));
            setPrecision(column.getAttributeInteger("precision"));

            setTable(column.getAttributeString("table"));
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof ColumnMetadata) {
            ColumnMetadata column = (ColumnMetadata) objectToCompare;

            if (!valuesMatch(m_unique, column.getUnique())) {
                return false;
            }

            if (!valuesMatch(m_scale, column.getScale())) {
                return false;
            }

            if (!valuesMatch(m_length, column.getLength())) {
                return false;
            }

            if (!valuesMatch(m_precision, column.getPrecision())) {
                return false;
            }

            return valuesMatch(m_table, column.getTable());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_unique != null ? m_unique.hashCode() : 0;
        result = 31 * result + (m_scale != null ? m_scale.hashCode() : 0);
        result = 31 * result + (m_length != null ? m_length.hashCode() : 0);
        result = 31 * result + (m_precision != null ? m_precision.hashCode() : 0);
        result = 31 * result + (m_table != null ? m_table.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     */
    @Override
    public DatabaseField getDatabaseField() {
        DatabaseField field = super.getDatabaseField();
        field.setUnique(m_unique == null ? false : m_unique.booleanValue());
        field.setScale(m_scale == null ? 0 : m_scale.intValue());
        field.setLength(m_length == null ? 0 : m_length.intValue());
        field.setPrecision(m_precision == null ? 0 : m_precision.intValue());
        field.setTableName(m_table == null ? "" : m_table);
        return field;
    }

    /**
     * INTERNAL: Used for OX mapping.
     */
    public Integer getLength() {
        return m_length;
    }

    /**
     * INTERNAL: Used for OX mapping.
     */
    public Integer getPrecision() {
        return m_precision;
    }

    /**
     * INTERNAL: Used for OX mapping.
     */
    public Integer getScale() {
        return m_scale;
    }

    /**
     * INTERNAL: Used for OX mapping.
     */
    public String getTable() {
        return m_table;
    }

    /**
     * INTERNAL: Used for OX mapping.
     */
    public Boolean getUnique() {
        return m_unique;
    }

    /**
     * INTERNAL: Used for OX mapping.
     */
    public void setLength(Integer length) {
        m_length = length;
    }

    /**
     * INTERNAL: Used for OX mapping.
     */
    public void setPrecision(Integer precision) {
        m_precision = precision;
    }

    /**
     * INTERNAL: Used for OX mapping.
     */
    public void setScale(Integer scale) {
        m_scale = scale;
    }

    /**
     * INTERNAL: Used for OX mapping.
     */
    public void setTable(String table) {
        m_table = table;
    }

    /**
     * INTERNAL: Used for OX mapping.
     */
    public void setUnique(Boolean unique) {
        m_unique = unique;
    }
}
