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

import javax.persistence.PrimaryKeyJoinColumn;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;

/**
 * Object to hold onto join column metadata in a TopLink database fields.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataPrimaryKeyJoinColumn {
    protected DatabaseField m_pkField;
    protected DatabaseField m_fkField;
    
    public static final String DEFAULT_NAME = "";
    public static final String DEFAULT_COLUMN_DEFINITION = "";
    public static final String DEFAULT_REFERENCED_COLUMN_NAME = "";
    
    /**
     * INTERNAL:
     * Called for association override.
     */
    public MetadataPrimaryKeyJoinColumn(PrimaryKeyJoinColumn primaryKeyJoinColumn, DatabaseTable sourceTable, DatabaseTable targetTable) {
        this(sourceTable, targetTable);
        
        if (primaryKeyJoinColumn != null) {
            // Process the primary key field metadata.
            m_pkField.setName(primaryKeyJoinColumn.referencedColumnName());
        
            // Process the foreign key field metadata.
            m_fkField.setName(primaryKeyJoinColumn.name());
            m_fkField.setColumnDefinition(primaryKeyJoinColumn.columnDefinition());
        }
    }
    
    /**
     * INTERNAL:
     */
    public MetadataPrimaryKeyJoinColumn(DatabaseTable sourceTable, DatabaseTable targetTable) {
        this(sourceTable, targetTable, DEFAULT_REFERENCED_COLUMN_NAME, DEFAULT_NAME);
    }
    
    /**
     * INTERNAL:
     */
    public MetadataPrimaryKeyJoinColumn(DatabaseTable sourceTable, DatabaseTable targetTable, String defaultFieldName) {
        this(sourceTable, targetTable, defaultFieldName, defaultFieldName);
    }
    
    /**
     * INTERNAL:
     */
    protected MetadataPrimaryKeyJoinColumn(DatabaseTable sourceTable, DatabaseTable targetTable, String defaultPKFieldName, String defaultFKFieldName) {
        m_pkField = new DatabaseField();
        m_pkField.setName(defaultPKFieldName);
        m_pkField.setTable(sourceTable);
        
        m_fkField = new DatabaseField();
        m_fkField.setName(defaultFKFieldName);
        m_fkField.setTable(targetTable);
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
