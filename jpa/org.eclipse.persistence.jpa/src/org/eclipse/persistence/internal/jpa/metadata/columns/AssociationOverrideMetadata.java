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
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
//     06/02/2009-2.0 Guy Pelletier
//       - 278768: JPA 2.0 Association Override Join Table
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     02/20/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.tables.JoinTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * Object to hold onto an association override meta data.
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
public class AssociationOverrideMetadata extends OverrideMetadata {
    private ForeignKeyMetadata m_foreignKey;
    private JoinTableMetadata m_joinTable;
    private List<JoinColumnMetadata> m_joinColumns = new ArrayList<JoinColumnMetadata>();

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public AssociationOverrideMetadata() {
        super("<association-override>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public AssociationOverrideMetadata(MetadataAnnotation associationOverride, MetadataAccessor accessor) {
        super(associationOverride, accessor);

        // Set the join columns.
        for (Object joinColumn : associationOverride.getAttributeArray("joinColumns")) {
            m_joinColumns.add(new JoinColumnMetadata((MetadataAnnotation) joinColumn, accessor));
        }

        // Set the foreign key if one is specified in the annotation.
        if (associationOverride.hasAttribute("foreignKey")) {
            m_foreignKey = new ForeignKeyMetadata(associationOverride.getAttributeAnnotation("foreignKey"), accessor);
        }

        // Set the join table.
        m_joinTable = new JoinTableMetadata(associationOverride.getAttributeAnnotation("joinTable"), accessor);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof AssociationOverrideMetadata) {
            AssociationOverrideMetadata associationOverride = (AssociationOverrideMetadata) objectToCompare;

            if (! valuesMatch(m_joinColumns, associationOverride.getJoinColumns())) {
                return false;
            }

            if (! valuesMatch(m_foreignKey, associationOverride.getForeignKey())) {
                return false;
            }

            return valuesMatch(m_joinTable, associationOverride.getJoinTable());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_foreignKey != null ? m_foreignKey.hashCode() : 0;
        result = 31 * result + (m_joinTable != null ? m_joinTable.hashCode() : 0);
        result = 31 * result + (m_joinColumns != null ? m_joinColumns.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     */
    @Override
    public String getIgnoreMappedSuperclassContext() {
        return MetadataLogger.IGNORE_MAPPED_SUPERCLASS_ASSOCIATION_OVERRIDE;
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
     * Used for OX mapping.
     */
    public JoinTableMetadata getJoinTable() {
        return m_joinTable;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize single objects.
        initXMLObject(m_joinTable, accessibleObject);
        initXMLObject(m_foreignKey, accessibleObject);

        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_joinColumns, accessibleObject);
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

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJoinTable(JoinTableMetadata joinTable) {
        m_joinTable = joinTable;
    }
}
