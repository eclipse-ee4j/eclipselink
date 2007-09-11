/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import javax.persistence.JoinColumn;

import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * Object to hold onto join column metadata in a TopLink database fields.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataJoinColumn {
    protected DatabaseField m_pkField;
    protected DatabaseField m_fkField;
    
    public static final String DEFAULT_NAME = "";
    public static final String DEFAULT_TABLE = "";
    public static final String DEFAULT_COLUMN_DEFINITION = "";
    public static final String DEFAULT_REFERENCED_COLUMN_NAME = "";
    
    public static final boolean DEFAULT_UNIQUE = false;
    public static final boolean DEFAULT_NULLABLE = true;
    public static final boolean DEFAULT_UPDATABLE = true;
    public static final boolean DEFAULT_INSERTABLE = true;
    
    /**
     * INTERNAL:
     */
    public MetadataJoinColumn() {
        this(DEFAULT_REFERENCED_COLUMN_NAME, DEFAULT_NAME);
    }
    
    /**
     * INTERNAL:
     * Called for association override.
     */
    public MetadataJoinColumn(JoinColumn joinColumn) {
        this();
        
        if (joinColumn != null) {
            // Process the primary key field metadata.
            m_pkField.setName(joinColumn.referencedColumnName());
        
            // Process the foreign key field metadata.
            m_fkField.setName(joinColumn.name());
            m_fkField.setTableName(joinColumn.table());
            m_fkField.setUnique(joinColumn.unique());
            m_fkField.setNullable(joinColumn.nullable());
            m_fkField.setUpdatable(joinColumn.updatable());
            m_fkField.setInsertable(joinColumn.insertable());
            m_fkField.setColumnDefinition(joinColumn.columnDefinition());
        }
    }
    
    /**
     * INTERNAL:
     */
    public MetadataJoinColumn(String defaultName) {
        this(defaultName, defaultName);
    }
    
    /**
     * INTERNAL:
     */
    protected MetadataJoinColumn(String defaultReferenceColumnName, String defaultName) {
        m_pkField = new DatabaseField();
        m_pkField.setName(defaultReferenceColumnName);
        
        m_fkField = new DatabaseField();
        m_fkField.setName(defaultName);
        m_fkField.setTableName(DEFAULT_TABLE);
        m_fkField.setUnique(DEFAULT_UNIQUE);
        m_fkField.setNullable(DEFAULT_NULLABLE);
        m_fkField.setUpdatable(DEFAULT_UPDATABLE);
        m_fkField.setInsertable(DEFAULT_INSERTABLE);
        m_fkField.setColumnDefinition(DEFAULT_COLUMN_DEFINITION);
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getForeignKeyField() {
        return m_fkField;
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getPrimaryKeyField() {
        return m_pkField;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isForeignKeyFieldNotSpecified() {
        return m_fkField.getName().equals("");
    }
    
    /**
     * INTERNAL:
     */
    public boolean isPrimaryKeyFieldNotSpecified() {
        return m_pkField.getName().equals("");
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return false;
    }
}
