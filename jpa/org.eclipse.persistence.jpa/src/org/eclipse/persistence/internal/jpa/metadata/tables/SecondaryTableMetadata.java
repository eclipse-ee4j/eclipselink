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
//       - 218084: Implement metadata merging functionality between mapping file
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     02/20/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * INTERNAL:
 * Object to hold onto a secondary table metadata in a TopLink database table.
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
public class SecondaryTableMetadata extends TableMetadata {
    private PrimaryKeyForeignKeyMetadata m_primaryKeyForeignKey;
    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public SecondaryTableMetadata() {
        super("<secondary-table>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public SecondaryTableMetadata(MetadataAnnotation secondaryTable, MetadataAccessor accessor) {
        super(secondaryTable, accessor);

        if (secondaryTable != null) {
            for (Object pkJoinColumn : secondaryTable.getAttributeArray("pkJoinColumns")) {
                PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn = new PrimaryKeyJoinColumnMetadata((MetadataAnnotation) pkJoinColumn, accessor);
                m_primaryKeyJoinColumns.add(primaryKeyJoinColumn);
            }

            // Set the foreign key if one is specified in the annotation.
            if (secondaryTable.hasAttribute("foreignKey")) {
                m_primaryKeyForeignKey = new PrimaryKeyForeignKeyMetadata(secondaryTable.getAttributeAnnotation("foreignKey"), accessor);
            }
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof SecondaryTableMetadata) {
            SecondaryTableMetadata secondaryTable = (SecondaryTableMetadata) objectToCompare;

            if (! valuesMatch(m_primaryKeyForeignKey, secondaryTable.getPrimaryKeyForeignKey())) {
                return false;
            }

            return valuesMatch(m_primaryKeyJoinColumns, secondaryTable.getPrimaryKeyJoinColumns());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_primaryKeyForeignKey != null ? m_primaryKeyForeignKey.hashCode() : 0;
        result = 31 * result + (m_primaryKeyJoinColumns != null ? m_primaryKeyJoinColumns.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String getCatalogContext() {
        return MetadataLogger.SECONDARY_TABLE_CATALOG;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String getNameContext() {
        return MetadataLogger.SECONDARY_TABLE_NAME;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public PrimaryKeyForeignKeyMetadata getPrimaryKeyForeignKey() {
        return m_primaryKeyForeignKey;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<PrimaryKeyJoinColumnMetadata> getPrimaryKeyJoinColumns() {
        return m_primaryKeyJoinColumns;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String getSchemaContext() {
        return MetadataLogger.SECONDARY_TABLE_SCHEMA;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize single objects.
        initXMLObject(m_primaryKeyForeignKey, accessibleObject);

        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_primaryKeyJoinColumns, accessibleObject);
    }

    /**
     * INTERNAL:
     * Process any primary key foreign key specification for this table.
     */
    @Override
    public void processForeignKey() {
        if (m_primaryKeyForeignKey != null) {
            m_primaryKeyForeignKey.process(getDatabaseTable());
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPrimaryKeyForeignKey(PrimaryKeyForeignKeyMetadata primaryKeyForeignKey) {
        m_primaryKeyForeignKey = primaryKeyForeignKey;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
        m_primaryKeyJoinColumns = primaryKeyJoinColumns;
    }
}
