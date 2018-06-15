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
 * INTERNAL:
 * Object to hold onto relation (fk and pk) column metadata in a Eclipselink
 * database field.
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
public class DirectColumnMetadata extends MetadataColumn {
    private Boolean m_nullable;
    private Boolean m_updatable;
    private Boolean m_insertable;

    /**
     * INTERNAL:
     * Used annotation loading.
     */
    public DirectColumnMetadata(MetadataAnnotation directColumn, MetadataAccessor accessor) {
        super(directColumn, accessor);

        if (directColumn != null) {
            m_nullable = directColumn.getAttributeBooleanDefaultTrue("nullable");
            m_updatable = directColumn.getAttributeBooleanDefaultTrue("updatable");
            m_insertable = directColumn.getAttributeBooleanDefaultTrue("insertable");
        }
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected DirectColumnMetadata(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof DirectColumnMetadata) {
            DirectColumnMetadata directColumn = (DirectColumnMetadata) objectToCompare;

            if (! valuesMatch(m_nullable, directColumn.getNullable())) {
                return false;
            }

            if (! valuesMatch(m_updatable, directColumn.getUpdatable())) {
                return false;
            }

            return valuesMatch(m_insertable, directColumn.getInsertable());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_nullable != null ? m_nullable.hashCode() : 0;
        result = 31 * result + (m_updatable != null ? m_updatable.hashCode() : 0);
        result = 31 * result + (m_insertable != null ? m_insertable.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     */
    @Override
    public DatabaseField getDatabaseField() {
        DatabaseField databaseField = super.getDatabaseField();

        databaseField.setNullable(m_nullable == null ? true : m_nullable.booleanValue());
        databaseField.setUpdatable(m_updatable == null ? true : m_updatable.booleanValue());
        databaseField.setInsertable(m_insertable == null ? true : m_insertable.booleanValue());

        return databaseField;
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
    public void setUpdatable(Boolean updatable) {
        m_updatable = updatable;
    }
}
