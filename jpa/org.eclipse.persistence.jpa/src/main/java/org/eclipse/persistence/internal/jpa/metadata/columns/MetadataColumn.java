/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.tables.CheckConstraintMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * INTERNAL:
 * Object to process JPA column type into EclipseLink database fields.
 * <p>
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
public abstract class MetadataColumn extends ORMetadata {
    private String m_name;
    private String m_columnDefinition;
    private String m_options;
    private String m_comment;
    private List<CheckConstraintMetadata> m_checkConstraints = new ArrayList<>();

    /**
     * INTERNAL:
     * Used for defaulting.
     */
    protected MetadataColumn(MetadataAccessor accessor) {
        super(null, accessor);
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    protected MetadataColumn(MetadataAnnotation column, MetadataAccessor accessor) {
        super(column, accessor);

        if (column != null) {
            m_name = column.getAttributeString("name");
            m_columnDefinition =  column.getAttributeString("columnDefinition");
            m_options = column.getAttributeString("options");
            m_comment = column.getAttributeString("comment");

            for (Object checkConstraint : column.getAttributeArray("check")) {
                m_checkConstraints.add(new CheckConstraintMetadata((MetadataAnnotation) checkConstraint, accessor));
            }
        }
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected MetadataColumn(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof MetadataColumn column) {

            if (! valuesMatch(m_columnDefinition, column.getColumnDefinition())) {
                return false;
            }

            if (! valuesMatch(m_name, column.getName())) {
                return false;
            }

            if (! valuesMatch(m_comment, column.getComment())) {
                return false;
            }

            if (! valuesMatch(m_checkConstraints, column.getCheckConstraints())) {
                return false;
            }

            return valuesMatch(m_options, column.getOptions());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_name != null ? m_name.hashCode() : 0);
        result = 31 * result + (m_columnDefinition != null ? m_columnDefinition.hashCode() : 0);
        result = 31 * result + (m_options != null ? m_options.hashCode() : 0);
        result = 31 * result + (m_comment != null ? m_comment.hashCode() : 0);
        result = 31 * result + (m_checkConstraints != null ? m_checkConstraints.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<CheckConstraintMetadata> getCheckConstraints() {
        return m_checkConstraints;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getColumnDefinition() {
        return m_columnDefinition;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getComment() {
        return m_comment;
    }

    /**
     * INTERNAL:
     * Those objects that need/want to initialize more meta data should
     * override this method.
     */
    public DatabaseField getDatabaseField() {
        // Initialize the DatabaseField with values and defaults.
        DatabaseField databaseField = new DatabaseField();
        //use the following method to manage delimited or case insensitive defaults
        setFieldName(databaseField, m_name == null ? "" : m_name);
        databaseField.setColumnDefinition(m_columnDefinition == null ? "" : m_columnDefinition);
        databaseField.setComment(m_comment);
        databaseField.setOptionalSuffix(m_options);

        return databaseField;
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
     * Used for OX mapping.
     */
    public String getOptions() {
        return m_options;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCheckConstraints(List<CheckConstraintMetadata> checkConstraints) {
        m_checkConstraints = checkConstraints;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumnDefinition(String columnDefinition) {
        m_columnDefinition = columnDefinition;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setComment(String comment) {
        m_comment = comment;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOptions(String options) {
        m_options = options;
    }

}
