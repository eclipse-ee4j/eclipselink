/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL: Object to process a JPA column into an EclipseLink database field.
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
     * INTERNAL: Used for OX mapping.
     */
    public ColumnMetadata() {
        super("<column>");
    }

    /**
     * INTERNAL:
     */
    public ColumnMetadata(MetadataAnnotation column, MetadataAccessibleObject accessibleObject) {
        super(column, accessibleObject);

        if (column != null) {
            // Apply the values from the column annotation.
            setUnique((Boolean) column.getAttribute("unique"));
            setNullable((Boolean) column.getAttribute("nullable"));
            setUpdatable((Boolean) column.getAttribute("updatable"));
            setInsertable((Boolean) column.getAttribute("insertable"));

            setScale((Integer) column.getAttribute("scale"));
            setLength((Integer) column.getAttribute("length"));
            setPrecision((Integer) column.getAttribute("precision"));

            setName((String) column.getAttributeString("name"));
            setTable((String) column.getAttributeString("table"));
            setColumnDefinition((String) column.getAttributeString("columnDefinition"));
        }
    }

    /**
     * INTERNAL:
     */
    public ColumnMetadata(MetadataAccessibleObject accessibleObject) {
        this(null, accessibleObject);
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

    /**
     * INTERNAL:
     */
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
