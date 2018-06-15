/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
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
import org.eclipse.persistence.internal.jpa.metadata.columns.ForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * INTERNAL:
 * Object to hold onto a relational table metadata in an EclipseLink
 * database table. By relational we mean one that has join columns and a
 * foreign key specification.
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
 * @since EclipseLink 2.5
 */
public class RelationalTableMetadata extends TableMetadata {
    private ForeignKeyMetadata m_foreignKey;
    private List<JoinColumnMetadata> m_joinColumns = new ArrayList<JoinColumnMetadata>();

    /**
     * INTERNAL:
     * Used for defaulting.
     */
    public RelationalTableMetadata(MetadataAccessor accessor) {
        super(null, accessor);
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public RelationalTableMetadata(MetadataAnnotation relationalTable, MetadataAccessor accessor) {
        super(relationalTable, accessor);

        if (relationalTable != null) {
            // Add the join columns if specified in the annotation.
            for (Object joinColumn : relationalTable.getAttributeArray("joinColumns")) {
                m_joinColumns.add(new JoinColumnMetadata((MetadataAnnotation) joinColumn, accessor));
            }

            // Set the foreign key if one is specified in the annotation.
            if (relationalTable.hasAttribute("foreignKey")) {
                m_foreignKey = new ForeignKeyMetadata(relationalTable.getAttributeAnnotation("foreignKey"), accessor);
            }
        }
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public RelationalTableMetadata(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare)&& objectToCompare instanceof RelationalTableMetadata) {
            RelationalTableMetadata relationalTable = (RelationalTableMetadata) objectToCompare;

            if (! valuesMatch(m_joinColumns, relationalTable.getJoinColumns())) {
                return false;
            }

            return valuesMatch(m_foreignKey, relationalTable.getForeignKey());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_foreignKey != null ? m_foreignKey.hashCode() : 0;
        result = 31 * result + (m_joinColumns != null ? m_joinColumns.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ForeignKeyMetadata getForeignKey() {
        return m_foreignKey;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<JoinColumnMetadata> getJoinColumns() {
        return m_joinColumns;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize single objects.
        initXMLObject(m_foreignKey, accessibleObject);

        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_joinColumns, accessibleObject);
    }

    /**
     * INTERNAL:
     * Process any foreign key specification for this table.
     */
    @Override
    public void processForeignKey() {
        if (m_foreignKey != null) {
            m_foreignKey.process(getDatabaseTable());
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setForeignKey(ForeignKeyMetadata foreignKey) {
        m_foreignKey = foreignKey;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJoinColumns(List<JoinColumnMetadata> joinColumns) {
        m_joinColumns = joinColumns;
    }
}
