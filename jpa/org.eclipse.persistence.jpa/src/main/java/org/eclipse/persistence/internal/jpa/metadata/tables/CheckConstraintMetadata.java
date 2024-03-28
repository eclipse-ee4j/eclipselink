/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.metadata.tables;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.StringHelper;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.tools.schemaframework.CheckConstraint;
import org.eclipse.persistence.tools.schemaframework.IndexDefinition;

/**
 * INTERNAL:
 * A basic version accessor.
 * <p>
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject  method.
 * - methods should be preserved in alphabetical order.
 */
public class CheckConstraintMetadata extends ORMetadata {

    /** Name prefix. */
    private static final String CHK = "CHK_";

    /** Name parts separator. */
    private static final char FIELD_SEP = '_';

    private String m_name;
    private String m_constraint;
    private String m_options;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public CheckConstraintMetadata() {
        super("<check-constraint>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public CheckConstraintMetadata(MetadataAnnotation checkConstraint, MetadataAccessor accessor) {
        super(checkConstraint, accessor);

        m_name = checkConstraint.getAttributeString("name");
        m_constraint = checkConstraint.getAttributeString("constraint");
        m_options = checkConstraint.getAttributeString("options");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof CheckConstraintMetadata checkConstraint) {

            if (! valuesMatch(m_name, checkConstraint.getName())) {
                return false;
            }

            if (! valuesMatch(m_constraint, checkConstraint.getConstraint())) {
                return false;
            }

            return valuesMatch(m_options, checkConstraint.getOptions());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_name != null ? m_name.hashCode() : 0);
        result = 31 * result + (m_constraint != null ? m_constraint.hashCode() : 0);
        result = 31 * result + (m_options != null ? m_options.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getConstraint() {
        return m_constraint;
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
     * Return true if a name has been specified for this check constraint.
     */
    public boolean hasName() {
        return m_name != null && !m_name.isEmpty();
    }

    public void process(DatabaseTable table) {
        CheckConstraint checkConstraint = new CheckConstraint();
        checkConstraint.setName(processName(table));
        checkConstraint.setConstraint(getConstraint());
        checkConstraint.setOptions(getOptions());
        table.addCheckConstraint(checkConstraint);
    }

    /**
     * INTERNAL:
     * Process the name. If specified it, use it, otherwise create a default.
     * e.g. CHK_tablename_idx
     */
    private String processName(DatabaseTable table) {
        return hasName()
                ? m_name
                : CHK + table.getName() + FIELD_SEP + (table.getCheckConstraints().size() + 1);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConstraint(String columnNames) {
        m_constraint = columnNames;
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
