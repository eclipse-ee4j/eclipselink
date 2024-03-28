/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     02/20/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.util.List;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;

/**
 * INTERNAL:
 * Object to process JPA foreign key metadata.
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
 * @since EclipseLink 2.5
 */
public class ForeignKeyMetadata extends ORMetadata {
    protected String m_name;
    protected String m_constraintMode;
    protected String m_foreignKeyDefinition;
    private String m_options;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ForeignKeyMetadata() {
        super("<foreign-key>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading from subclasses of ForeignKeyMetadata.
     */
    public ForeignKeyMetadata(ForeignKeyMetadata foreignKey) {
        super(foreignKey);

        m_name = foreignKey.getName();
        m_constraintMode = foreignKey.getConstraintMode();
        m_foreignKeyDefinition = foreignKey.getForeignKeyDefinition();
        m_options = foreignKey.getOptions();
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ForeignKeyMetadata(MetadataAnnotation foreignKey, MetadataAccessor accessor) {
        super(foreignKey, accessor);

        m_name = foreignKey.getAttributeString("name");
        m_constraintMode = foreignKey.getAttributeString("value");
        m_foreignKeyDefinition = foreignKey.getAttributeString("foreignKeyDefinition");
        m_options = foreignKey.getAttributeString("options");
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ForeignKeyMetadata(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ForeignKeyMetadata foreignKey) {

            if (! valuesMatch(m_name, foreignKey.getName())) {
                return false;
            }

            if (! valuesMatch(m_foreignKeyDefinition, foreignKey.getForeignKeyDefinition())) {
                return false;
            }

            if (! valuesMatch(m_options, foreignKey.getOptions())) {
                return false;
            }

            return valuesMatch(m_constraintMode, foreignKey.getConstraintMode());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_name != null ? m_name.hashCode() : 0);
        result = 31 * result + (m_constraintMode != null ? m_constraintMode.hashCode() : 0);
        result = 31 * result + (m_foreignKeyDefinition != null ? m_foreignKeyDefinition.hashCode() : 0);
        result = 31 * result + (m_options != null ? m_options.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getConstraintMode() {
        return m_constraintMode;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getForeignKeyDefinition() {
        return m_foreignKeyDefinition;
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
     */
    protected boolean isConstraintMode() {
        return m_constraintMode == null || m_constraintMode.equals(MetadataConstants.JPA_CONSTRAINT_MODE_CONSTRAINT);
    }

    /**
     * INTERNAL:
     */
    protected boolean isNoConstraintMode() {
        return m_constraintMode != null && m_constraintMode.equals(MetadataConstants.JPA_CONSTRAINT_MODE_NO_CONSTRAINT);
    }

    /**
     * INTERNAL:
     */
    protected boolean isProviderDefaultConstraintMode() {
        return m_constraintMode != null && m_constraintMode.equals(MetadataConstants.JPA_CONSTRAINT_MODE_PROVIDER_DEFAULT);
    }

    /**
     * INTERNAL:
     * Process this JPA metadata into an EclipseLink ForeignKeyConstraint.
     */
    public void process(DatabaseTable table) {
        process(table, null, null, null);
    }

    /**
     * INTERNAL:
     * Process this JPA metadata into an EclipseLink ForeignKeyConstraint.
     */
    public void process(DatabaseTable table, List<String> sourceFields, List<String> targetFields, DatabaseTable targetTable) {
        if (! isProviderDefaultConstraintMode()) {
            ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint();
            foreignKeyConstraint.setName(getName());
            foreignKeyConstraint.setForeignKeyDefinition(getForeignKeyDefinition());
            foreignKeyConstraint.setDisableForeignKey(isNoConstraintMode());
            // Bug 441546 - Foreign Key attribute when used in JoinColumn generates wrong DDL statement
            // If foreignKeyDefinition element is not specified, the provider will generate foreign
            // key constraints whose update and delete actions it determines most appropriate for
            // the join column(s) to which the foreign key annotation is applied.
            if (getForeignKeyDefinition() == null) {
                if (sourceFields != null) {
                    foreignKeyConstraint.setSourceFields(sourceFields);
                }
                if (targetFields != null) {
                    foreignKeyConstraint.setTargetFields(targetFields);
                }
                if (targetTable != null) {
                    foreignKeyConstraint.setTargetTable(targetTable.getName());
                }
            }
            table.addForeignKeyConstraint(foreignKeyConstraint);
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConstraintMode(String constraintMode) {
        m_constraintMode = constraintMode;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setForeignKeyDefinition(String foreignKeyDefinition) {
        m_foreignKeyDefinition = foreignKeyDefinition;
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
