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
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * Object to hold onto join column metadata in an EclipseLink database field.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class JoinColumnMetadata {    
    private Boolean m_unique;
    private Boolean m_nullable;
    private Boolean m_updatable;
    private Boolean m_insertable;
    
    private String m_name;
    private String m_table;
    private String m_columnDefinition;
    private String m_referencedColumnName;
    
    /**
     * INTERNAL:
     */
    public JoinColumnMetadata() {}
    
    /**
     * INTERNAL:
     */
    public JoinColumnMetadata(Object joinColumn) {
        if (joinColumn != null) {
            // Process the primary key field metadata.
            setReferencedColumnName((String)MetadataHelper.invokeMethod("referencedColumnName", joinColumn));
        
            // Process the foreign key field metadata.
            setName((String)MetadataHelper.invokeMethod("name", joinColumn));
            setTable((String)MetadataHelper.invokeMethod("table", joinColumn));
            setUnique((Boolean)MetadataHelper.invokeMethod("unique", joinColumn));
            setNullable((Boolean)MetadataHelper.invokeMethod("nullable", joinColumn));
            setUpdatable((Boolean)MetadataHelper.invokeMethod("updatable", joinColumn));
            setInsertable((Boolean)MetadataHelper.invokeMethod("insertable", joinColumn));
            setColumnDefinition((String)MetadataHelper.invokeMethod("columnDefinition", joinColumn));
        }
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getForeignKeyField() {
    	// Initialize the DatabaseField with values and defaults.
    	DatabaseField fkField = new DatabaseField();
    	
    	fkField.setUnique(m_unique == null ? false : m_unique.booleanValue());
    	fkField.setNullable(m_nullable == null ? true : m_nullable.booleanValue());
    	fkField.setUpdatable(m_updatable == null ? true : m_updatable.booleanValue());
    	fkField.setInsertable(m_insertable == null ? true : m_insertable.booleanValue());
        
    	fkField.setName(m_name == null ? "" : m_name);
    	fkField.setTableName(m_table == null ? "" : m_table);
    	fkField.setColumnDefinition(m_columnDefinition == null ? "" : m_columnDefinition);
    	
    	return fkField;
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getPrimaryKeyField() {
    	// Initialize the DatabaseField with values and defaults.
    	DatabaseField pkField = new DatabaseField();
        
    	pkField.setName(m_referencedColumnName == null ? "" : m_referencedColumnName);
    	
        return pkField;
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
    public Boolean getInsertable() {
    	return m_insertable;	
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
    public Boolean getNullable() {
    	return m_nullable;	
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
     * USed for OX mapping.
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
     * Used for OX mapping.
     */
    public Boolean getUpdatable() {
    	return m_updatable;	
    }
    
    /**
     * INTERNAL:
     */
    public boolean isForeignKeyFieldNotSpecified() {
    	return m_name == null || m_name.equals("");
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
    public void setColumnDefinition(String columnDefinition) {
    	m_columnDefinition = columnDefinition;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setInsertable(Boolean insertable) {
    	m_insertable = insertable;	
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
    public void setNullable(Boolean nullable) {
    	m_nullable = nullable;	
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReferencedColumnName(String referencedColumnName) {
    	m_referencedColumnName = referencedColumnName;
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
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setUpdatable(Boolean updatable) {
    	m_updatable = updatable;	
    }
}
