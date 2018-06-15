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
//     09/03/2010-2.2 Guy Pelletier
//       - 317286: DB column lenght not in sync between @Column and @JoinColumn
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to process JPA relational type colummns into EclipseLink database fields.
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
public abstract class RelationalColumnMetadata extends MetadataColumn {
    // NOTE: The foreign key metadata is currently not mapped in the join-column
    // XML element, rather it is mapped as a separate element in a sequence with
    // it. Therefore, this element is only populated through annotation
    // processing right now, BUT this should be made available from our
    // eclipselink-orm.xml and therefore maintaining a better annotation/xml
    // mirror which was unfortunately not followed with JPA 2.1.
    private ForeignKeyMetadata m_foreignKey;

    private String m_referencedColumnName;

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public RelationalColumnMetadata(MetadataAnnotation relationalColumn, MetadataAccessor accessor) {
        super(relationalColumn, accessor);

        if (relationalColumn != null) {
            m_referencedColumnName = (relationalColumn.getAttributeString("referencedColumnName"));

            // Also allow EIS/NoSQL synonym referencedFieldName
            if (m_referencedColumnName == null) {
                m_referencedColumnName = (relationalColumn.getAttributeString("referencedFieldName"));
            }

            // Set a foreign key if one if specified in the annotation.
            if (relationalColumn.hasAttribute("foreignKey")) {
                m_foreignKey = new ForeignKeyMetadata(relationalColumn.getAttributeAnnotation("foreignKey"), accessor);
            }
        }
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected RelationalColumnMetadata(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof RelationalColumnMetadata) {
            RelationalColumnMetadata relationalColumn = (RelationalColumnMetadata) objectToCompare;

            if (! valuesMatch(m_foreignKey, relationalColumn.getForeignKey())) {
                return false;
            }

            return valuesMatch(m_referencedColumnName, relationalColumn.getReferencedColumnName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_foreignKey != null ? m_foreignKey.hashCode() : 0);
        result = 31 * result + (m_referencedColumnName != null ? m_referencedColumnName.hashCode() : 0);
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
     */
    protected DatabaseField getForeignKeyField() {
        return super.getDatabaseField();
    }

    /**
     * INTERNAL:
     * By calling this method we will return the foreign key field with the
     * extra metadata from the primary key field that can not be applied to a
     * referenced column.
     */
    public DatabaseField getForeignKeyField(DatabaseField primaryKeyField) {
        // Initialize the DatabaseField with values and defaults.
        DatabaseField fkField = getForeignKeyField();

        // Primary key field is null in variable one to one case.
        if (primaryKeyField != null) {
            fkField.setLength(primaryKeyField.getLength());
            fkField.setPrecision(primaryKeyField.getPrecision());
            fkField.setScale(primaryKeyField.getScale());
        }

        return fkField;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getReferencedColumnName() {
        return m_referencedColumnName;
    }

    /**
     * INTERNAL:
     * Return true if a foreign key has been defined on this column.
     */
    public boolean hasForeignKey() {
        return m_foreignKey != null;
    }

    /**
     * INTERNAL:
     */
    public boolean isForeignKeyFieldNotSpecified() {
        return getName() == null || getName().equals("");
    }

    /**
     * INTERNAL:
     */
    public boolean isPrimaryKeyFieldNotSpecified() {
        return m_referencedColumnName == null || m_referencedColumnName.equals("");
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
    public void setReferencedColumnName(String referencedColumnName) {
        m_referencedColumnName = referencedColumnName;
    }
}
