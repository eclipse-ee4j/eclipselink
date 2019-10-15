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
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     06/09/2009-2.0 Guy Pelletier
//       - 249037: JPA 2.0 persisting list item index
//     09/03/2010-2.2 Guy Pelletier
//       - 317286: DB column lenght not in sync between @Column and @JoinColumn
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to process JPA join columns EclipseLink database fields.
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
public class JoinColumnMetadata extends RelationalColumnMetadata {
    private Boolean m_unique;
    private Boolean m_nullable;
    private Boolean m_updatable;
    private Boolean m_insertable;

    private String m_table;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public JoinColumnMetadata() {
        super("<join-column>");
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected JoinColumnMetadata(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public JoinColumnMetadata(MetadataAnnotation joinColumn, MetadataAccessor accessor) {
        super(joinColumn, accessor);

        if (joinColumn != null) {
            m_table = joinColumn.getAttributeString("table");
            m_unique = joinColumn.getAttributeBooleanDefaultFalse("unique");
            m_nullable = joinColumn.getAttributeBooleanDefaultTrue("nullable");
            m_updatable = joinColumn.getAttributeBooleanDefaultTrue("updatable");
            m_insertable = joinColumn.getAttributeBooleanDefaultTrue("insertable");
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof JoinColumnMetadata) {
            JoinColumnMetadata joinColumn = (JoinColumnMetadata) objectToCompare;

            if (! valuesMatch(m_unique, joinColumn.getUnique())) {
                return false;
            }

            if (! valuesMatch(m_nullable, joinColumn.getNullable())) {
                return false;
            }

            if (! valuesMatch(m_updatable, joinColumn.getUpdatable())) {
                return false;
            }

            if (! valuesMatch(m_insertable, joinColumn.getInsertable())) {
                return false;
            }

            return valuesMatch(m_table, joinColumn.getTable());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_unique != null ? m_unique.hashCode() : 0;
        result = 31 * result + (m_nullable != null ? m_nullable.hashCode() : 0);
        result = 31 * result + (m_updatable != null ? m_updatable.hashCode() : 0);
        result = 31 * result + (m_insertable != null ? m_insertable.hashCode() : 0);
        result = 31 * result + (m_table != null ? m_table.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     */
    @Override
    protected DatabaseField getForeignKeyField() {
        // Initialize the DatabaseField with values and defaults.
        DatabaseField fkField = super.getForeignKeyField();

        fkField.setTableName(m_table == null ? "" : m_table);
        fkField.setUnique(m_unique == null ? false : m_unique.booleanValue());
        fkField.setNullable(m_nullable == null ? true : m_nullable.booleanValue());
        fkField.setUpdatable(m_updatable == null ? true : m_updatable.booleanValue());
        fkField.setInsertable(m_insertable == null ? true : m_insertable.booleanValue());

        return fkField;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getInsertable() {
        return m_insertable;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getNullable() {
        return m_nullable;
    }

    /**
     * INTERNAL:
     * USed for OX mapping.
     */
    public String getTable() {
        return m_table;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getUnique() {
        return m_unique;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getUpdatable() {
        return m_updatable;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setInsertable(Boolean insertable) {
        m_insertable = insertable;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setNullable(Boolean nullable) {
        m_nullable = nullable;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTable(String table) {
        m_table = table;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setUnique(Boolean unique) {
        m_unique = unique;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setUpdatable(Boolean updatable) {
        m_updatable = updatable;
    }
}
