/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.StringHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.tools.schemaframework.IndexDefinition;

/**
 * INTERNAL:
 * Object to hold onto database index metadata.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class IndexMetadata extends ORMetadata {
    /** Name prefix. */
    private static final String INDEX = "INDEX";
    /** Name parts separator. */
    private static final char FIELD_SEP = '_';

    private Boolean m_unique;

    private String m_name;
    private String m_schema;
    private String m_catalog;
    private String m_table;
    private String m_columnList;

    private List<String> m_columnNames = new ArrayList();

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public IndexMetadata() {
        super("<index>");
    }

    /**
     * INTERNAL:
     */
    public IndexMetadata(MetadataAnnotation index, MetadataAccessor accessor) {
        super(index, accessor);

        m_name = index.getAttributeString("name");
        m_schema = index.getAttributeString("schema");
        m_catalog = index.getAttributeString("catalog");
        m_table = index.getAttributeString("table");
        m_unique = index.getAttributeBooleanDefaultFalse("unique");
        m_columnList = index.getAttributeString("columnList");

        for (Object columnName : index.getAttributeArray("columnNames")) {
            m_columnNames.add((String) columnName);
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof IndexMetadata) {
            IndexMetadata index = (IndexMetadata) objectToCompare;

            if (! valuesMatch(m_name, index.getName())) {
                return false;
            }

            if (! valuesMatch(m_unique, index.getUnique())) {
                return false;
            }

            if (! valuesMatch(m_schema, index.getSchema())) {
                return false;
            }

            if (! valuesMatch(m_catalog, index.getCatalog())) {
                return false;
            }

            if (! valuesMatch(m_table, index.getTable())) {
                return false;
            }

            if (! valuesMatch(m_columnList, index.getColumnList())) {
                return false;
            }

            return m_columnNames.equals(index.getColumnNames());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_unique != null ? m_unique.hashCode() : 0;
        result = 31 * result + (m_name != null ? m_name.hashCode() : 0);
        result = 31 * result + (m_schema != null ? m_schema.hashCode() : 0);
        result = 31 * result + (m_catalog != null ? m_catalog.hashCode() : 0);
        result = 31 * result + (m_table != null ? m_table.hashCode() : 0);
        result = 31 * result + (m_columnList != null ? m_columnList.hashCode() : 0);
        result = 31 * result + (m_columnNames != null ? m_columnNames.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCatalog() {
        return m_catalog;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getColumnList() {
        return m_columnList;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<String> getColumnNames() {
        return m_columnNames;
    }

    /**
     * INTERNAL:
     * Sub classed must that can uniquely be identified must override this
     * message to allow the overriding and merging to uniquely identify objects.
     * It will also be used when logging messages (that is provide a more
     * detailed message).
     *
     * @see shouldOverride
     * @see mergeListsAndOverride
     */
    @Override
    protected String getIdentifier() {
        return getName();
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
    public String getSchema() {
        return m_schema;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
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
     * Return true is a name has been specified for this index.
     */
    protected boolean hasName() {
        return m_name != null && ! m_name.equals("");
    }

    /**
     * INTERNAL:
     * Return true is there is a unique setting for this index.
     */
    protected boolean isUnique() {
        return m_unique != null && m_unique.booleanValue();
    }

    /**
     * INTERNAL:
     * Process the index metadata. This is called from all table metadata.
     * CollectionTable, SecondaryTable, JoinTable, Table, TableGenerator.
     */
    public void process(DatabaseTable table) {
        IndexDefinition indexDefinition = new IndexDefinition();

        // Process the column list (comma separated string)
        StringTokenizer st = new StringTokenizer(m_columnList, ",");
        while (st.hasMoreTokens()) {
            indexDefinition.addField(st.nextToken().trim());
        }

        // Process the name value.
        indexDefinition.setName(processName(table, indexDefinition));

        // Process the unique value.
        indexDefinition.setIsUnique(isUnique());

        // Add the index definition to the table provided.
        indexDefinition.setQualifier(table.getTableQualifier());
        indexDefinition.setTargetTable(table.getQualifiedName());
        table.addIndex(indexDefinition);
    }

    /**
     * INTERNAL:
     * Process the index metadata. This is called from EntityAccessor and
     * BasicAccesor (for Basic, Id and Version)
     */
    public void process(MetadataDescriptor descriptor, String defaultColumnName) {
        IndexDefinition indexDefinition = new IndexDefinition();
        DatabaseTable primaryTable = descriptor.getPrimaryTable();

        if (m_columnNames.isEmpty() && defaultColumnName != null) {
            indexDefinition.getFields().add(defaultColumnName);
        } else {
            indexDefinition.getFields().addAll(m_columnNames);
        }

        // Process the name value.
        indexDefinition.setName(processName(primaryTable, indexDefinition));

        // Process the schema value.
        if (m_schema != null && m_schema.length() != 0) {
            indexDefinition.setQualifier(m_schema);
        } else if (descriptor.getDefaultSchema() != null && descriptor.getDefaultSchema().length() != 0) {
            indexDefinition.setQualifier(descriptor.getDefaultSchema());
        }

        // Process the catalog value.
        if (m_catalog != null && m_catalog.length() != 0) {
            indexDefinition.setQualifier(m_catalog);
        } else if (descriptor.getDefaultCatalog() != null && descriptor.getDefaultCatalog().length() != 0) {
            indexDefinition.setQualifier(descriptor.getDefaultCatalog());
        }

        // Process the unique value.
        indexDefinition.setIsUnique(isUnique());

        // Process table value.
        if (m_table == null || m_table.length() == 0) {
            indexDefinition.setTargetTable(primaryTable.getQualifiedName());
            primaryTable.addIndex(indexDefinition);
        } else if (m_table.equals(primaryTable.getQualifiedName()) || m_table.equals(primaryTable.getName())) {
            indexDefinition.setTargetTable(m_table);
            primaryTable.addIndex(indexDefinition);
        } else {
            indexDefinition.setTargetTable(m_table);
            boolean found = false;
            for (DatabaseTable databaseTable : descriptor.getClassDescriptor().getTables()) {
                if (m_table.equals(databaseTable.getQualifiedName()) || m_table.equals(databaseTable.getName())) {
                    databaseTable.addIndex(indexDefinition);
                    found = true;
                }
            }

            if (!found) {
                primaryTable.addIndex(indexDefinition);
            }
        }
    }

    /**
     * INTERNAL:
     * Process the name. If specified it, use it, otherwise create a default.
     * e.g. INDEX_tablename_field1_field2_.....
     */
    protected String processName(DatabaseTable table, IndexDefinition indexDefinition) {
        if (hasName()) {
            return m_name;
        } else {
            String tableName = StringHelper.nonNullString(table.getName());
            // Calculate name length to avoid StringBuilder resizing
            int length = INDEX.length() + 1 + tableName.length();
            for (String field : indexDefinition.getFields()) {
                length += 1;
                length += StringHelper.nonNullString(field).length();
            }
            // Build name
            StringBuilder name = new StringBuilder(length);
            name.append(INDEX).append(FIELD_SEP).append(tableName);
            // Append all the field names to it.
            for (String field : indexDefinition.getFields()) {
                name.append(FIELD_SEP);
                name.append(StringHelper.nonNullString(field));
            }

            return name.toString();
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCatalog(String catalog) {
        m_catalog = catalog;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumnList(String columnList) {
        m_columnList = columnList;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumnNames(List<String> columnNames) {
        m_columnNames = columnNames;
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
    public void setSchema(String schema) {
        m_schema = schema;
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
}
