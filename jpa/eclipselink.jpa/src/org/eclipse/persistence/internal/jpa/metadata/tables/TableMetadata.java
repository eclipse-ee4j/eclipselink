/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.helper.DatabaseTable;

/**
 * INTERNAL:
 * Object to hold onto table metadata in a TopLink database table.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class TableMetadata {	
	private DatabaseTable m_databaseTable;
	private List<UniqueConstraintMetadata> m_uniqueConstraints;
	private String m_location;
	private String m_name;
    private String m_schema;
    private String m_catalog;
    
    /**
     * INTERNAL:
     */
    public TableMetadata() {
    	m_databaseTable = new DatabaseTable();
    }
    
    /**
     * INTERNAL:
     */
    public TableMetadata(String entityClassName) {
    	this();
    	m_location = entityClassName;
    }

    /**
     * INTERNAL:
     */
    public TableMetadata(Table table, String entityClassName) {
    	this(entityClassName);
    	
        if (table != null) {
        	m_name = table.name();
            m_schema = table.schema();
            m_catalog = table.catalog();
            setUniqueConstraints(table.uniqueConstraints());
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCatalog() {
        return m_catalog;
    }
    
    /**
     * INTERNAL: (Overridden in MetadataSecondaryTable, MetadataJoinTable,
     * MetadataCollectionTable and MetadataTableGenerator)
     */
    public String getCatalogContext() {
        return MetadataLogger.TABLE_CATALOG;
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseTable getDatabaseTable() {
        return m_databaseTable;
    }
    
    /**
     * INTERNAL:
     */
    public String getLocation() {
        return m_location;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL: (Overridden in MetadataSecondaryTable, MetadataJoinTable,
     * MetadataCollectionTable and MetadataTableGenerator)
     */
    public String getNameContext() {
        return MetadataLogger.TABLE_NAME;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getSchema() {
        return m_schema;
    }
    
    /**
     * INTERNAL: (Overridden in MetadataSecondaryTable, MetadataJoinTable,
     * MetadataCollectionTable and MetadataTableGenerator)
     */
    public String getSchemaContext() {
        return MetadataLogger.TABLE_SCHEMA;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<UniqueConstraintMetadata> getUniqueConstraints() {
    	return m_uniqueConstraints;
    }
    
    /**
     * INTERNAL:
     * Add the unique constraints to the database table.
     */
    public void processUniqueConstraints() {
    	if (m_uniqueConstraints != null) {
    		for (UniqueConstraintMetadata uniqueConstraint : m_uniqueConstraints) {
    			m_databaseTable.addUniqueConstraints(uniqueConstraint.getColumnNames());
    		}
    	}
    }
    
    /**
     * INTERNAL:
     */
    public void setFullyQualifiedTableName(String fullyQualifiedTableName) {
        m_databaseTable.setPossiblyQualifiedName(fullyQualifiedTableName);  
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
     */
    public void setDatabaseTable(DatabaseTable databaseTable) {
        m_databaseTable = databaseTable;
    }
    
    /**
     * INTERNAL:
     */
    public void setLocation(String location) {
        m_location = location;
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
     * Called from annotation population.
     */
    protected void setUniqueConstraints(UniqueConstraint[] uniqueConstraints) {
    	m_uniqueConstraints = new ArrayList<UniqueConstraintMetadata>();
    	
    	for (UniqueConstraint uniqueConstraint : uniqueConstraints) {
    		m_uniqueConstraints.add(new UniqueConstraintMetadata(uniqueConstraint));
    	}
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setUniqueConstraints(List<UniqueConstraintMetadata> uniqueConstraints) {
    	m_uniqueConstraints = uniqueConstraints;
    }
}
