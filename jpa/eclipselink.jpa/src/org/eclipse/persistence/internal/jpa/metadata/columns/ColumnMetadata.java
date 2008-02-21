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

import javax.persistence.Column;

import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * Object to hold onto column metadata in a TopLink database field.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class ColumnMetadata  {
    private Boolean m_unique;
    private Boolean m_nullable;
    private Boolean m_updatable;
    private Boolean m_insertable;

    private DatabaseField m_databaseField;
    
    private Integer m_scale;
    private Integer m_length;
    private Integer m_precision;

    private String m_attributeName;
    private String m_name;
    private String m_table;
    private String m_columnDefinition;    
    
    /**
     * INTERNAL:
     */
    public ColumnMetadata() {}
    
    /**
     * INTERNAL:
     */
    public ColumnMetadata(String attributeName) {
        m_attributeName = attributeName;
    }
    
    /**
     * INTERNAL:
     */
    public ColumnMetadata(Column column) {
        this(column, "");
    }
    
    /**
     * INTERNAL:
     */
    public ColumnMetadata(Column column, String attributeName) {
        this(attributeName);
        
        if (column != null) {
            // Apply the values from the column annotation.
            setUnique(column.unique());
            setNullable(column.nullable());
            setUpdatable(column.updatable());
            setInsertable(column.insertable());
        
            setScale(column.scale());
            setLength(column.length());
            setPrecision(column.precision());
        
            setName(column.name());
            setTable(column.table());
            setColumnDefinition(column.columnDefinition());
        }
    }
    
    /**
     * INTERNAL:
     */
    public String getAttributeName() {
        return m_attributeName;
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
     */
    public DatabaseField getDatabaseField() {
    	if (m_databaseField == null) {
        	// Initialize the DatabaseField with values and defaults.
        	m_databaseField = new DatabaseField();
        	
        	m_databaseField.setUnique(m_unique == null ? false : m_unique.booleanValue());
        	m_databaseField.setNullable(m_nullable == null ? true : m_nullable.booleanValue());
        	m_databaseField.setUpdatable(m_updatable == null ? true : m_updatable.booleanValue());
        	m_databaseField.setInsertable(m_insertable == null ? true : m_insertable.booleanValue());
            
        	m_databaseField.setScale(m_scale == null ? 0 : m_scale.intValue());
        	m_databaseField.setLength(m_length == null ? 255 : m_length.intValue());
        	m_databaseField.setPrecision(m_precision == null ? 0 : m_precision.intValue());
            
        	m_databaseField.setName(m_name == null ? "" : m_name);
        	m_databaseField.setTableName(m_table == null ? "" : m_table);
        	m_databaseField.setColumnDefinition(m_columnDefinition == null ? "" : m_columnDefinition);
    	}
    	
        return m_databaseField;
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
    public Integer getLength() {
    	return m_length;
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
    public Integer getPrecision() {
    	return m_precision;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getScale() {
    	return m_scale;
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
     * Used for OX mapping.
     */
    public Boolean getUpdatable() {
    	return m_updatable;
    }
    
    /**
     * INTERNAL:
     */
    public String getUpperCaseAttributeName() {
        return m_attributeName.toUpperCase();
    }
    
    /**
     * INTERNAL:
     */
    public void setAttributeName(String attributeName) {
        m_attributeName = attributeName;
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
     * This method will get called if we have an attribute override that
     * overrides another attribute override. See EmbeddedAccessor.
     */
    public void setDatabaseField(DatabaseField databaseField) {
        m_databaseField = databaseField;
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
    public void setLength(Integer length) {
    	m_length = length;
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
    public void setPrecision(Integer precision) {
    	m_precision = precision;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setScale(Integer scale) {
    	m_scale = scale;
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
