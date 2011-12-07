/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.helper.DatabaseTable;
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
    private Boolean m_unique;
    
    private String m_name;
    private String m_schema;
    private String m_catalog;
    private String m_table;
    
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
        
        if (index != null) {
            m_name = (String) index.getAttribute("name"); 
            m_schema = (String) index.getAttribute("schema"); 
            m_catalog = (String) index.getAttribute("catalog");
            m_table = (String) index.getAttribute("table");
            m_unique = (Boolean) index.getAttribute("unique");
            
            for (Object columnName : (Object[]) index.getAttributeArray("columnNames")) {
                m_columnNames.add((String)columnName);
            }
        }
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof IndexMetadata) {
            IndexMetadata table = (IndexMetadata) objectToCompare;
            
            if (! valuesMatch(this.m_name, table.getName())) {
                return false;
            }
            
            if (! valuesMatch(this.m_schema, table.getSchema())) {
                return false;
            }
            
            if (! valuesMatch(this.m_catalog, table.getCatalog())) {
                return false;
            }
            
            return this.m_columnNames.equals(table.getColumnNames());
        }
        
        return false;
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
     * Process the index metadata
     */
    public void process(MetadataDescriptor descriptor, String defaultColumnName) {
        IndexDefinition indexDefinition = new IndexDefinition();
        
        if (m_columnNames.isEmpty() && defaultColumnName != null) {
            indexDefinition.getFields().add(defaultColumnName);
        } else {
            indexDefinition.getFields().addAll(m_columnNames);
        }
        
        // Process the name value.
        if (m_name != null && m_name.length() != 0) {
            indexDefinition.setName(m_name);            
        } else {            
            String name = "INDEX_" + descriptor.getPrimaryTableName();
            
            for (String column : indexDefinition.getFields()) {
                name = name + "_" + column;
            }
            
            indexDefinition.setName(name);
        }
        
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
        if (m_unique != null) {
            indexDefinition.setIsUnique(m_unique);
        }
        
        // Process table value.
        if (m_table == null || m_table.length() == 0) {
            indexDefinition.setTargetTable(descriptor.getPrimaryTable().getQualifiedName());
            descriptor.getPrimaryTable().getIndexes().add(indexDefinition);
        } else if (m_table.equals(descriptor.getPrimaryTable().getQualifiedName()) || m_table.equals(descriptor.getPrimaryTableName())) {
            indexDefinition.setTargetTable(m_table);
            descriptor.getPrimaryTable().getIndexes().add(indexDefinition);
        } else {
            indexDefinition.setTargetTable(m_table);
            boolean found = false;
            for (DatabaseTable databaseTable : descriptor.getClassDescriptor().getTables()) {
                if (m_table.equals(databaseTable.getQualifiedName()) || m_table.equals(databaseTable.getName())) {
                    databaseTable.getIndexes().add(indexDefinition);
                    found = true;
                }
            }
            
            if (!found) {
                descriptor.getPrimaryTable().getIndexes().add(indexDefinition);
            }
        }
    }

    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setUnique(Boolean unique) {
        this.m_unique = unique;
    }

    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setColumnNames(List<String> columnNames) {
        this.m_columnNames = columnNames;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setCatalog(String catalog) {
        this.m_catalog = catalog;
    }

    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setName(String name) {
        this.m_name = name;
    }

    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setSchema(String schema) {
        this.m_schema = schema;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setTable(String table) {
        this.m_table = table;
    }
}
