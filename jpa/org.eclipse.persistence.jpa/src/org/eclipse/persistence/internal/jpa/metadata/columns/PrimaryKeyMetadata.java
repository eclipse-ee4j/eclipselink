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
//     James Sutherland - initial impl
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     06/18/2010-2.2 Guy Pelletier
//       - 300458: EclispeLink should throw a more specific exception than NPE
//     09/03/2010-2.2 Guy Pelletier
//       - 317286: DB column lenght not in sync between @Column and @JoinColumn
//     10/28/2010-2.2 Guy Pelletier
//       - 3223850: Primary key metadata issues
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;

/**
 * Object to hold onto primary key metadata.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @see PrimaryKey
 * @author James Sutherland
 * @since EclipseLink 1.1
 */
public class PrimaryKeyMetadata extends ORMetadata {
    private String m_validation;
    private String m_cacheKeyType;
    private List<ColumnMetadata> m_columns = new ArrayList<ColumnMetadata>();

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public PrimaryKeyMetadata() {
        super("<primary-key>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public PrimaryKeyMetadata(MetadataAnnotation primaryKey, MetadataAccessor accessor) {
        super(primaryKey, accessor);

        m_validation = primaryKey.getAttributeString("validation");

        m_cacheKeyType = primaryKey.getAttributeString("cacheKeyType");

        for (Object selectedColumn : primaryKey.getAttributeArray("columns")) {
            m_columns.add(new ColumnMetadata((MetadataAnnotation)selectedColumn, accessor));
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof PrimaryKeyMetadata) {
            PrimaryKeyMetadata primaryKey = (PrimaryKeyMetadata) objectToCompare;

            if (! valuesMatch(m_validation, primaryKey.getValidation())) {
                return false;
            }

            if (! valuesMatch(m_cacheKeyType, primaryKey.getCacheKeyType())) {
                return false;
            }

            return valuesMatch(m_columns, primaryKey.getColumns());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_validation != null ? m_validation.hashCode() : 0;
        result = 31 * result + (m_cacheKeyType != null ? m_cacheKeyType.hashCode() : 0);
        result = 31 * result + (m_columns != null ? m_columns.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCacheKeyType() {
        return m_cacheKeyType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ColumnMetadata> getColumns() {
        return m_columns;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getValidation() {
        return m_validation;
    }

    /**
     * INTERNAL:
     */
    public boolean hasColumns() {
        return ! m_columns.isEmpty();
    }

    /**
     * Process the meta-data, configure primary key and idValidation in descriptor.
     */
    public void process(MetadataDescriptor descriptor) {
        descriptor.setHasPrimaryKey();

        if (m_validation != null) {
            descriptor.getClassDescriptor().setIdValidation(IdValidation.valueOf(m_validation));
        }

        if (m_cacheKeyType != null) {
            descriptor.getClassDescriptor().setCacheKeyType(CacheKeyType.valueOf(m_cacheKeyType));
        }

        if (hasColumns()) {
            for (ColumnMetadata column : m_columns) {
                if (column.getName().equals("")) {
                    throw ValidationException.primaryKeyColumnNameNotSpecified(descriptor.getJavaClass());
                } else {
                    descriptor.addPrimaryKeyField(column.getDatabaseField());
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCacheKeyType(String cacheKeyType) {
        m_cacheKeyType = cacheKeyType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumns(List<ColumnMetadata> columns) {
        m_columns = columns;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValidation(String validation) {
        m_validation = validation;
    }
}
